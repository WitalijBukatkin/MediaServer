package ru.mediaserver.service.fileservice;

import ru.mediaserver.service.fileservice.model.FileProperty;

import static ru.mediaserver.service.fileservice.model.FileType.DIRECTORY;
import static ru.mediaserver.service.fileservice.model.FileType.NONE;

public class FileTestData {
    public static final FileProperty ROOT = new FileProperty("root", "/rock64", DIRECTORY, 0);

    public static final FileProperty FILE1 = new FileProperty("file1", "/rock64/file1", NONE, 13);
    public static final FileProperty FILE2 = new FileProperty("file2", "/rock64/file2", NONE , 13);

    public static final FileProperty FOLDER1 = new FileProperty("folder1", "/rock64/folder1", DIRECTORY, 0);
    public static final FileProperty FILE3 = new FileProperty("file3", "/rock64/folder1/file3", NONE, 13);
    public static final FileProperty FILE4 = new FileProperty("file4", "/rock64/folder1/file4", NONE, 13);

    public static final String fileString = "Hello, World!";
}
