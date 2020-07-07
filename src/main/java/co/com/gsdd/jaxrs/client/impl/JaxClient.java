package co.com.gsdd.jaxrs.client.impl;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import co.com.gsdd.jaxrs.client.HTTPClient;
import co.com.gsdd.jaxrs.client.builder.RestCallParams;
import co.com.gsdd.jaxrs.client.exception.HTTPClientException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Default
public class JaxClient implements HTTPClient {

    private Client client;

    private static final String ERROR_EXECUTING = "Error when execute %s method for target -> '%s' . %s";

    @PreDestroy
    public void onDestroyCloseClient() {
        closeClient();
    }

    protected Response invokeGet(RestCallParams params) {
        log.debug("About to execute GET method for target {}", params.getTarget());
        try {
            return getInvocationBuilder(params).get();
        } catch (Exception e) {
            String message = String.format(ERROR_EXECUTING, "GET", params.getTarget(), e.getMessage());
            log.error(message, e);
            throw new HTTPClientException(message);
        }
    }

    @Override
    public <E> E get(RestCallParams params, Class<E> entity) {
        return callEndpointAndGetEntity(invokeGet(params), params, getGenericType(entity));
    }

    @Override
    public <E> E get(RestCallParams params, GenericType<E> entity) {
        return callEndpointAndGetEntity(invokeGet(params), params, entity);
    }

    @Override
    public void get(RestCallParams params) {
        callEndpointAndGetEntity(invokeGet(params), params, null);
    }

    protected Response invokePost(RestCallParams params) {
        log.debug("About to execute POST method for target {} with payload -> {}", params.getTarget(),
                params.getPayload());
        try {
            return getInvocationBuilder(params).post(Entity.entity(params.getPayload(), params.getAcceptedMimeType()));
        } catch (Exception e) {
            String message = String.format(ERROR_EXECUTING, "POST", params.getTarget(), e.getMessage());
            log.error(message, e);
            throw new HTTPClientException(message);
        }
    }

    @Override
    public <E> E post(RestCallParams params, Class<E> entity) {
        return callEndpointAndGetEntity(invokePost(params), params, getGenericType(entity));
    }

    @Override
    public <E> E post(RestCallParams params, GenericType<E> entity) {
        return callEndpointAndGetEntity(invokePost(params), params, entity);
    }

    @Override
    public void post(RestCallParams params) {
        callEndpointAndGetEntity(invokePost(params), params, null);
    }

    protected Response invokePut(RestCallParams params) {
        try {
            log.debug("About to execute PUT method for target {} with payload -> {}", params.getTarget(),
                    params.getPayload());
            return getInvocationBuilder(params).put(Entity.entity(params.getPayload(), params.getAcceptedMimeType()));
        } catch (Exception e) {
            String message = String.format(ERROR_EXECUTING, "PUT", params.getTarget(), e.getMessage());
            log.error(message, e);
            throw new HTTPClientException(message);
        }
    }

    @Override
    public <E> E put(RestCallParams params, Class<E> entity) {
        return callEndpointAndGetEntity(invokePut(params), params, getGenericType(entity));
    }

    @Override
    public <E> E put(RestCallParams params, GenericType<E> entity) {
        return callEndpointAndGetEntity(invokePut(params), params, entity);
    }

    @Override
    public void put(RestCallParams params) {
        callEndpointAndGetEntity(invokePut(params), params, null);
    }

    protected Response invokePatch(RestCallParams params) {
        try {
            log.debug("About to execute PATCH method for target {} with payload -> {}", params.getTarget(),
                    params.getPayload());
            return getInvocationBuilder(params)
                    .build("PATCH", Entity.entity(params.getPayload(), params.getAcceptedMimeType())).invoke();
        } catch (Exception e) {
            String message = String.format(ERROR_EXECUTING, "PATCH", params.getTarget(), e.getMessage());
            log.error(message, e);
            throw new HTTPClientException(message);
        }
    }

    @Override
    public <E> E patch(RestCallParams params, Class<E> entity) {
        return callEndpointAndGetEntity(invokePatch(params), params, getGenericType(entity));
    }

    @Override
    public <E> E patch(RestCallParams params, GenericType<E> entity) {
        return callEndpointAndGetEntity(invokePatch(params), params, entity);
    }

    @Override
    public void patch(RestCallParams params) {
        callEndpointAndGetEntity(invokePatch(params), params, null);
    }

    protected Response invokeDelete(RestCallParams params) {
        try {
            log.debug("About to execute DELETE method for target {}", params.getTarget());
            return getInvocationBuilder(params).delete();
        } catch (Exception e) {
            String message = String.format(ERROR_EXECUTING, "DELETE", params.getTarget(), e.getMessage());
            log.error(message, e);
            throw new HTTPClientException(message);
        }
    }

    @Override
    public <E> E delete(RestCallParams params, Class<E> entity) {
        return callEndpointAndGetEntity(invokeDelete(params), params, getGenericType(entity));
    }

    @Override
    public <E> E delete(RestCallParams params, GenericType<E> entity) {
        return callEndpointAndGetEntity(invokeDelete(params), params, entity);
    }

    @Override
    public void delete(RestCallParams params) {
        callEndpointAndGetEntity(invokeDelete(params), params, null);
    }

    @Override
    public void closeClient() {
        try {
            if (getClient() != null) {
                int hash = getClient().hashCode();
                log.debug("Client with hashCode {} will be closed", hash);
                getClient().close();
                log.debug("Client with hashCode {} closed", hash);
            }
        } catch (Exception e) {
            log.debug("Error at closing a JaxClient", e);
        }
    }

    private <E> GenericType<E> getGenericType(Class<E> entity) {
        return entity != null ? new GenericType<>(entity) : null;
    }

    /**
     * This can init a rest client, we can identify it with the haschCode.
     * 
     * @return the created client.
     */
    private Client initClient() {
        long starts = System.currentTimeMillis();
        Client newClient = ClientBuilder.newClient();
        log.debug("Client with hashCode {} returned in {} milliseconds", newClient.hashCode(),
                (System.currentTimeMillis() - starts));
        return newClient;
    }

    protected Client getClient() {
        return client;
    }

    protected void setClient(Client client) {
        this.client = client;
    }

    protected Invocation.Builder getInvocationBuilder(RestCallParams params) {
        long starts = System.currentTimeMillis();
        WebTarget webTarget = null;
        try {
            setClient(initClient());
            if (StringUtils.isNotBlank(params.getUser()) && StringUtils.isNotBlank(params.getPass())) {
                setClient(getClient().register(new Authenticator(params.getUser(), params.getPass())));
            }
            log.debug("Getting invocationBuilder from Client with hashcode {}", getClient().hashCode());
            webTarget = getClient().target(params.getTarget());
            return webTarget.request(params.getAcceptedMimeType());
        } catch (Exception e) {
            log.error("Failed creating webtarget for {} closing client and instantiation a new one", params.getTarget(),
                    e);
            throw new HTTPClientException("Error creating webtarget request", e);
        } finally {
            log.debug("Webtarget returned in {} milliseconds", (System.currentTimeMillis() - starts));
        }
    }

    protected <E> E callEndpointAndGetEntity(Response response, RestCallParams params, GenericType<E> entity) {
        try {
            params.getResponseHandler().processResponse(log, response);
            return entity != null ? response.readEntity(entity) : null;
        } catch (HTTPClientException mc) {
            throw mc;
        } catch (Exception e) {
            throw new HTTPClientException(params.getErrorMsg(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    log.debug("Error at closing JaxClient response", e);
                }
            }
            closeClient();
        }
    }

}
