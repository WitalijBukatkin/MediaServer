package ru.mediaserver.service.fileservice.repository;

import ru.mediaserver.service.fileservice.model.FileProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileRepository {

    List<FileProperty> get(String path);

    boolean delete(String path);

    /**
     * Upload file to server
     * @param path on server
     * @param inputStream file data
     * @return path to uploaded fileName, maybe null
     * @throws IOException if not writing
     */
    String upload(String path, InputStream inputStream) throws IOException;

    /**
     * Download file from server
     * @param path on server
     * @param outputStream file data
     * @return fileName, maybe null if not downloaded
     * @throws IOException if not reading
     */
    List<FileProperty> download(String path, OutputStream outputStream) throws IOException;

    boolean copy(String pathOf, String pathTo);

    boolean move(String pathOf, String pathTo);

    boolean createDirectory(String path);
}
