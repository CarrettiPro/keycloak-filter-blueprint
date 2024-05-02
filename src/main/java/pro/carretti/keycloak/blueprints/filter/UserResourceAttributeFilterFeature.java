package pro.carretti.keycloak.blueprints.filter;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import org.keycloak.services.resources.admin.UserResource;

/**
 * A DynamicFeature that binds UserResourceAttributeFilter to a particular endpoint (UserResource), ignoring others.
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
@Provider
public class UserResourceAttributeFilterFeature implements DynamicFeature {

    private static final Logger LOG = Logger.getLogger(UserResourceAttributeFilterFeature.class);

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String className = resourceInfo.getResourceClass().getSimpleName();
        String methodName = resourceInfo.getResourceMethod().getName();
        LOG.tracev("configure: {0}::{1}", className, methodName);
        if (resourceInfo.getResourceClass().isAssignableFrom(UserResource.class) &&
                (methodName.equals("getUser") || methodName.equals("updateUser"))) {
            LOG.debug("Registering UserResourceAttributeFilter");
            context.register(UserResourceAttributeFilter.class, 1);
        }
    }

}
