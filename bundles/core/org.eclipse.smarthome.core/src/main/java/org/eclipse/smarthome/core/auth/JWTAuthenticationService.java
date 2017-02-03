package org.eclipse.smarthome.core.auth;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.UUID;

import javax.ws.rs.InternalServerErrorException;

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

    private static byte[] key = initializeKey();

    private static int KEY_LENGTH = 10;

    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    private static byte[] initializeKey() {

        String[] array = { "smarthome", "distribution", "smarthome", "conf", "jwt_key.key" };

        final String absoluteFilePath = PathTools.getPathFromFileComponents(array);

        File file = new File(absoluteFilePath);

        try {

            if (file.createNewFile()) {

                return createKey(file);

            } else {

                Scanner scanner = new Scanner(file);

                try {
                    if (scanner.hasNextLine()) {
                        return scanner.nextLine().getBytes();
                    } else {
                        return createKey(file);
                    }
                } finally {
                    scanner.close();
                }

            }
        } catch (Exception e) {
            // TODO: log the error.

            throw new InternalServerErrorException();
        }
    }

    private static byte[] createKey(final File file) {

        byte[] bytes = new byte[KEY_LENGTH];

        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        try {
            FileOutputStream fileOuputStream = new FileOutputStream(file);

            try {
                fileOuputStream.write(bytes);
            } finally {
                fileOuputStream.close();
            }
        } catch (Exception e) {
            // TODO: log the error.

            throw new InternalServerErrorException();
        }

        return bytes;
    }

    public static void register(UsernamePasswordCredentials credentials) throws AuthenticationException {
        ConfigurationFileHandler.register(credentials);
    }

    public static String getToken(UsernamePasswordCredentials credentials) throws AuthenticationException {
        UUID uuid = ConfigurationFileHandler.login(credentials);
        String base64Key = Base64.encodeBase64String(key);
        JwtBuilder builder = Jwts.builder();
        builder.setSubject(uuid.toString());
        builder.claim("name", credentials.getUsername());
        String compactJws = builder.signWith(SignatureAlgorithm.HS512, base64Key).compact();
        return compactJws;
    }

    public static boolean authenticate(String authorizationHeader) {
        try {
            String token = authorizationHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String base64Key = Base64.encodeBase64String(key);
            Jwts.parser().setSigningKey(base64Key).parseClaimsJws(token).getBody();
            return true;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }
}
