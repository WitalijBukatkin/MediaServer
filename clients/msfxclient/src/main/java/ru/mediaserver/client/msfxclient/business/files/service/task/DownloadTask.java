package ru.mediaserver.client.msfxclient.business.files.service.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ru.mediaserver.client.msfxclient.business.files.repository.FileRepository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import static ru.mediaserver.client.msfxclient.business.files.service.FileService.eventBarProperty;

public class DownloadTask extends Service<Void> {
    private String user;
    private String path;
    private FileRepository repository;
    private File output;

    public DownloadTask(String user, String path, FileRepository repository, File output) {
        this.user = user;
        this.path = path;
        this.repository = repository;
        this.output = output;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try(BufferedInputStream inputStream = new BufferedInputStream(repository.download(user, path));
                FileOutputStream fileOutputStream = new FileOutputStream(output)){
                    byte[] dataBuffer = new byte[1024];
                    int downloaded = 0;
                    int bytesRead;

                    while ((bytesRead = inputStream.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        downloaded += bytesRead;

                        eventBarProperty.set(path + " downloaded " + downloaded / 1000 + "KB");
                    }

                    eventBarProperty.set(path + " is downloaded");
                } catch (Throwable e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}