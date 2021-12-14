package com.gsdd.jaxrs.client.impl;

import java.net.URI;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.gsdd.jaxrs.client.builder.RestCallParams;
import com.gsdd.jaxrs.client.builder.RestCallParams.RestCallBuilder;
import com.gsdd.jaxrs.client.exception.HTTPClientException;

class JaxClientTest {

  private static final String CLIENT_RESPONSE = "Client response";
  private static final String ERROR = "Error";
  private static final URI LOCAL_PATH = URI.create("http://localhost:1080/path");
  private static final URI LOCAL_DYNAMIC_PATH = LOCAL_PATH.resolve("1111");
  private static final String APPLICATION_JSON = "application/json";

  @Spy
  @InjectMocks
  private JaxClient jaxClientSpy;

  @Mock
  private Client wsClientMock;

  @Mock
  private WebTarget webTargetMock;

  @Mock
  private Invocation.Builder builderMock;

  @Mock
  private Invocation invocation;

  @Mock
  private Response response;

  @Mock
  private StatusType responseStatus;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Mockito.doReturn(wsClientMock).when(jaxClientSpy).getClient();
    Mockito.doReturn(wsClientMock).when(wsClientMock).property(Mockito.anyString(), Mockito.any());
    Mockito.when(wsClientMock.target(Mockito.any(URI.class))).thenReturn(webTargetMock);
    Mockito.when(webTargetMock.request(Mockito.anyString())).thenReturn(builderMock);
  }

  @Test
  void getTest_shouldGetExistingObject_whenNoResponseType() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), null);
    Mockito.when(builderMock.get()).thenReturn(response);
    Response response = jaxClientSpy.invokeGet(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString())
        .errorMsg(ERROR).acceptedMimeType(APPLICATION_JSON).build());
    Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void getTest_shouldGetAndRetrieveExistingObject_whenNoResponseType() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.get()).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.get(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).user("admin").pass(null).build(),
        ServiceResponse.class);
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void getTest_shouldGetAndRetrieveExistingObjectGeneric_whenNoResponseType() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.get()).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.get(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).user("admin").pass(null).build(),
        new GenericType<>(ServiceResponse.class));
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void getTest_shouldGetAndNoReturnEntity() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.get()).thenReturn(response);
    jaxClientSpy.get(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).user("admin").pass(null)
        .errorMsg(ERROR).build());
    Mockito.verify(jaxClientSpy).callEndpointAndGetEntity(Mockito.eq(response),
        Mockito.any(RestCallParams.class), Mockito.eq(null));
    Mockito.verify(response).close();
  }

  @Test
  void getTest_shouldGetAndRetrieveExistingObject_whenUnauthorized() throws Exception {
    arrangeResponseMock(Status.UNAUTHORIZED.getStatusCode(), null);
    Mockito.when(builderMock.get()).thenReturn(response);
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class, () -> jaxClientSpy
        .get(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).build(), ServiceResponse.class));
    Assertions.assertEquals("Service responses with a status: 401 reason: Error", e.getMessage());
  }

  @Test
  void getTest_shouldGetAndRetrieveExistingObject_whenNoClass() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), null);
    Mockito.when(builderMock.get()).thenReturn(response);
    Class<?> clazz = null;
    Object actual = jaxClientSpy.get(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).user("admin").pass("admin").build(),
        clazz);
    Assertions.assertNull(actual);
  }

  @Test
  void getTestShouldLaunchHttpClientExcWhenInvokeFailTest() {
    Mockito.when(builderMock.get()).thenThrow(new ProcessingException(ERROR));
    Class<?> clazz = null;
    HTTPClientException e =
        Assertions.assertThrows(HTTPClientException.class,
            () -> jaxClientSpy.get(
                new RestCallBuilder(LOCAL_PATH.toString()).user("admin").pass("admin").build(),
                clazz));
    Assertions.assertEquals(
        "Error when execute GET method for target -> 'http://localhost:1080/path' . Error",
        e.getMessage());
  }

  @Test
  void postTest_shouldCreateObject_whenInformationIsValid() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Response serverResponse =
        arrangeResponse(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.post(Mockito.any())).thenReturn(serverResponse);
    Response response = jaxClientSpy.invokePost(new RestCallBuilder(LOCAL_PATH.toString())
        .errorMsg(ERROR).payload(serviceContentResponse).build());
    Assertions.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    Assertions.assertNotNull(response.getEntity());
  }

  @Test
  void postTest_shouldCreateAndRetrieveObject_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.post(Mockito.any())).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.post(
        new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
        ServiceResponse.class);
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void postTest_shouldCreateAndRetrieveObjectGeneric_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.post(Mockito.any())).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.post(
        new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
        new GenericType<>(ServiceResponse.class));
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void postTest_shouldPostAndNoReturnEntity() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.OK.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.post(Mockito.any())).thenReturn(response);
    jaxClientSpy.post(new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse)
        .errorMsg(ERROR).build());
    Mockito.verify(jaxClientSpy).callEndpointAndGetEntity(Mockito.eq(response),
        Mockito.any(RestCallParams.class), Mockito.eq(null));
    Mockito.verify(response).close();
  }

  @Test
  void postTest_shouldNotCreateObject_whenWrongInformation() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.INTERNAL_SERVER_ERROR.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.post(Mockito.any())).thenReturn(response);
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.post(
            new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
            ServiceResponse.class));
    Assertions.assertEquals("Service responses with a status: 500 reason: Error", e.getMessage());
  }

  @Test
  void postTestShouldLaunchHttpClientExcWhenInvokeFailTest() {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Mockito.when(builderMock.post(Mockito.any())).thenThrow(new ProcessingException(ERROR));
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.post(
            new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
            ServiceResponse.class));
    Assertions.assertEquals(
        "Error when execute POST method for target -> 'http://localhost:1080/path' . Error",
        e.getMessage());
  }

  @Test
  void putTest_shouldUpdateObject_whenInformationIsValid() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Response serverResponse = arrangeResponse(Status.OK.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.put(Mockito.any())).thenReturn(serverResponse);
    Response response = jaxClientSpy.invokePut(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString())
        .errorMsg(ERROR).payload(serviceContentResponse).build());
    Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
    Assertions.assertNotNull(response.getEntity());
  }

  @Test
  void putTest_shouldUpdateAndRetrieveObject_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.put(Mockito.any())).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.put(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).payload(serviceContentResponse).build(),
        ServiceResponse.class);
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void putTest_shouldUpdateAndRetrieveObjectGeneric_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.put(Mockito.any())).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.put(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).payload(serviceContentResponse).build(),
        new GenericType<>(ServiceResponse.class));
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void putTest_shouldPutAndNoReturnEntity() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.OK.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.put(Mockito.any())).thenReturn(response);
    jaxClientSpy.put(new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse)
        .errorMsg(ERROR).build());
    Mockito.verify(jaxClientSpy).callEndpointAndGetEntity(Mockito.eq(response),
        Mockito.any(RestCallParams.class), Mockito.eq(null));
    Mockito.verify(response).close();
  }

  @Test
  void putTest_shouldNotUpdateObject() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.INTERNAL_SERVER_ERROR.getStatusCode(), null);
    Mockito.when(builderMock.put(Mockito.any())).thenReturn(response);
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.put(
            new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
            ServiceResponse.class));
    Assertions.assertEquals("Service responses with a status: 500 reason: Error", e.getMessage());
  }

  @Test
  void putTestShouldLaunchHttpClientExcWhenInvokeFailTest() {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Mockito.when(builderMock.put(Mockito.any())).thenThrow(new ProcessingException(ERROR));
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.put(
            new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
            ServiceResponse.class));
    Assertions.assertEquals(
        "Error when execute PUT method for target -> 'http://localhost:1080/path' . Error",
        e.getMessage());
  }

  @Test
  void patchTest_shouldUpdateObject_whenInformationIsValid() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Response serverResponse = arrangeResponse(Status.OK.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenReturn(serverResponse);
    Response response = jaxClientSpy.invokePatch(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString())
        .errorMsg(ERROR).payload(serviceContentResponse).build());
    Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
    Assertions.assertNotNull(response.getEntity());
  }

  @Test
  void patchTest_shouldUpdateAndRetrieveObject_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.patch(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).payload(serviceContentResponse).build(),
        ServiceResponse.class);
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void patchTest_shouldUpdateAndRetrieveObjectGeneric_whenNoResponseType() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.CREATED.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy.patch(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).payload(serviceContentResponse).build(),
        new GenericType<>(ServiceResponse.class));
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void patchTest_shouldPatchAndNoReturnEntity() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.OK.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenReturn(response);
    jaxClientSpy.patch(new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse)
        .errorMsg(ERROR).build());
    Mockito.verify(jaxClientSpy).callEndpointAndGetEntity(Mockito.eq(response),
        Mockito.any(RestCallParams.class), Mockito.eq(null));
    Mockito.verify(response).close();
  }

  @Test
  void patchTest_shouldNotUpdateObject() throws Exception {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    arrangeResponseMock(Status.INTERNAL_SERVER_ERROR.getStatusCode(), serviceContentResponse);
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenReturn(response);
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.patch(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString())
            .payload(serviceContentResponse).build(), ServiceResponse.class));
    Assertions.assertEquals("Service responses with a status: 500 reason: Error", e.getMessage());
  }

  @Test
  void patchTestShouldLaunchHttpClientExcWhenInvokeFailTest() {
    ServiceResponse serviceContentResponse = createServiceContentResponse();
    Mockito.when(builderMock.build(Mockito.anyString(), Mockito.any())).thenReturn(invocation);
    Mockito.when(invocation.invoke()).thenThrow(new ProcessingException(ERROR));
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.patch(
            new RestCallBuilder(LOCAL_PATH.toString()).payload(serviceContentResponse).build(),
            ServiceResponse.class));
    Assertions.assertEquals(
        "Error when execute PATCH method for target -> 'http://localhost:1080/path' . Error",
        e.getMessage());
  }

  @Test
  void deleteTest_shouldNotDeleteObject_whenInformationIsNotValid() throws Exception {
    Response serverResponse = arrangeResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(), null);
    Mockito.when(builderMock.delete()).thenReturn(serverResponse);
    Response response = jaxClientSpy
        .invokeDelete(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).errorMsg(ERROR).build());
    Assertions.assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    Assertions.assertNull(response.getEntity());
  }

  @Test
  void deleteTest_shouldDeleteAndRetrieveObject() throws Exception {
    arrangeResponseMock(Status.CREATED.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.delete()).thenReturn(response);
    ServiceResponse serviceResponse = jaxClientSpy
        .delete(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).build(), ServiceResponse.class);
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void deleteTest_shouldDeleteAndRetrieveObjectGeneric() throws Exception {
    arrangeResponseMock(Status.CREATED.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.delete()).thenReturn(response);
    ServiceResponse serviceResponse =
        jaxClientSpy.delete(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).build(),
            new GenericType<>(ServiceResponse.class));
    assertServiceContentResponse(serviceResponse);
    Mockito.verify(response).close();
  }

  @Test
  void deleteTest_shouldDeleteObject_whenNoResponseType() throws Exception {
    arrangeResponseMock(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
        createServiceContentResponse());
    Mockito.when(builderMock.delete()).thenReturn(response);
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class, () -> jaxClientSpy
        .delete(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).build(), ServiceResponse.class));
    Assertions.assertEquals("Service responses with a status: 500 reason: Error", e.getMessage());
  }

  @Test
  void deleteTest_shouldDeleteAndNoReturnEntity() throws Exception {
    arrangeResponseMock(Status.OK.getStatusCode(), createServiceContentResponse());
    Mockito.when(builderMock.delete()).thenReturn(response);
    jaxClientSpy.delete(new RestCallBuilder(LOCAL_PATH.toString()).errorMsg(ERROR).build());
    Mockito.verify(jaxClientSpy).callEndpointAndGetEntity(Mockito.eq(response),
        Mockito.any(RestCallParams.class), Mockito.eq(null));
    Mockito.verify(response).close();
  }

  @Test
  void deleteTestShouldLaunchHttpClientExcWhenInvokeFailTest() {
    Mockito.when(builderMock.delete()).thenThrow(new ProcessingException(ERROR));
    HTTPClientException e = Assertions.assertThrows(HTTPClientException.class, () -> jaxClientSpy
        .delete(new RestCallBuilder(LOCAL_PATH.toString()).errorMsg(ERROR).build()));
    Assertions.assertEquals(
        "Error when execute DELETE method for target -> 'http://localhost:1080/path' . Error",
        e.getMessage());
  }

  @Test
  void testGetInvocationBuilder() {
    Builder invocationBuilder = jaxClientSpy.getInvocationBuilder(
        new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString()).acceptedMimeType(APPLICATION_JSON)
            .errorMsg(ERROR).responseHandler(new DefaultResponseHandler()).build());
    Assertions.assertNotNull(invocationBuilder);
  }

  @Test
  void testGetInvocationBuilderHandlesException() {
    Mockito.when(webTargetMock.request(Mockito.anyString()))
        .thenThrow(new RuntimeException("testFail"));
    Assertions.assertThrows(HTTPClientException.class,
        () -> jaxClientSpy.getInvocationBuilder(new RestCallBuilder(LOCAL_DYNAMIC_PATH.toString())
            .acceptedMimeType(APPLICATION_JSON).errorMsg(ERROR).build()));
  }

  private void assertServiceContentResponse(ServiceResponse response) {
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getMessage());
    Assertions.assertEquals(CLIENT_RESPONSE, response.getMessage());
  }

  private void arrangeResponseMock(int statusCode, ServiceResponse serviceContentResponse) {
    Mockito.when(response.getStatus()).thenReturn(statusCode);
    Mockito.when(response.getStatusInfo()).thenReturn(responseStatus);
    Mockito.when(responseStatus.getStatusCode()).thenReturn(statusCode);
    Mockito.when(responseStatus.getReasonPhrase()).thenReturn(ERROR);
    Mockito.when(response.readEntity(new GenericType<>(ServiceResponse.class)))
        .thenReturn(serviceContentResponse);
  }

  private Response arrangeResponse(int statusCode, ServiceResponse response) {
    return Response.status(statusCode).entity(response).build();
  }

  private ServiceResponse createServiceContentResponse() {
    ServiceResponse serviceResponse = new ServiceResponse();
    serviceResponse.setMessage(CLIENT_RESPONSE);
    return serviceResponse;
  }

  private class ServiceResponse {
    private String message;

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

}
