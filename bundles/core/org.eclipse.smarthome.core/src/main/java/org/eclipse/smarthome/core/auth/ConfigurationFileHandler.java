package org.eclipse.smarthome.core.auth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import javax.ws.rs.InternalServerErrorException;

public class ConfigurationFileHandler {

    private final static String CFG_FILE_PATH = initializeCFGFilePath();

    private static String initializeCFGFilePath() {
        final String relativePathToCFGFile = "distribution/smarthome/conf/user.cfg";

        String absolutePath = new File("").getAbsolutePath();

        return absolutePath.substring(0, absolutePath.length() - 39).concat(relativePathToCFGFile);
    }

    // e.g. 'user=password,username,uuid'
    private final static int PASSWORD_LOCATION = 0;
    private final static int USERNAME_LOCATION = 1;
    private final static int UUID_LOCATION = 2;
    private final static String SEPERATOR_USER = "=";
    private final static String SEPERATOR_DATA = ",";

    /**
     * This method registers an user and persists the given data in the cfg-file.
     *
     * @param credentials username and password for registration wrapped in UsernamePasswordCredentials.
     * @throws InternalServerErrorException if the cfg-file can't be read or written.
     * @throws AuthenticationException if the username is allready in use.
     */
    public static void register(final UsernamePasswordCredentials credentials) {

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();

        if (usernameExists(username)) {

            // TODO: log the Error.

            throw new AuthenticationException("Invalid");
        }

        UUID uuid = UUID.randomUUID();

        while (uuidExists(uuid)) {
            uuid = UUID.randomUUID();
        }

        final BufferedWriter bufferedWriter = cfgFileBufferedWriterFactory();

        try {
            bufferedWriter.write(String.format("user=%s,%s,%s", password, username, uuid));

            bufferedWriter.newLine();
        } catch (IOException e) {
            // TODO: log the Error.

            throw new InternalServerErrorException();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    // TODO: log the Error.

                    throw new InternalServerErrorException();
                }
            }
        }

    }

    /**
     * This method checks, whether the user data is persisted in the cfg-file and returns the uuid.
     *
     * @param credentials username and password for registration wrapped in UsernamePasswordCredentials.
     * @return unique uuid for the user.
     */
    public static UUID login(final UsernamePasswordCredentials credentials) {

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();

        final String[] userData = searchCfgFile(username, USERNAME_LOCATION);

        if (userData == null || !userData[PASSWORD_LOCATION].equals(password)) {

            // TODO: log the Error.

            throw new AuthenticationException("Invalid");
        }

        return UUID.fromString(userData[UUID_LOCATION]);
    }

    public static String[] getUserDataByUUID(final UUID uuid) {

        return searchCfgFile(uuid.toString(), UUID_LOCATION);
    }

    /**
     * Checks, whether the username is allready defined.
     *
     * @param username
     * @return true, if the username is allready defined in the cfg-file.
     */
    private static boolean usernameExists(final String username) {

        return searchCfgFile(username, USERNAME_LOCATION) != null;
    }

    /**
     * Checks, whether the UUID is allready in use.
     *
     * @param uuid
     * @return true, if the uuid is allready in use, false otherwise.
     */
    private static boolean uuidExists(final UUID uuid) {

        return searchCfgFile(uuid.toString(), UUID_LOCATION) != null;
    }

    /**
     * Searches for the given substring in the cfg-file, at the location, it should be found by the structure of a
     * cfg-file entry.
     *
     * @param substring The string, that should be found in the cfg-file.
     * @param locationInString which position of the data in a cfg-file entry.
     * @return The users data extracted from the cfg-file and stored in an array.
     */
    private static String[] searchCfgFile(final String substring, final int locationInString) {

        Scanner scanner = null;

        try {
            scanner = cfgFileScannerFactory();

            while (scanner.hasNextLine()) {

                final String lineFromFile = scanner.nextLine();

                final String[] userData = lineFromFile.substring(lineFromFile.indexOf(SEPERATOR_USER) + 1)
                        .split(SEPERATOR_DATA);

                if (userData[locationInString].equals(substring)) {
                    return userData;
                }
            }

            return null;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * Returns Scanner for cfg-file.
     *
     * @return Scanner for cfg-file.
     */
    private static Scanner cfgFileScannerFactory() {

        try {
            return new Scanner(new File(CFG_FILE_PATH));
        } catch (FileNotFoundException e) {

            // TODO: log the Error.

            throw new InternalServerErrorException();
        }
    }

    /**
     * Returns BufferedWriter for cfg-file.
     *
     * @return BufferedWriter for cfg-file.
     */
    private static BufferedWriter cfgFileBufferedWriterFactory() {

        try {
            return new BufferedWriter(new FileWriter(CFG_FILE_PATH, true));
        } catch (IOException e) {

            // TODO: log the Error.

            throw new InternalServerErrorException();
        }
    }

    public static void main(String[] args) {
        register(new UsernamePasswordCredentials("amir", "amir_password"));
    }
}
