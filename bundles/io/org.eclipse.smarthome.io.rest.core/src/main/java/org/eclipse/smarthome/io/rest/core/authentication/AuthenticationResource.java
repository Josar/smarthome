package org.eclipse.smarthome.io.rest.core.authentication;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.auth.UsernamePasswordCredentials;
import org.eclipse.smarthome.io.rest.SatisfiableRESTResource;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Amir Khostevan
 * @author Sven Höper
 */
@Path(AuthenticationResource.PATH_AUTHENTICATION)
@Api(value = AuthenticationResource.PATH_AUTHENTICATION)
public class AuthenticationResource implements SatisfiableRESTResource {

    /** The URI path to this resource */
    public static final String PATH_AUTHENTICATION = "auth";

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Register new account")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Response register(@FormParam("username") String username, @FormParam("password") String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        // UUID uuid = ConfigurationFileHandler.getUUID(credentials);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Get JSON Web Token (JWT)")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Invalid Credentials"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Response login(@ApiParam(value = "Username", required = true) @QueryParam("username") String username,
            @ApiParam(value = "Password", required = true) @QueryParam("password") String password) {
        // TODO Username und Passwort verarbeiten und passende Antwort liefern
        Key key = MacProvider.generateKey();

        String compactJws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS512, key).compact();
        System.out.println(compactJws);
        Map<String, String> json = new HashMap<String, String>();
        json.put("key", compactJws);
        return Response.ok(json).build();
    }

    private Response authenticationErrorResponse(String message) {
        return null;
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

}
