package ru.mediaserver.client.msfxclient.presentation.event;

import ru.mediaserver.client.msfxclient.business.files.model.FileProperty;

@FunctionalInterface
public interface FileHandler {
    void handle(FileProperty property);
}