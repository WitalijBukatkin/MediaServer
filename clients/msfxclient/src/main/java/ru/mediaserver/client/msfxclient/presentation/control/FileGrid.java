package ru.mediaserver.client.msfxclient.presentation.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import ru.mediaserver.client.msfxclient.business.files.model.FileProperty;
import ru.mediaserver.client.msfxclient.presentation.event.DragFileHandler;
import ru.mediaserver.client.msfxclient.presentation.event.FileHandler;

import java.util.List;

public class FileGrid extends GridPane {

    /*
        Events
     */

    private static ObjectProperty<FileItem> selectedFile = new SimpleObjectProperty<>();
    private static ObjectProperty<FileItem> pressedFile = new SimpleObjectProperty<>();

    private static ObjectProperty<FileItem> copyFile = new SimpleObjectProperty<>();
    private static ObjectProperty<FileItem> moveFile = new SimpleObjectProperty<>();
    private static ObjectProperty<FileItem> renameFile = new SimpleObjectProperty<>();

    private static ObjectProperty<FileItem> dragDetected = new SimpleObjectProperty<>();

    public static ObjectProperty<FileItem> dragDetectedProperty() {
        return dragDetected;
    }

    public static ObjectProperty<FileItem> selectedFileProperty() {
        return selectedFile;
    }
    public static ObjectProperty<FileItem> pressedFileProperty() {
        return pressedFile;
    }

    public static ObjectProperty<FileItem> copyFileProperty() {
        return copyFile;
    }
    public static ObjectProperty<FileItem> moveFileProperty() {
        return moveFile;
    }
    public static ObjectProperty<FileItem> renameFileProperty() {
        return renameFile;
    }

    public ObjectProperty<DragFileHandler> onDragDetected = new SimpleObjectProperty<>();

    private ObjectProperty<FileHandler> onCopyFile = new SimpleObjectProperty<>();
    private ObjectProperty<FileHandler> onMoveFile = new SimpleObjectProperty<>();
    private ObjectProperty<FileHandler> onRenameFile = new SimpleObjectProperty<>();

    private ObjectProperty<FileHandler> onPressed = new SimpleObjectProperty<>();

    public void setOnDragDetected(DragFileHandler onDragDetected) {
        this.onDragDetected.set(onDragDetected);
    }

    public static FileItem getSelectedFile() {
        return selectedFile.get();
    }

    public void setOnPressed(FileHandler fileHandler) {
        onPressed.set(fileHandler);
    }

    public void setOnCopyFile(FileHandler fileHandler) {
        onCopyFile.set(fileHandler);
    }

    public void setOnMoveFile(FileHandler fileHandler) {
        onMoveFile.set(fileHandler);
    }

    public void setOnRenameFile(FileHandler fileHandler) {
        onRenameFile.set(fileHandler);
    }

    public void setCopyFile(FileItem copyFile) {
        FileGrid.copyFile.set(copyFile);
    }

    public void setMoveFile(FileItem moveFile) {
        FileGrid.moveFile.set(moveFile);
    }

    public void setRenameFile(FileItem renameFile) {
        FileGrid.renameFile.set(renameFile);
    }

    /*
        Constructor
     */

    public FileGrid() {
        setWidth(590);
        setHeight(500);

        dragFilePropertyAddListener(dragDetected, onDragDetected);
        filePropertyAddListener(pressedFile, onPressed);
        filePropertyAddListener(renameFile, onRenameFile);
        filePropertyAddListener(copyFile, onCopyFile);
        filePropertyAddListener(moveFile, onMoveFile);
    }

    private void filePropertyAddListener(ObjectProperty<FileItem> fileItemObjectProperty, ObjectProperty<FileHandler> fileHandlerObjectProperty){
        fileItemObjectProperty.addListener(((observable, oldValue, newValue) -> {
            if(fileHandlerObjectProperty.get() != null && newValue != null){
                fileHandlerObjectProperty.get().handle(newValue.getFileProperty());
            }
        }));
    }

    private void dragFilePropertyAddListener(ObjectProperty<FileItem> fileItemObjectProperty, ObjectProperty<DragFileHandler> fileHandlerObjectProperty){
        fileItemObjectProperty.addListener(((observable, oldValue, newValue) -> {
            if(fileHandlerObjectProperty.get() != null && newValue != null){
                fileHandlerObjectProperty.get().handle(newValue);
            }
        }));
    }

    /*
        Add and change file on list
     */

    private static final Integer IMAGE_SIZE = 50;

    private Integer columnLast = 0;
    private Integer rowLast = 0;

    public void addAll(List<FileProperty> properties) {
        if (properties != null) {
            properties.forEach(this::add);
        }
    }

    private void add(FileProperty fileProperty) {
        FileItem item = new FileItem(fileProperty);
        GridPane.setMargin(item, new Insets(10));

        if (IMAGE_SIZE * columnLast * 2 > getWidth() - 10 * columnLast) {
            columnLast = 0;
            rowLast++;
        }

        add(item, columnLast++, rowLast);
    }

    public void clear() {
        getChildren().clear();
        selectedFile.set(null);

        columnLast = 0;
        rowLast = 0;
    }

}