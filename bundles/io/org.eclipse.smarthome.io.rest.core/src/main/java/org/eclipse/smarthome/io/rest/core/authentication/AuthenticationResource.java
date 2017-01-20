package org.eclipse.smarthome.io.rest.core.authentication;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.auth.UsernamePasswordCredentials;
import org.eclipse.smarthome.io.rest.SatisfiableRESTResource;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

@Path(AuthenticationResource.PATH_AUTHENTICATION)
public class AuthenticationResource implements SatisfiableRESTResource {

    public static final String PATH_AUTHENTICATION = "auth";

    @POST
    @Path("/register")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response register(@FormParam("username") String username, @FormParam("password") String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        // UUID uuid = ConfigurationFileHandler.getUUID(credentials);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response login() {
        // TODO Username und Passwort verarbeiten und passende Antwort liefern
        Key key = MacProvider.generateKey();

        String compactJws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS512, key).compact();
        System.out.println(compactJws);
        Map<String, String> json = new HashMap<String, String>();
        // json.put("key", compactJws);
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
