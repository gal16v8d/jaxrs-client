package co.com.gsdd.jaxrs.client;

public interface ProxyClient {

    /**
     * Gets a proxy client based on resteasy proxy.
     * 
     * @param baseUri
     * @param clazz
     *            kind of client to get.
     * @return the client instance.
     */
    <T> T getProxy(String baseUri, Class<T> clazz);

    /**
     * Allows to close the client on demand. Should be used at finally block for ensure the client connection is closed.
     */
    void closeClient();
}
