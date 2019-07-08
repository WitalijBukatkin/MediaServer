package ru.mediaserver.clients.msmobile.presentation.event;

import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;

@FunctionalInterface
public interface FileHandler {
    void handle(FileProperty property);
}