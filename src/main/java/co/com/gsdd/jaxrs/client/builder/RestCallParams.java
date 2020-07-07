package co.com.gsdd.jaxrs.client.builder;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import co.com.gsdd.jaxrs.client.HTTPResponseHandler;
import co.com.gsdd.jaxrs.client.impl.DefaultResponseHandler;
import co.com.gsdd.jaxrs.client.util.FileNameUtils;
import lombok.Getter;

@Getter
public class RestCallParams {

    private static final String ERROR_CALLING_SERVICE = "Error calling service: ";

    private final URI target;
    private final String user;
    private final String pass;
    private final String acceptedMimeType;
    private final String errorMsg;
    private final Object payload;
    private final HTTPResponseHandler responseHandler;

    public RestCallParams(RestCallBuilder builder) {
        this.target = builder.target;
        this.user = builder.user;
        this.pass = builder.pass;
        this.acceptedMimeType = builder.acceptedMimeType;
        this.errorMsg = builder.errorMsg;
        this.payload = builder.payload;
        this.responseHandler = builder.responseHandler;
    }

    /**
     * This builder class allows to send the endpoint target, auth, mediatype and even custom error message for call any
     * service.
     * 
     * @author alexander.galvis
     *
     */
    public static class RestCallBuilder {
        private final URI target;
        private String user;
        private String pass;
        private String acceptedMimeType;
        private String errorMsg;
        private Object payload;
        private HTTPResponseHandler responseHandler;

        /**
         * Notice that this method will build a new rest client using ClientBuilder.newClient() method.
         * 
         * @param target
         *            the root uri for the endpoint.
         * @see ClientBuilder#newClient()
         */
        public RestCallBuilder(String target) {
            this.target = FileNameUtils.removeUnsupportedCharactersFromPath(target);
            initDefaultFields();
        }

        private void initDefaultFields() {
            this.acceptedMimeType = MediaType.APPLICATION_JSON;
            this.errorMsg = ERROR_CALLING_SERVICE + this.target.toString();
            this.responseHandler = new DefaultResponseHandler();
        }

        /**
         * Set the username for rest call when auth is required.
         * 
         * @param user
         *            for basic auth endpoint call
         * @return builder with the username for endpoint.
         */
        public RestCallBuilder user(String user) {
            this.user = user;
            return this;
        }

        /**
         * Set the password for rest call when auth is required.
         * 
         * @param pass
         *            for basic auth endpoint call
         * @return builder with the password for endpoint.
         */
        public RestCallBuilder pass(String pass) {
            this.pass = pass;
            return this;
        }

        /**
         * Set the acceptedMimeType for rest call.
         * 
         * @param acceptedMimeType
         *            For example: {@link javax.ws.rs.core.MediaType.APPLICATION_JSON,
         *            javax.ws.rs.core.MediaType.APPLICATION_XML}
         * @return the builder instance with mime.
         */
        public RestCallBuilder acceptedMimeType(String acceptedMimeType) {
            this.acceptedMimeType = acceptedMimeType;
            return this;
        }

        /**
         * Customize the error message returned when an exception is throw calling the endpoint.
         * 
         * @param errorMsg
         *            the custom error message to return.
         * @return builder with custom error message, by default is Error calling service: your_uri.
         */
        public RestCallBuilder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        /**
         * Allows to define payload when needed.
         * 
         * @param payload
         *            the data to sent to server/service.
         * @return builder with the defined payload.
         */
        public RestCallBuilder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        /**
         * Allows to define any custom response handler for the request.
         * 
         * @param responseHandler
         *            the responseHandler for processing our response.
         * @return builder with the defined responseHandler.
         */
        public RestCallBuilder responseHandler(HTTPResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        public RestCallParams build() {
            return new RestCallParams(this);
        }
    }
}
