package ru.mediaserver.clients.msdesktop.presentation.event;

import ru.mediaserver.clients.msdesktop.presentation.control.FileItem;

@FunctionalInterface
public interface DragFileHandler {
    void handle(FileItem item);
}