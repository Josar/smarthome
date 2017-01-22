package org.eclipse.smarthome.io.rest.internal.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.smarthome.core.auth.JWTAuthenticationService;
import org.eclipse.smarthome.core.auth.LoginRequired;

/**
 *
 * @author Mohamadreza Amir Khostevan
 *
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestFromSecuredPath()) {
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

    private boolean requestFromSecuredPath() {
        Method resourceMethod = resourceInfo.getResourceMethod();
        return resourceMethod.getAnnotation(LoginRequired.class) != null;
    }

}
