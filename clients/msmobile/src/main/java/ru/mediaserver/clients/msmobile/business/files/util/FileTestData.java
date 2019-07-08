package ru.mediaserver.clients.msmobile.business.files.util;

import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.model.FileType;

public class FileTestData {
    public static final FileProperty ROOT = new FileProperty("root", "", FileType.DIRECTORY, 0);

    public static final FileProperty FILE1 = new FileProperty("file1", "file1", FileType.NONE, 13);
    public static final FileProperty FILE2 = new FileProperty("file2", "file2", FileType.NONE, 13);

    public static final FileProperty FOLDER1 = new FileProperty("folder1", "folder1", FileType.DIRECTORY, 0);
    public static final FileProperty FILE3 = new FileProperty("file3", "folder1/file3", FileType.NONE, 13);
    public static final FileProperty FILE4 = new FileProperty("file4", "folder1/file4", FileType.NONE, 13);

    public static final String fileString = "Hello, World!";
}
