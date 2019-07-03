package ru.mediaserver.client.msdesktop.presentation.event;

import ru.mediaserver.client.msdesktop.presentation.control.FileItem;

@FunctionalInterface
public interface DragFileHandler {
    void handle(FileItem item);
}