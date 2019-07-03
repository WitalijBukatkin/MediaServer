package ru.mediaserver.client.msdesktop.presentation.event;

import ru.mediaserver.client.msdesktop.business.files.model.FileProperty;

@FunctionalInterface
public interface FileHandler {
    void handle(FileProperty property);
}