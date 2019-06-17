package ru.mediaserver.service.fileservice.util;

import ru.mediaserver.service.fileservice.model.FileType;

import java.util.HashMap;
import java.util.Map;

import static ru.mediaserver.service.fileservice.model.FileType.*;

public class ExtensionUtil {

    private static Map<String, FileType> extensions = new HashMap<>();

    static {
        extensions.putAll(Map.of(
                "png", IMAGE,
                "jpg", IMAGE,
                "bmp", IMAGE,
                "gif", IMAGE));

        extensions.putAll(Map.of(
                "exe", EXEC,
                "jar", EXEC
        ));

        extensions.putAll(Map.of(
                "txt", TEXT
        ));

        extensions.putAll(Map.of(
                "bat", SCRIPT,
                "cmd", SCRIPT,
                "sh", SCRIPT,
                "js", SCRIPT
        ));

        extensions.putAll(Map.of(
                "png", IMAGE,
                "jpg", IMAGE,
                "bmp", IMAGE,
                "gif", IMAGE,
                "txt", TEXT,
                "bat", SCRIPT,
                "cmd", SCRIPT,
                "sh", SCRIPT,
                "js", SCRIPT
        ));

        extensions.putAll(Map.of(
                "zip", ARCHIVE,
                "bz2", ARCHIVE,
                "tbz2", ARCHIVE,
                "tar", ARCHIVE,
                "rar", ARCHIVE,
                "7z", ARCHIVE));
    }

    public static FileType getTypeOfExtension(String extension){
        return extensions.get(extension) != null ? extensions.get(extension) : NONE;
    }
}