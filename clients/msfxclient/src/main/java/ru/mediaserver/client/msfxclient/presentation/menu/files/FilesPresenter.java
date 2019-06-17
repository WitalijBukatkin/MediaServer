package ru.mediaserver.client.msfxclient.presentation.menu.files;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import ru.mediaserver.client.msfxclient.business.files.model.FileProperty;
import ru.mediaserver.client.msfxclient.business.files.model.FileType;
import ru.mediaserver.client.msfxclient.business.files.service.FileService;
import ru.mediaserver.client.msfxclient.business.files.util.SecurityUtil;
import ru.mediaserver.client.msfxclient.presentation.control.FileGrid;
import ru.mediaserver.client.msfxclient.presentation.event.DragFileHandler;
import ru.mediaserver.client.msfxclient.presentation.menu.files.namepanel.NamePanelPresenter;
import ru.mediaserver.client.msfxclient.presentation.menu.files.namepanel.NamePanelView;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import static ru.mediaserver.client.msfxclient.business.files.service.FileService.eventBarProperty;

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
                openFile(property.getPath());
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

            var path = event.getFileProperty().getPath();

            var tempFile = createTempFile(path);
            content.putFiles(List.of(tempFile));

            db.setContent(content);
        });
    }

    public File createTempFile(String path){
        String name = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        try {
            var tempFile = Files.createTempFile("", name).toFile();
            service.download(SecurityUtil.getUserName(), path, tempFile);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete() {
        var property = FileGrid.getSelectedFile().getFileProperty();

        var alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want delete " + property.getPath() + "?");
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
            var download = service.download(SecurityUtil.getUserName(), source.getPath(), file);

            download.setOnFailed(event ->
                    new Alert(Alert.AlertType.ERROR, "Download file " + file.getName() +" is failed").show());
        }
    }

    public void upload() {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(fileGrid.getScene().getWindow());

        if (file != null) {
            var upload = service.upload(SecurityUtil.getUserName(), file);
            upload.setOnSucceeded((event) ->
                    update());

            upload.setOnFailed((event ->
                    new Alert(Alert.AlertType.ERROR, "Upload file " + file.getName() +" is failed").show()));
        }
    }

    public void rename(FileProperty property){
        if(!service.rename(SecurityUtil.getUserName(), property.getPath(), property.getName())){
            new Alert(Alert.AlertType.ERROR, "File is't renamed").showAndWait();
        }
        update();
    }

    public void openRootDirectory() {
        openDirectory(service.getRootDir());
    }

    public void openDocumentDirectory(){
        openDirectory("Documents");
    }

    public void openDirectory(String path) {
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

    private void openFile(String path) {
        var tempFile = createTempFile(path);
        try {
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        openDirectory(service.getCurrentDir());
    }

    public void showCopyMove(){
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
        var currentDir = service.getCurrentDir();

        boolean result;

        if(FileGrid.copyFileProperty().isNotNull().get()){
            var propertyOf = FileGrid.copyFileProperty().get().getFileProperty();
            result = service.copy(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        }
        else {
            var propertyOf = FileGrid.moveFileProperty().get().getFileProperty();
            result = service.move(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        }

        if(!result){
            new Alert(Alert.AlertType.ERROR, "Copy/Move is failed").showAndWait();
        }

        cancelCopyMove();
        update();
    }

    public void createDirectory() {
        NamePanelView namePanelView = new NamePanelView();

        var namePanelPresenter = (NamePanelPresenter) namePanelView.getPresenter();
        namePanelPresenter.init("", "Create");

        var popOver = new PopOver(namePanelView.getView());
        popOver.show(controlsBar);

        namePanelPresenter.confirm.setOnAction(event -> {
            if(!service.createDirectory(SecurityUtil.getUserName(), namePanelPresenter.text.getText())){
                new Alert(Alert.AlertType.ERROR, "Create directory is failed!").showAndWait();
            }
            update();
            popOver.hide();
        });
    }
}
