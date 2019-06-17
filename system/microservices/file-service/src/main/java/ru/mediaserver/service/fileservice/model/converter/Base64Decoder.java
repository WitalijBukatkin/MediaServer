package ru.mediaserver.service.fileservice.model.converter;

import java.util.Base64;
import java.util.Base64.Decoder;

public class Base64Decoder {
    private static final Decoder decoder = Base64.getDecoder();

    public static String decode(String encodedString){
        if(encodedString == null){
            return "";
        }
        return new String(decoder.decode(encodedString));
    }
}