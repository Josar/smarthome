package org.eclipse.smarthome.io.rest.core.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.io.rest.SatisfiableRESTResource;

import io.swagger.annotations.ApiParam;

@Path(AuthenticationResource.PATH_AUTHENTICATION)
public class AuthenticationResource implements SatisfiableRESTResource {

    public static final String PATH_AUTHENTICATION = "auth";

    @PUT
    @Path("/login")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getAll(@HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) @ApiParam(value = "language") String language) {
        // TODO Username und Passwort verarbeiten und passende Antwort liefern

        Map<String, String> json = new HashMap<String, String>();

        return Response.ok(json).build();
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

}
