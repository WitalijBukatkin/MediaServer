package ru.mediaserver.client.msdesktop.business.files.model.converter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class URLEncoder {
    public static String encode(String decodedString) {
        if (decodedString == null) {
            return "";
        }

        try {
            return java.net.URLEncoder.encode(decodedString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
