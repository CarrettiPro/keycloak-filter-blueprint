package pro.carretti.keycloak.blueprints.filter;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jboss.logging.Logger;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.LDAPConstants;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;

/**
 * A filter that massages user representation on-the-fly, for both request and response.
 *
 * This example defines "protected" user attributes:
 * - upon GET request, the attribute values will be masked
 * - upon PUT, an attempt to modify such an attribute will be prevented
 *
 * The latter could be used as a workaround for buggy software that tries to update read-only attributes
 * with out-of-date values, which will fail with a 400 Bad Request.
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
public class UserResourceAttributeFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(UserResourceAttributeFilter.class);

    private static final Set<String> SPECIAL_ATTRS = Set.of(
            "foo", "bar"
    );

//    private static final Set<String> SPECIAL_ATTRS = Set.of(
//            LDAPConstants.MODIFY_TIMESTAMP
//    );

    private static final String MASKED = "***";

    @Context
    KeycloakSession session;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (request.getMethod().equals(HttpMethod.PUT)) {
            if (request.hasEntity()) {
                UserRepresentation user = JsonSerialization.readValue(request.getEntityStream(), UserRepresentation.class);
                Map<String, List<String>> attributes = user.getAttributes();

                // For read-only attributes, just stripping them off will work, though a WARN will be logged
//                attributes.keySet().removeAll(SPECIAL_ATTRS);

                // Alternatively, replace them with up-to-date values from Keycloak:
                RealmModel realm = session.getContext().getRealm();
                UserModel user0 = session.users().getUserById(realm, user.getId());
                Map<String, List<String>> attributes0 = new HashMap<>(user0.getAttributes());
                attributes0.keySet().retainAll(SPECIAL_ATTRS);
                attributes.putAll(attributes0);

                byte[] buf = JsonSerialization.writeValueAsBytes(user);
                request.setEntityStream(new ByteArrayInputStream(buf));
            } else {
                LOG.debug("No entity in a PUT request to UserResource, ignoring");
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (request.getMethod().equals(HttpMethod.GET)) {
            if (response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity()) {
                UserRepresentation user = (UserRepresentation) response.getEntity();
                Map<String, List<String>> attributes = user.getAttributes();
                SPECIAL_ATTRS.stream().forEach(a -> attributes.replace(a, Collections.singletonList(MASKED)));
                response.setEntity(user);
            } else {
                LOG.debug("No entity returned from a GET request to UserResource, ignoring");
            }
        }
    }

}
