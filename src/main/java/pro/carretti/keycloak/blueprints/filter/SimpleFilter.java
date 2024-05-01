package pro.carretti.keycloak.blueprints.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import org.jboss.logging.Logger;

import org.keycloak.models.KeycloakSession;

/**
 * A simple filter that just dumps the Keycloak session upon every request and response
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
@Provider
@Priority(1)
public class SimpleFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(SimpleFilter.class);

    @Context
    KeycloakSession session;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOG.debugv("request: session = {0}", session);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        LOG.debugv("response: session = {0}", session);
    }

}
