package pro.carretti.keycloak.blueprints.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import org.jboss.logging.Logger;

/**
 * A filter that tweaks the Cache-Control response header for the UserInfo endpoint, by adding "no-store"
 *
 * @author <a href="mailto:demetrio@carretti.pro">Dmitry Telegin</a>
 */
public class UserInfoCacheFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(UserInfoCacheFilter.class);

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        CacheControl cacheControl = (CacheControl) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
        if (cacheControl != null) {
            cacheControl.setNoStore(true);
            LOG.debugf("%s: %s", HttpHeaders.CACHE_CONTROL, cacheControl);
            response.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
        }
    }

}
