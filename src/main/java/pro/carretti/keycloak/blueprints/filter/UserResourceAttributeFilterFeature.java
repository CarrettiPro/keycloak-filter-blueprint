package pro.carretti.keycloak.blueprints.filter;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.keycloak.protocol.oidc.endpoints.UserInfoEndpoint;

/**
 * A DynamicFeature that binds UserInfoCacheFilter to a particular endpoint (UserInfo), ignoring others.
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
@Provider
public class UserInfoCacheFilterFeature implements DynamicFeature {

    private static final Logger LOG = Logger.getLogger(UserInfoCacheFilterFeature.class);

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        LOG.tracev("configure: {0}::{1}", resourceInfo.getResourceClass().getSimpleName(), resourceInfo.getResourceMethod().getName());
        if (resourceInfo.getResourceClass().isAssignableFrom(UserInfoEndpoint.class)) {
            LOG.debug("Registering UserInfoCacheFilter");
            context.register(UserInfoCacheFilter.class, 1);
        }
    }

}
