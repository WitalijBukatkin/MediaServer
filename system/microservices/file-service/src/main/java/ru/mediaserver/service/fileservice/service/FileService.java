package ru.mediaserver.service.fileservice.service;

import ru.mediaserver.service.fileservice.model.FileProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileService {
    List<FileProperty> get(String user, String path);

    void delete(String user, String path);

    String upload(String user, String fileName, String destination, InputStream inputStream) throws IOException;

    FileProperty download(String user, String path, OutputStream outputStream) throws IOException;

    void copy(String user, String pathOf, String pathTo);

    void move(String user, String pathOf, String pathTo);

    void createDirectory(String user, String path) throws IOException;
}
