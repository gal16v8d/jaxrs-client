package com.gsdd.jaxrs.client.impl;

import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import com.gsdd.jaxrs.client.HTTPResponseHandler;
import com.gsdd.jaxrs.client.exception.HTTPClientException;

@Default
public class DefaultResponseHandler implements HTTPResponseHandler {

  @Override
  public void processResponse(Logger logger, Response response) {
    if (response == null) {
      throw new HTTPClientException("Service responses is null.");
    }
    int statusCode = response.getStatus();
    logger.debug("Service responses with status -> {}", statusCode);
    if (statusCode >= Status.BAD_REQUEST.getStatusCode()) {
      String moreInfo = response.readEntity(String.class);
      String message = "Service responses with a status: "
          + response.getStatusInfo().getStatusCode() + " reason: "
          + (moreInfo != null ? moreInfo : response.getStatusInfo().getReasonPhrase());
      throw new HTTPClientException(message);
    }
  }
}
