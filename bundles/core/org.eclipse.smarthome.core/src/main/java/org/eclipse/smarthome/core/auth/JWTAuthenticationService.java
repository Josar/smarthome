package org.eclipse.smarthome.core.auth;

import java.util.UUID;

import org.apache.commons.net.util.Base64;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Use this jwt-token
 * eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiYW1pciJ9.-oAc68PBtr4dwBI-Z5K80kRWq2aCxyp1Fnksc72-Czds30iczZYFR63kK15PFnNbKeZRBQsbDufQ7juTHvUEeQ
 * for debugging
 *
 * @author Mohamadreza Amir Khostevan
 *
 */
public class JWTAuthenticationService {

    private static String key = "beduinokey";

    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    public static void register(UsernamePasswordCredentials credentials) throws AuthenticationException {
        ConfigurationFileHandler.register(credentials);
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

    public static boolean authenticate(String authorizationHeader) {
        try {
            String token = authorizationHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String base64Key = Base64.encodeBase64String(key.getBytes());
            Jwts.parser().setSigningKey(base64Key).parseClaimsJws(token).getBody();
            return true;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }
}
