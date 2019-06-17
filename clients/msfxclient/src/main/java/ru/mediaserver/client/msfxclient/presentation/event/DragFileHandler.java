package ru.mediaserver.client.msfxclient.presentation.event;

import ru.mediaserver.client.msfxclient.presentation.control.FileItem;

@FunctionalInterface
public interface DragFileHandler {
    void handle(FileItem item);
}