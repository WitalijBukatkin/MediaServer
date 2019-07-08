package ru.mediaserver.clients.msmobile.business.files.service;

import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.model.converter.URLEncoder;
import ru.mediaserver.clients.msmobile.business.files.repository.FileRepository;
import ru.mediaserver.clients.msmobile.business.files.repository.FileRepositoryImpl;
import ru.mediaserver.clients.msmobile.business.files.repository.InMemoryFileRepositoryImpl;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class FileService {
    private FileRepository repository;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private String currentDir = "";

    public String getCurrentDir() {
        return currentDir;
    }

    public FileService() {
        if (SecurityUtil.isDemo()) {
            repository = new InMemoryFileRepositoryImpl();
        } else {
            repository = new FileRepositoryImpl();
        }


        logger.info(repository.getClass().getName() + " is starting");
    }
    
    private String encodePath(String path){
        if(repository instanceof InMemoryFileRepositoryImpl){
            return path == null || path.equals("") ? "" : path;
        }
        return URLEncoder.encode(path);
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

    public void download(String user, String path, File output) throws IOException {
        logger.info("DOWNLOAD " + path);
        String encodedPath = encodePath(path);

        try (BufferedInputStream inputStream = new BufferedInputStream(repository.download(user, encodedPath));
             FileOutputStream outputStream = new FileOutputStream(output)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                outputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    public void upload(String user, File input) throws IOException {
        logger.info("UPLOAD " + currentDir);
        String encodedPath = encodePath(currentDir);

        if (!repository.upload(user, encodedPath, "photo" + LocalDateTime.now() + ".jpg", new FileInputStream(input))) {
            throw new IOException();
        }
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