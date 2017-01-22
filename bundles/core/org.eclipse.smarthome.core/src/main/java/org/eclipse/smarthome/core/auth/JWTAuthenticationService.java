package org.eclipse.smarthome.core.auth;

import java.util.UUID;

import org.apache.commons.net.util.Base64;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationService {
    private static String key = "beduinokey";

    public static UUID register(UsernamePasswordCredentials credentials) throws AuthenticationException {
        return ConfigurationFileHandler.getUUID(credentials);
    }

    public static String getToken(UsernamePasswordCredentials credentials) throws AuthenticationException {
        UUID uuid = ConfigurationFileHandler.login(credentials);
        String base64Key = Base64.encodeBase64String(key.getBytes());
        JwtBuilder builder = Jwts.builder();
        builder.setSubject(uuid.toString());
        builder.claim("name", credentials.getUsername());
        String compactJws = builder.signWith(SignatureAlgorithm.HS512, base64Key).compact();
        return compactJws;
    }
}
