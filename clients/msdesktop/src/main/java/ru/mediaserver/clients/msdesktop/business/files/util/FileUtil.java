package ru.mediaserver.clients.msdesktop.business.files.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    public static String getNameOfPath(String path){
        return path.contains("/") ?
                path.substring(path.lastIndexOf("/") + 1) : path;
    }

    public static String getPathWithOutName(String path){
        return path.contains("/") ?
                path.substring(0, path.lastIndexOf("/")) : path;
    }

    public static byte[] getAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
