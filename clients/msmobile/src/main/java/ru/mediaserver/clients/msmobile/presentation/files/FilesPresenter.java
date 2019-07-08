package ru.mediaserver.clients.msmobile.presentation.files;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.PicturesService;
import com.gluonhq.charm.down.plugins.ShareService;
import com.gluonhq.charm.down.plugins.StorageService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.layout.layer.MenuSidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import ru.mediaserver.clients.msmobile.MSMobile;
import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.model.FileType;
import ru.mediaserver.clients.msmobile.business.files.service.FileService;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;
import ru.mediaserver.clients.msmobile.presentation.control.FileGrid;
import ru.mediaserver.clients.msmobile.presentation.control.FileItem;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FilesPresenter extends GluonPresenter<MSMobile> {
    @FXML
    private View files;

    @FXML
    private FileGrid fileGrid;

    private FileService service;

    private AppBar appBar = getApp().getAppBar();

    public void initialize() {
        files.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                if (SecurityUtil.getAccessToken() != null) {
                    service = new FileService();

                    fileGrid.init(files);

                    openDirectory(service.getCurrentDir());

                    fileGrid.setOnPressed((property -> {
                        if (property.getType() == FileType.DIRECTORY) {
                            openDirectory(property.getPath());
                        } else {
                            download(property);
                        }
                    }));

                    showNormalAppBar();
                }
            }
        });
    }

    private void menuInitialize() {
        Menu menu = new Menu();

        MenuItem name = new MenuItem();
        name.setDisable(true);
        MenuItem path = new MenuItem();
        path.setDisable(true);
        MenuItem size = new MenuItem();
        size.setDisable(true);

        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(event -> {
            FileItem file = FileGrid.getSelectedFile();
            if (file != null) {
                FileGrid.setCopyFile(file.getFileProperty());
                showCopyMoveBar();
            }
        });

        MenuItem move = new MenuItem("Move");
        move.setOnAction(event -> {
            FileItem file = FileGrid.getSelectedFile();
            if (file != null) {
                FileGrid.setMoveFile(file.getFileProperty());
                showCopyMoveBar();
            }
        });

        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(event -> rename());

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> delete());

        menu.getItems().addAll(name, path, size, copy, move, rename, delete);

        appBar.getActionItems().add(MaterialDesignIcon.MORE_VERT.button(e -> {
            FileItem file = FileGrid.getSelectedFile();
            if (file != null) {
                FileProperty fileProperty = file.getFileProperty();
                name.setText("Name: " + fileProperty.getName());
                path.setText("Path: " + fileProperty.getPath());
                size.setText("Size: " + fileProperty.getLength());
                new MenuSidePopupView(menu).show();
            }
        }));
    }

    private void showCopyMoveBar() {
        AppBar appBar = getApp().getAppBar();
        appBar.clear();

        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                getApp().getDrawer().open()));
        appBar.setTitleText("Files");

        appBar.getActionItems().add(MaterialDesignIcon.CANCEL.button(e ->
                cancelCopyMove()));

        new FloatingActionButton(MaterialDesignIcon.CHECK.text,
                e -> applyCopyMove()).showOn(files);
    }

    private void showNormalAppBar() {
        appBar.clear();

        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                getApp().getDrawer().open()));
        appBar.setTitleText("Files");

        appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e ->
                update()));

        appBar.getActionItems().add(MaterialDesignIcon.CREATE_NEW_FOLDER.button(e ->
                createDirectory()));

        menuInitialize();

        new FloatingActionButton(MaterialDesignIcon.FILE_UPLOAD.text,
                e -> upload()).showOn(files);
    }

    private void openDirectory(String path) {
        fileGrid.clear();

        try {
            List<FileProperty> properties = service.get(SecurityUtil.getUserName(), path);
            fileGrid.addAll(properties);
        } catch (Throwable e) {
            new Alert(AlertType.ERROR, e.toString()).showAndWait();
        }
    }

    private void update() {
        openDirectory(service.getCurrentDir());
    }

    private void delete() {
        FileItem file = FileGrid.getSelectedFile();

        if (file != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure want delete " + file.getFileProperty().getPath() + "?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (!service.delete(SecurityUtil.getUserName(), file.getFileProperty().getPath())) {
                    new Alert(AlertType.ERROR, "File is't deleted").showAndWait();
                }
                update();
            }
        }
    }

    private void upload() {
        Services.get(PicturesService.class).ifPresent(picturesService -> {
            picturesService.loadImageFromGallery();
            Optional<File> input = picturesService.getImageFile();

            if (input.isPresent()) {
                try {
                    service.upload(SecurityUtil.getUserName(), input.get());
                } catch (Throwable e) {
                    new Alert(AlertType.ERROR, e.toString()).showAndWait();
                }
                update();
            }
        });
    }

    private void download(FileProperty property) {
        File root = Services.get(StorageService.class)
                .flatMap(s -> s.getPublicStorage("Documents"))
                .orElseThrow(() -> new RuntimeException("Documents not available"));

        try {
            File file = new File(root, property.getName());

            service.download(SecurityUtil.getUserName(), property.getPath(), file);

            String type = property.getType() == FileType.IMAGE ?
                    "image/*" : "&lowast;/&lowast;";

            Services.get(ShareService.class).ifPresent(shareService ->
                    shareService.share(type, file));
            file.deleteOnExit();
        } catch (Exception e) {
            new Alert(AlertType.ERROR, e.toString()).showAndWait();
        }
    }

    private void cancelCopyMove() {
        FileGrid.setCopyFile(null);
        FileGrid.setMoveFile(null);
        showNormalAppBar();
    }

    private void applyCopyMove() {
        String currentDir = service.getCurrentDir();

        boolean result;

        if (FileGrid.getCopyFile() != null) {
            FileProperty propertyOf = FileGrid.getCopyFile();
            result = service.copy(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        } else {
            FileProperty propertyOf = FileGrid.getMoveFile();
            result = service.move(SecurityUtil.getUserName(), propertyOf.getPath(), currentDir, propertyOf.getName());
        }

        if (!result) {
            new Alert(AlertType.ERROR, "Copy/Move is failed").showAndWait();
        }

        cancelCopyMove();
        update();
    }

    private void createDirectory() {
        Dialog dialog = new Dialog();
        dialog.setTitle(new Label("New Folder"));
        TextField name = new TextField("New Folder");
        dialog.setContent(name);

        Button apply = new Button("Apply");
        apply.setOnAction(event -> {
            if (!service.createDirectory(SecurityUtil.getUserName(), name.getText())) {
                new Alert(AlertType.ERROR, "Create directory is failed!").showAndWait();
            }
            update();
            dialog.hide();
        });

        dialog.getButtons().add(apply);
        dialog.showAndWait();
    }

    private void rename() {
        FileItem file = FileGrid.getSelectedFile();

        if (file != null) {
            FileProperty fileProperty = file.getFileProperty();

            Dialog dialog = new Dialog();
            dialog.setTitle(new Label("Rename"));
            TextField name = new TextField(fileProperty.getName());
            dialog.setContent(name);

            Button apply = new Button("Apply");
            apply.setOnAction(event -> {
                if (!service.rename(SecurityUtil.getUserName(), fileProperty.getPath(), name.getText())) {
                    new Alert(AlertType.ERROR, "File is't renamed").showAndWait();
                }
                update();
                dialog.hide();
            });

            dialog.getButtons().add(apply);
            dialog.showAndWait();
        }
    }
}
