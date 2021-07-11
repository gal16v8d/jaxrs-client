package co.com.gsdd.jaxrs.client.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import co.com.gsdd.jaxrs.client.exception.HTTPClientException;

@ExtendWith(MockitoExtension.class)
class DefaultResponseHandlerTest {

    private static final String MOCKED_ERROR = "Mocked error";
    @Spy
    private DefaultResponseHandler defaultResponseHandler;
    @Mock
    private Logger logger;
    @Mock
    private Response response;
    @Mock
    private StatusType responseStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void launchExcWhenResponseIsNullTest() {
        HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
                () -> defaultResponseHandler.processResponse(logger, null));
        Assertions.assertEquals("Service responses is null.", e.getMessage());
    }

    @Test
    void mustlaunchExcWhenResponseIsBadTest() {
        Mockito.when(response.getStatus()).thenReturn(Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.when(response.getStatusInfo()).thenReturn(responseStatus);
        Mockito.when(response.readEntity(String.class)).thenReturn(MOCKED_ERROR);
        Mockito.when(responseStatus.getStatusCode()).thenReturn(Status.INTERNAL_SERVER_ERROR.getStatusCode());
        HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
                () -> defaultResponseHandler.processResponse(logger, response));
        Assertions.assertEquals("Service responses with a status: 500 reason: " + MOCKED_ERROR, e.getMessage());
    }

    @Test
    void mustlaunchExcWhenResponseIsBadCanNotReadEntityTest() {
        Mockito.when(response.getStatus()).thenReturn(Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.when(response.getStatusInfo()).thenReturn(responseStatus);
        Mockito.when(response.readEntity(String.class)).thenReturn(null);
        Mockito.when(responseStatus.getStatusCode()).thenReturn(Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.when(responseStatus.getReasonPhrase()).thenReturn(MOCKED_ERROR);
        HTTPClientException e = Assertions.assertThrows(HTTPClientException.class,
                () -> defaultResponseHandler.processResponse(logger, response));
        Assertions.assertEquals("Service responses with a status: 500 reason: " + MOCKED_ERROR, e.getMessage());
    }

    @Test
    void mustNotlaunchExcWhenResponseIsGoodTest() {
        Mockito.when(response.getStatus()).thenReturn(Status.OK.getStatusCode());
        Assertions.assertDoesNotThrow(() -> defaultResponseHandler.processResponse(logger, response));
        Mockito.verify(logger).debug(Mockito.eq("Service responses with status -> {}"), Mockito.anyInt());
        Mockito.verify(response).getStatus();
    }
}
