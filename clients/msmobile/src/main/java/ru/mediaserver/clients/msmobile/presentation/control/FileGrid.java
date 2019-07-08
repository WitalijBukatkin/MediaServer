package ru.mediaserver.clients.msmobile.presentation.control;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.presentation.event.FileHandler;

import java.util.List;

public class FileGrid extends GridPane {

    /*
        Events
     */

    private static ObjectProperty<FileItem> selectedFile = new SimpleObjectProperty<>();
    private static ObjectProperty<FileItem> pressedFile = new SimpleObjectProperty<>();

    public static ObjectProperty<FileItem> selectedFileProperty() {
        return selectedFile;
    }
    public static ObjectProperty<FileItem> pressedFileProperty() {
        return pressedFile;
    }

    private ObjectProperty<FileHandler> onPressed = new SimpleObjectProperty<>();

    public static FileItem getSelectedFile() {
        return selectedFile.get();
    }

    public void setOnPressed(FileHandler fileHandler) {
        onPressed.set(fileHandler);
    }

    private static ObjectProperty<FileProperty> copyFile = new SimpleObjectProperty<>();
    private static ObjectProperty<FileProperty> moveFile = new SimpleObjectProperty<>();

    public static FileProperty getCopyFile() {
        return copyFile.get();
    }

    public static void setCopyFile(FileProperty copyFile) {
        FileGrid.copyFile.set(copyFile);
    }

    public static FileProperty getMoveFile() {
        return moveFile.get();
    }

    public static void setMoveFile(FileProperty moveFile) {
        FileGrid.moveFile.set(moveFile);
    }

    /*
        Constructor
     */

    public void init(View view) {
        filePropertyAddListener(pressedFile, onPressed);

        setHeight(600);
        setWidth(350);

        prefHeightProperty().bind(view.heightProperty());
        prefWidthProperty().bind(view.widthProperty());
    }

    private void filePropertyAddListener(ObjectProperty<FileItem> fileItemObjectProperty, ObjectProperty<FileHandler> fileHandlerObjectProperty){
        fileItemObjectProperty.addListener(((observable, oldValue, newValue) -> {
            if(fileHandlerObjectProperty.get() != null && newValue != null){
                fileHandlerObjectProperty.get().handle(newValue.getFileProperty());
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