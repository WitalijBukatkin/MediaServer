package ru.mediaserver.client.msfxclient.business.files.repository;

import ru.mediaserver.client.msfxclient.business.files.model.FileProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileRepository {
    List<FileProperty> get(String user, String path);

    boolean delete(String user, String path);

    boolean upload(String user, String path, String name, InputStream inputStream) throws IOException;

    InputStream download(String user, String path) throws IOException;

    boolean copy(String user, String pathOf, String pathTo);

    boolean move(String user, String pathOf, String pathTo);

    boolean createDirectory(String user, String path);
}