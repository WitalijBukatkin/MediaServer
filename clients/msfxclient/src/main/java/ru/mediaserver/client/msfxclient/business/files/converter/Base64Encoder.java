package ru.mediaserver.client.msfxclient.business.files.converter;

import java.util.Base64;

public class Base64Encoder {
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String encode(String decodedString) {
        if (decodedString == null) {
            return "";
        }
        return encoder.encodeToString(decodedString.getBytes());
    }
}
