package org.eclipse.smarthome.io.rest.internal.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.smarthome.core.auth.JWTAuthenticationService;
import org.eclipse.smarthome.core.auth.NoAuthenticationRequired;
import org.eclipse.smarthome.io.rest.JSONResponse;

import io.swagger.jaxrs.listing.ApiListingResource;

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
    private final String SWAGGER_OVERVIEW_PATH_STRING = "/doc/index.html";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestFromSecuredPath()) {
            if (!authenticatedWithCookie(requestContext)) {
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
    }

    private boolean authenticatedWithCookie(ContainerRequestContext requestContext) {
        boolean requestIsFromSwaggerOverview = requestContext.getHeaderString("referer")
                .contains(SWAGGER_OVERVIEW_PATH_STRING);
        if (requestIsFromSwaggerOverview && requestContext.getCookies().containsKey("key")) {
            Cookie jwtCookie = requestContext.getCookies().get("key");
            if (JWTAuthenticationService.authenticate(jwtCookie.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void abortWithUnauthorizedStatus(ContainerRequestContext requestContext) {
        Response response = JSONResponse.createErrorResponse(Status.UNAUTHORIZED, "Invalid user credentials");
        requestContext.abortWith(response);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean requestFromSecuredPath() {
        Class resourceClass = resourceInfo.getResourceClass();
        Method resourceMethod = resourceInfo.getResourceMethod();
        if (resourceClass == ApiListingResource.class) {
            return false;
        }
        NoAuthenticationRequired methodAnnotation = resourceMethod.getAnnotation(NoAuthenticationRequired.class);
        Annotation classAnnotation = resourceClass.getAnnotation(NoAuthenticationRequired.class);
        return classAnnotation == null && methodAnnotation == null;
    }

}
