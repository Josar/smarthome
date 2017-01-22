package org.eclipse.smarthome.io.rest.internal.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.smarthome.core.auth.JWTAuthenticationService;
import org.eclipse.smarthome.core.auth.LoginRequired;
import org.eclipse.smarthome.io.rest.JSONResponse;

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
        Response response = JSONResponse.createErrorResponse(Status.UNAUTHORIZED, "Invalid user credentials");
        requestContext.abortWith(response);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean requestFromSecuredPath() {
        Class resourceClass = resourceInfo.getResourceClass();
        Method resourceMethod = resourceInfo.getResourceMethod();
        LoginRequired methodAnnotation = resourceMethod.getAnnotation(LoginRequired.class);
        Annotation classAnnotation = resourceClass.getAnnotation(LoginRequired.class);
        return classAnnotation != null || methodAnnotation != null;
    }

}
