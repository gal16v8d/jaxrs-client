package co.com.gsdd.jaxrs.client.exception;

import javax.ejb.ApplicationException;

/**
 * This exception will be thrown when we got an error with service responses.
 * 
 * @author alexander.galvis
 *
 */
@ApplicationException
public class HTTPClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HTTPClientException(String message) {
        super(message);
    }

    public HTTPClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
