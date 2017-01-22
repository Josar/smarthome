package org.eclipse.smarthome.io.rest.internal.filter;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.smarthome.core.auth.JWTAuthenticationService;

/**
 *
 * @author Mohamadreza Amir Khostevan
 *
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String[] unsecuredPahts = { "swagger.json", "login", "register" };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestFromSecuredPath(requestContext)) {
            List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.size() > 0) {
                String authToken = authHeader.get(0);
                if (!JWTAuthenticationService.authenticate(authToken)) {
                    abortWithUnauthorizedStatus(requestContext);
                }
            } else {
                abortWithUnauthorizedStatus(requestContext);
            }
        }
    }

    private void abortWithUnauthorizedStatus(ContainerRequestContext requestContext) {
        Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity("Invalid user credentials")
                .build();
        requestContext.abortWith(unauthorizedStatus);
    }

    private boolean requestFromSecuredPath(ContainerRequestContext requestContext) {
        for (String path : unsecuredPahts) {
            if (requestContext.getUriInfo().getPath().contains(path)) {
                return false;
            }
        }
        return true;
    }

}
