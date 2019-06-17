package ru.mediaserver.service.fileservice.repository;

import ru.mediaserver.service.fileservice.model.FileProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileRepository {

    List<FileProperty> get(String user, String path);

    boolean delete(String user, String path);

    /**
     * Upload file to server
     * @param user userName
     * @param path on server
     * @param inputStream file data
     * @return path to uploaded fileName, maybe null
     * @throws IOException if not writing
     */
    String upload(String user, String path, InputStream inputStream) throws IOException;

    /**
     * Download file from server
     * @param user userName
     * @param path on server
     * @param outputStream file data
     * @return fileName, maybe null if not downloaded
     * @throws IOException if not reading
     */
    List<FileProperty> download(String user, String path, OutputStream outputStream) throws IOException;

    boolean copy(String user, String pathOf, String pathTo);

    boolean move(String user, String pathOf, String pathTo);

    boolean createDirectory(String user, String path);
}
