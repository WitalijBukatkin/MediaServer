package ru.mediaserver.service.fileservice.util;

import ru.mediaserver.service.fileservice.model.FileType;

import java.util.HashMap;
import java.util.Map;

import static ru.mediaserver.service.fileservice.model.FileType.*;

public class ExtensionUtil {

    private static Map<String, FileType> extensions = new HashMap<>();

    static {
        extensions.put("png", IMAGE);
        extensions.put("jpg", IMAGE);
        extensions.put("bmp", IMAGE);
        extensions.put("gif", IMAGE);


        extensions.put("exe", EXEC);
        extensions.put("jar", EXEC);


        extensions.put("txt", TEXT);

        extensions.put("bat", SCRIPT);
        extensions.put("cmd", SCRIPT);
        extensions.put("sh", SCRIPT);
        extensions.put("js", SCRIPT);

        extensions.put("zip", ARCHIVE);
        extensions.put("bz2", ARCHIVE);
        extensions.put("tbz2", ARCHIVE);
        extensions.put("tar", ARCHIVE);
        extensions.put("rar", ARCHIVE);
        extensions.put("7z", ARCHIVE);
    }

    public static FileType getTypeOfExtension(String extension){
        return extensions.get(extension) != null ? extensions.get(extension) : NONE;
    }
}