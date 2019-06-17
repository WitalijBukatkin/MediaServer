package ru.mediaserver.client.msfxclient.business.files.util;

import ru.mediaserver.client.msfxclient.business.files.model.FileType;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static ru.mediaserver.client.msfxclient.business.files.model.FileType.*;

public class FileImageUtil {
    private static final Map<FileType, String> images = new HashMap<>();

    private static final String root = "../../../presentation/menu/files/filetypes/";

    static {
        images.put(IMAGE, "icons8-pictures_folder.png");
        images.put(EXEC, "icons8-exe.png");
        images.put(ARCHIVE, "icons8-archive_folder.png");
        images.put(MUSIC, "icons8-music_folder.png");
        images.put(NONE, "icons8-file.png");
        images.put(DIRECTORY, "icons8-opened_folder.png");
        images.put(SCRIPT, "icons8-scroll.png");
        images.put(TEXT, "icons8-red_file.png");
    }

    public static InputStream getImageOfExtension(FileType type) {
        String imagePath = root + (images.get(type) != null ? images.get(type) : images.get(NONE));
        return FileImageUtil.class.getResourceAsStream(imagePath);
    }
}