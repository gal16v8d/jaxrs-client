package co.com.gsdd.jaxrs.client.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
    private static final String UTF_8 = "UTF-8";

    private final String user;
    private final String password;

    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        final String basicAuthentication = getBasicAuthentication();
        headers.add(AUTHORIZATION, basicAuthentication);
    }

    private String getBasicAuthentication() {
        String token = this.user + COLON + this.password;
        try {
            return BASIC + DatatypeConverter.printBase64Binary(token.getBytes(UTF_8));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Cannot encode with UTF-8 the basic auth", ex);
        }
    }

}
