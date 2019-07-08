package ru.mediaserver.clients.msmobile.presentation.files.filetypes;

import ru.mediaserver.clients.msmobile.business.files.model.FileType;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileImageUtil {
    private static final Map<FileType, String> images = new HashMap<>();

    static {
        images.put(FileType.IMAGE, "icons8-pictures_folder.png");
        images.put(FileType.EXEC, "icons8-exe.png");
        images.put(FileType.ARCHIVE, "icons8-archive_folder.png");
        images.put(FileType.MUSIC, "icons8-music_folder.png");
        images.put(FileType.NONE, "icons8-file.png");
        images.put(FileType.DIRECTORY, "icons8-opened_folder.png");
        images.put(FileType.SCRIPT, "icons8-scroll.png");
        images.put(FileType.TEXT, "icons8-red_file.png");
    }

    public static InputStream getImageOfExtension(FileType type) {
        String imagePath = (images.get(type) != null ? images.get(type) : images.get(FileType.NONE));
        return FileImageUtil.class.getResourceAsStream(imagePath);
    }
}