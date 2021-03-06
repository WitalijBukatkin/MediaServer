package ru.mediaserver.clients.msdesktop.business.files.service.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ru.mediaserver.clients.msdesktop.business.files.repository.FileRepository;
import ru.mediaserver.clients.msdesktop.business.files.service.FileService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

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
        return new Task<Void>() {
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

                        FileService.eventBarProperty.set(path + " downloaded " + downloaded / 1000 + "KB");
                    }

                    FileService.eventBarProperty.set(path + " is downloaded");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}