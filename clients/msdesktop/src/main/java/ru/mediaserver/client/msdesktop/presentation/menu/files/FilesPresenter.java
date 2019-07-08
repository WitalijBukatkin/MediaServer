package ru.mediaserver.client.msdesktop.presentation.menu.files;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import ru.mediaserver.client.msdesktop.business.files.model.FileProperty;
import ru.mediaserver.client.msdesktop.business.files.model.FileType;
import ru.mediaserver.client.msdesktop.business.files.service.FileService;
import ru.mediaserver.client.msdesktop.business.files.service.task.DownloadTask;
import ru.mediaserver.client.msdesktop.business.files.service.task.UploadTask;
import ru.mediaserver.client.msdesktop.business.files.util.FileUtil;
import ru.mediaserver.client.msdesktop.business.util.SecurityUtil;
import ru.mediaserver.client.msdesktop.presentation.control.FileGrid;
import ru.mediaserver.client.msdesktop.presentation.event.DragFileHandler;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static ru.mediaserver.client.msdesktop.business.files.service.FileService.eventBarProperty;

public class FilesPresenter implements Initializable {

    public BorderPane pane;

    public FileGrid fileGrid;
    public Label process;
    public ButtonBar controlsBar;
    public ButtonBar copyMoveBar;

    @Inject
    private FileService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openRootDirectory();

        /*
            set update eventbar
         */
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> process.setText(eventBarProperty.get()))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        /*
            click on filegrid
         */
        fileGrid.setOnPressed((property -> {
            if (property.getType() == FileType.DIRECTORY) {
                openDirectory(property.getPath());
            } else {
                openFile(property);
            }
        }));

        fileGrid.setOnCopyFile(property -> showCopyMove());
        fileGrid.setOnMoveFile(property -> showCopyMove());
        fileGrid.setOnRenameFile(this::rename);

        /*
            drag file
         */
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(-0.9);

        pane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
            pane.setEffect(colorAdjust);
        });

        pane.setOnDragDropped((d) -> {
            try {
                pane.setEffect(null);
                Dragboard dragboard = d.getDragboard();
                File file = dragboard.getFiles().get(0);
                service.upload(SecurityUtil.getUserName(), file);
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pane.setOnDragDone( (d) -> pane.setEffect(null));

        fileGrid.setOnDragDetected((DragFileHandler) event -> {
            Dragboard db = event.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();

            File tempFile = createTempFile(event.getFileProperty());
            content.putFiles(Collections.singletonList((tempFile)));
            db.setContent(content);
        });
    }

    private File createTempFile(FileProperty property){
        String path = property.getPath();

        if(property.getType() == FileType.DIRECTORY) {
            path = path.concat(".zip");
        }

        String name = FileUtil.getNameOfPath(path);
        try {
            File file = Files.createTempFile(null, name).toFile();
            service.download(SecurityUtil.getUserName(), property.getPath(), file);
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete() {
        FileProperty property = FileGrid.getSelectedFile().getFileProperty();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want delete " + property.getPath() + "?");
        if(alert.showAndWait().get() == ButtonType.OK){
            if(!service.delete(SecurityUtil.getUserName(), property.getPath())) {
                new Alert(Alert.AlertType.ERROR, "File is't deleted").showAndWait();
            }
            update();
        }
    }

    public void download() {
        FileChooser fileChooser = new FileChooser();
        FileProperty source = FileGrid.getSelectedFile().getFileProperty();
        fileChooser.setInitialFileName(source.getName() + (source.getType() == FileType.DIRECTORY ? ".zip" : ""));
        File file = fileChooser.showSaveDialog(fileGrid.getScene().getWindow());

        if (file != null) {
            DownloadTask download = service.download(SecurityUtil.getUserName(), source.getPath(), file);

            download.setOnFailed(event ->
                    new Alert(Alert.AlertType.ERROR, "Download file " + file.getName() +" is failed").show());
        }
    }

    public void upload() {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(fileGrid.getScene().getWindow());

        if (file != null) {
            UploadTask upload = service.upload(SecurityUtil.getUserName(), file);
            upload.setOnSucceeded((event) ->
                    update());

            upload.setOnFailed((event ->
                    new Alert(Alert.AlertType.ERROR, "Upload file " + file.getName() +" is failed").show()));
        }
    }

    private void rename(FileProperty property){
        if(!service.rename(SecurityUtil.getUserName(), property.getPath(), property.getName())){
            new Alert(Alert.AlertType.ERROR, "File is't renamed").showAndWait();
        }
        update();
    }

    public void openRootDirectory() {
        openDirectory(service.getRootDir());
    }

    public void openDocumentDirectory(){
        openDirectory("/Documents");
    }

    private void openDirectory(String path) {
        fileGrid.clear();

        try {
            List<FileProperty> properties = service.get(SecurityUtil.getUserName(), path);
            fileGrid.addAll(properties);
        } catch (NullPointerException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Can't open this directory").showAndWait();
            openRootDirectory();
        }
    }

    private void openFile(FileProperty property) {
        File tempFile = createTempFile(property);
        try {
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        openDirectory(service.getCurrentDir());
    }

    private void showCopyMove(){
        controlsBar.setVisible(false);
        copyMoveBar.setVisible(true);
    }

    public void cancelCopyMove() {
        fileGrid.setRenameFile(null);
        fileGrid.setCopyFile(null);
        fileGrid.setMoveFile(null);

        controlsBar.setVisible(true);
        copyMoveBar.setVisible(false);
    }

    public void applyCopyMove() {
        String currentDir = service.getCurrentDir();

        boolean result;

        if(FileGrid.copyFileProperty().isNotNull().get()){
            FileProperty propertyOf = FileGrid.copyFileProperty().get().getFileProperty();
            result = service.copy(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        }
        else {
            FileProperty propertyOf = FileGrid.moveFileProperty().get().getFileProperty();
            result = service.move(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        }

        if(!result){
            new Alert(Alert.AlertType.ERROR, "Copy/Move is failed").showAndWait();
        }

        cancelCopyMove();
        update();
    }

    public void createDirectory() {
        TextInputDialog inputDialog = new TextInputDialog();

        inputDialog.setHeaderText("Create directory");
        inputDialog.setTitle("Create directory");
        Optional<String> result = inputDialog.showAndWait();

        result.ifPresent(name ->{
            if(!service.createDirectory(SecurityUtil.getUserName(), name)){
                new Alert(Alert.AlertType.ERROR, "Create directory is failed!").showAndWait();
            }
            update();
        });
    }
}
