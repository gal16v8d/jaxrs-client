package com.gsdd.jaxrs.client.impl;

import javax.enterprise.inject.Default;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import com.gsdd.jaxrs.client.ProxyClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Default
public class ProxyClientImpl implements ProxyClient {

  private Client client;

  private Client initClient() {
    long starts = System.currentTimeMillis();
    Client client = ClientBuilder.newClient();
    log.debug("Client with hashCode {} returned in {} milliseconds", client.hashCode(),
        (System.currentTimeMillis() - starts));
    return client;
  }

  @Override
  public <T> T getProxy(String baseUri, Class<T> clazz) {
    client = initClient();
    WebTarget webTarget = getClient().target(baseUri);
    ResteasyWebTarget restEasyWebTarget = (ResteasyWebTarget) webTarget;
    return restEasyWebTarget.proxy(clazz);
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

  protected Client getClient() {
    return client;
  }

}
