package org.eclipse.smarthome.core.auth;

import java.io.File;
import java.nio.file.FileSystems;

public class PathTools {

    public static String getPathFromFileComponents(final String[] fileNameComponents) {

        final String[] absolutePath = new File("").getAbsolutePath().split(FileSystems.getDefault().getSeparator());

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < absolutePath.length - 3; i++) {
            builder.append(absolutePath[i]);
            builder.append(FileSystems.getDefault().getSeparator());
        }

        for (String s : fileNameComponents) {
            builder.append(s);
            builder.append(FileSystems.getDefault().getSeparator());
        }

        return builder.toString();
    }

}
