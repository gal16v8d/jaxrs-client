package com.gsdd.jaxrs.client.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Authenticator implements ClientRequestFilter {

  private static final String BASIC = "BASIC ";
  private static final String COLON = ":";
  private static final String AUTHORIZATION = "Authorization";

  private final String user;
  private final String password;

  public void filter(ClientRequestContext requestContext) throws IOException {
    MultivaluedMap<String, Object> headers = requestContext.getHeaders();
    final String basicAuthentication = getBasicAuthentication();
    headers.add(AUTHORIZATION, basicAuthentication);
  }

  private String getBasicAuthentication() {
    String token = this.user + COLON + this.password;
    return BASIC + DatatypeConverter.printBase64Binary(token.getBytes(StandardCharsets.UTF_8));
  }

}
