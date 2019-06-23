package ru.mediaserver.service.downloadservice.model.converter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class URLDecoder {
    public static String decode(String encodedString){
        if(encodedString == null){
            return "";
        }

        try {
            return java.net.URLDecoder.decode(encodedString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}