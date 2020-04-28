package co.com.gsdd.jaxrs.client;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;

public interface HTTPResponseHandler {

    /**
     * Handle the response to validate what to perform.
     * 
     * @param logger
     *            logs, the service status code response.
     * @param response
     *            the response from endpoint.
     */
    void processResponse(Logger logger, Response response);
}
