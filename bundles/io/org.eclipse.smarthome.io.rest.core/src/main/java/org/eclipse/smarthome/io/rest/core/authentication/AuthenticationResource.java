package org.eclipse.smarthome.io.rest.core.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.auth.User;
import org.eclipse.smarthome.core.auth.UsernamePasswordCredentials;
import org.eclipse.smarthome.io.rest.SatisfiableRESTResource;

@Path(AuthenticationResource.PATH_AUTHENTICATION)
public class AuthenticationResource implements SatisfiableRESTResource {

    public static final String PATH_AUTHENTICATION = "auth";

    @POST
    @Path("/register")
    @Produces({ MediaType.TEXT_PLAIN })
    public Response register(@FormParam("username") String username, @FormParam("password") String password,
            @FormParam("firstname") String firstname, @FormParam("lastname") String lastname) {
        User user = new User(new UsernamePasswordCredentials(username, password), firstname, lastname);
        return Response.ok(user.toString()).build();
    }

    @GET
    @Path("/test")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response test() {
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response login() {
        // TODO Username und Passwort verarbeiten und passende Antwort liefern
        // Key key = MacProvider.generateKey();

        // String compactJws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS512, key).compact();
        Map<String, String> json = new HashMap<String, String>();
        // json.put("key", compactJws);
        return Response.ok(json).build();
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

}
