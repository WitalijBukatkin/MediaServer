package ru.mediaserver.client.msfxclient.business.files.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.mediaserver.client.msfxclient.business.files.converter.Base64Encoder;
import ru.mediaserver.client.msfxclient.business.files.model.FileProperty;
import ru.mediaserver.client.msfxclient.business.files.repository.FileRepository;
import ru.mediaserver.client.msfxclient.business.files.repository.FileRepositoryImpl;
import ru.mediaserver.client.msfxclient.business.files.repository.InMemoryFileRepositoryImpl;
import ru.mediaserver.client.msfxclient.business.files.service.task.DownloadTask;
import ru.mediaserver.client.msfxclient.business.files.service.task.UploadTask;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class FileService {
    private FileRepository repository;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private String rootDir = "";
    private String currentDir = rootDir;

    public static StringProperty eventBarProperty = new SimpleStringProperty();

    public String getCurrentDir() {
        return currentDir;
    }

    public String getRootDir(){
        return rootDir;
    }

    public FileService() {
        repository = new FileRepositoryImpl();
        logger.info(repository.getClass().getName() + " is starting");
    }
    
    private String encodePath(String path){
        if(repository instanceof InMemoryFileRepositoryImpl){
            return path == null || path.equals("") ? "" : path;
        }
        return Base64Encoder.encode(path);
    }

    public List<FileProperty> get(String user, String path){
        logger.info("GET " + path);
        String encodedPath = encodePath(path);
        List<FileProperty> fileProperties = repository.get(user, encodedPath);
        currentDir = path;
        return fileProperties;
    }

    public boolean delete(String user, String path) {
        logger.info("DELETE " + path);
        String encodedPath = encodePath(path);
        return repository.delete(user, encodedPath);
    }

    public DownloadTask download(String user, String path, File output){
        logger.info("DOWNLOAD " + path);
        String encodedPath = encodePath(path);

        DownloadTask downloadTask = new DownloadTask(user, encodedPath, repository, output);
        downloadTask.start();
        return downloadTask;
    }

    public UploadTask upload(String user, File input) {
        logger.info("UPLOAD " + currentDir);
        String encodedPath = encodePath(currentDir);

        UploadTask uploadTask = new UploadTask(repository, user, encodedPath, input);
        uploadTask.start();
        return uploadTask;
    }

    public boolean createDirectory(String user, String name){
        String encodedPath = encodePath(currentDir + "/" + name);

        logger.info("CREATE DIRECTORY " + encodedPath);
        return repository.createDirectory(user, encodedPath);
    }

    public boolean copy(String user, String pathOf, String pathTo, String name){
        logger.info("COPY " + pathOf + " -> " + pathTo + "/" + name);

        String encodedPathOf = encodePath(pathOf);
        String encodedPathTo = encodePath(pathTo + "/" + name);

        return repository.copy(user, encodedPathOf, encodedPathTo);
    }

    public boolean move(String user, String pathOf, String pathTo, String name){
        logger.info("MOVE " + pathOf + " -> " + pathTo + "/" + name);

        String encodedPathOf = encodePath(pathOf);
        String encodedPathTo = encodePath(pathTo + "/" + name);

        return repository.move(user, encodedPathOf, encodedPathTo);
    }

    public boolean rename(String user, String path, String name){
        String pathWithOutName = path.contains("/") ?
                path.substring(0, path.indexOf("/")) : "";

        String newPath = pathWithOutName + "/" + name;

        String encodedPathOf = encodePath(path);
        String encodedPathTo = encodePath(newPath);

        logger.info("RENAME " + path + " -> " + newPath);
        return repository.move(user, encodedPathOf, encodedPathTo);
    }
}