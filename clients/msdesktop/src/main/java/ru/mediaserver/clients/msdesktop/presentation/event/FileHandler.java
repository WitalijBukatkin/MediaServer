package ru.mediaserver.clients.msdesktop.presentation.event;

import ru.mediaserver.clients.msdesktop.business.files.model.FileProperty;

@FunctionalInterface
public interface FileHandler {
    void handle(FileProperty property);
}