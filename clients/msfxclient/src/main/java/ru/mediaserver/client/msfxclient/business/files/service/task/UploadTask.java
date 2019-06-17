package ru.mediaserver.client.msfxclient.business.files.service.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ru.mediaserver.client.msfxclient.business.files.repository.FileRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static ru.mediaserver.client.msfxclient.business.files.service.FileService.eventBarProperty;

public class UploadTask extends Service<Void> {
    private FileRepository repository;
    private String path;
    private File input;

    public UploadTask(FileRepository repository, String path, File input) {
        this.repository = repository;
        this.path = path;
        this.input = input;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                eventBarProperty.set(input.getName() + " started upload");
                if (!repository.upload(path, input.getName(), new FileInputStream(input))) {
                    throw new IOException();
                }
                eventBarProperty.set(input.getName() + " is uploaded");
                return null;
            }
        };
    }
}