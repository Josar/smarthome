package org.eclipse.smarthome.io.rest.core.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.smarthome.core.auth.AuthenticationException;
import org.eclipse.smarthome.core.auth.JWTAuthenticationService;
import org.eclipse.smarthome.core.auth.UsernamePasswordCredentials;
import org.eclipse.smarthome.io.rest.JSONResponse;
import org.eclipse.smarthome.io.rest.SatisfiableRESTResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mohamadreza Amir Khostevan
 * @author Sven Höper
 */
@Path(AuthenticationResource.PATH_AUTHENTICATION)
@Api(value = AuthenticationResource.PATH_AUTHENTICATION)
public class AuthenticationResource implements SatisfiableRESTResource {

    /** The URI path to this resource */
    public static final String PATH_AUTHENTICATION = "auth";

    @POST
    @Path("/register")
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Register new account")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Required field is missing"),
            @ApiResponse(code = 409, message = "User already exists") })
    public Response register(@ApiParam(value = "Username", required = true) @FormParam("username") String username,
            @ApiParam(value = "Password", required = true) @FormParam("password") String password) {
        if (!requriedFieldIsMissing(username, password)) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            try {
                // JWTAuthenticationService.register(credentials);
                return Response.ok().build();
            } catch (AuthenticationException e) {
                return JSONResponse.createErrorResponse(Status.UNAUTHORIZED, e.getMessage());
            }
        }
        return JSONResponse.createErrorResponse(Status.BAD_REQUEST, "Required field is missing.");
    }

    @POST
    @Path("/login")
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Get JSON Web Token (JWT)")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Required field is missing"),
            @ApiResponse(code = 401, message = "Invalid credentials") })
    public Response login(@ApiParam(value = "Username", required = true) @FormParam("username") String username,
            @ApiParam(value = "Password", required = true) @FormParam("password") String password) {

        // JWTAuthenticationService.authenticate(
        // "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiYW1pciJ9.-oAc68PBtr4dwBI-Z5K80kRWq2aCxyp1Fnksc72-Czds30iczZYFR63kK15PFnNbKeZRBQsbDufQ7juTHvUEeQ");
        if (!requriedFieldIsMissing(username, password)) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            try {
                String token = JWTAuthenticationService.getToken(credentials);
                Map<String, String> json = new HashMap<String, String>();
                json.put("key", token);
                return Response.ok(json).build();
            } catch (AuthenticationException e) {
                return JSONResponse.createErrorResponse(Status.UNAUTHORIZED, e.getMessage());
            }
        }
        return JSONResponse.createErrorResponse(Status.BAD_REQUEST, "Required field is missing.");
    }

    private boolean requriedFieldIsMissing(String username, String password) {
        return username == null || username.isEmpty() || password == null || password.isEmpty();
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

}
