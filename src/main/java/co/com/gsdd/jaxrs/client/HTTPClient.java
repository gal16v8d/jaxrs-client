package co.com.gsdd.jaxrs.client;

import javax.ws.rs.core.GenericType;

import co.com.gsdd.jaxrs.client.builder.RestCallParams;
import co.com.gsdd.jaxrs.client.builder.RestCallParams.RestCallBuilder;
import co.com.gsdd.jaxrs.client.exception.HTTPClientException;

/**
 * Generic api for create a client and call the common endpoint types.
 * 
 * @author alexander.galvis
 *
 */
public interface HTTPClient {

    /**
     * Invokes HTTP GET for the given URI. (it properly handles the response for close it when not need it). If response
     * is null or the status code is an error code we throw a HTTPClientException
     * 
     * @param <E>
     * @param params
     *            params to send to endpoint like auth, mimetype, custom error message, etc.
     * @param entity
     *            the object that we expect as return of the call.
     * @return the given response from service.
     * @see RestCallBuilder
     * @see HTTPClientException
     */
    <E> E get(RestCallParams params, Class<E> entity);

    /**
     * Convenient method for invoke service with custom types like list of an especific class.
     * 
     * @see #get(RestCallParams, GenericType)
     */
    <E> E get(RestCallParams params, GenericType<E> entity);

    /**
     * Convenient method for invoke service and only check good response, nothing is returned.
     * 
     * @see #get(RestCallParams, GenericType)
     */
    void get(RestCallParams params);

    /**
     * Invokes HTTP POST for the given URI. (it properly handles the response for close it when not need itt). If
     * response is null or the status code is an error code we throw a HTTPClientException
     * 
     * @param params
     *            params to send to endpoint like auth, mimetype, custom error message, etc.
     * @param entity
     *            the object that we expect as return of the call.
     * @return the given response from service.
     * @see RestCallBuilder
     * @see HTTPClientException
     */
    <E> E post(RestCallParams params, Class<E> entity);

    /**
     * Convenient method for invoke service with custom types like list of an especific class.
     * 
     * @see #post(RestCallParams, GenericType)
     */
    <E> E post(RestCallParams params, GenericType<E> entity);

    /**
     * Convenient method for invoke service and only check good response, nothing is returned.
     * 
     * @see #post(RestCallParams, GenericType)
     */
    void post(RestCallParams params);

    /**
     * Invokes HTTP PUT for the given URI. (it properly handles the response for close it when not need it). If response
     * is null or the status code is an error code we throw a HTTPClientException
     * 
     * @param params
     *            params to send to endpoint like auth, mimetype, custom error message, etc.
     * @param entity
     *            the object that we expect as return of the call.
     * @return the given response from service.
     * @see RestCallBuilder
     * @see HTTPClientException
     */
    <E> E put(RestCallParams params, Class<E> entity);

    /**
     * Convenient method for invoke service with custom types like list of an especific class.
     * 
     * @see #put(RestCallParams, GenericType)
     */
    <E> E put(RestCallParams params, GenericType<E> entity);

    /**
     * Convenient method for invoke service and only check good response, nothing is returned.
     * 
     * @see #put(RestCallParams, GenericType)
     */
    void put(RestCallParams params);

    /**
     * Invokes HTTP PATCH for the given URI. (it properly handles the response for close it when not need it). If
     * response is null or the status code is an error code we throw a HTTPClientException
     * 
     * @param params
     *            params to send to endpoint like auth, mimetype, custom error message, etc.
     * @param entity
     *            the object that we expect as return of the call.
     * @return the given response from service.
     * @see RestCallBuilder
     * @see HTTPClientException
     */
    <E> E patch(RestCallParams params, Class<E> entity);

    /**
     * Convenient method for invoke service with custom types like list of an especific class.
     * 
     * @see #patch(RestCallParams, GenericType)
     */
    <E> E patch(RestCallParams params, GenericType<E> entity);

    /**
     * Convenient method for invoke service and only check good response, nothing is returned.
     * 
     * @see #patch(RestCallParams, GenericType)
     */
    void patch(RestCallParams params);

    /**
     * Invokes HTTP DELETE for the given URI. (it properly handles the response for close it when not need it). If
     * response is null or the status code is an error code we throw a HTTPClientException
     * 
     * @param params
     *            params to send to endpoint like auth, mimetype, custom error message, etc.
     * @param entity
     *            the object that we expect as return of the call.
     * @return the given response from service.
     * @see RestCallBuilder
     * @see HTTPClientException
     */
    <E> E delete(RestCallParams params, Class<E> entity);

    /**
     * Convenient method for invoke service with custom types like list of an especific class.
     * 
     * @see #delete(RestCallParams, GenericType)
     */
    <E> E delete(RestCallParams params, GenericType<E> entity);

    /**
     * Convenient method for invoke service and only check good response, nothing is returned.
     * 
     * @see #delete(RestCallParams, GenericType)
     */
    void delete(RestCallParams params);

    /**
     * Allows to close the client on demand. Should be used at finally block for ensure the client connection is closed.
     */
    void closeClient();

}
