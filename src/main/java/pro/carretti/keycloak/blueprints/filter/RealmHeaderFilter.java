package pro.carretti.keycloak.blueprints.filter;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import org.jboss.logging.Logger;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

/**
 * A filter that adds custom headers for realm name and display name (if configured).
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
@Provider
@Priority(1)
public class RealmHeaderFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RealmHeaderFilter.class);
    private static final String X_REALM_ID = "X-Realm-ID";
    private static final String X_REALM_NAME = "X-Realm-Name";

    @Context
    KeycloakSession session;

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        RealmModel realm = session.getContext().getRealm();
        if (realm != null) {
            String name = realm.getName();
            addHeader(response, X_REALM_ID, name);
            String displayName = realm.getDisplayName();
            if (displayName != null) {
                addHeader(response, X_REALM_NAME, displayName);
            }
        }
    }

    private void addHeader(ContainerResponseContext response, String key, String value) {
        LOG.debugv("{0}: {1}", key, value);
        response.getHeaders().putSingle(key, value);
    }

}
