package ru.mediaserver.clients.msmobile.presentation.downloads;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import ru.mediaserver.clients.msmobile.MSMobile;
import ru.mediaserver.clients.msmobile.business.downloads.model.DownloadProperty;
import ru.mediaserver.clients.msmobile.business.downloads.repository.DownloadRepository;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;

import java.io.IOException;

public class DownloadsPresenter extends GluonPresenter<MSMobile> {

    @FXML
    private View downloads;

    public ListView downloadsList;

    private DownloadRepository repository;

    public void initialize() {
        downloads.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                if (SecurityUtil.getAccessToken() != null) {
                    repository = new DownloadRepository();

                    update();

                    AppBar appBar = getApp().getAppBar();
                    appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                            getApp().getDrawer().open()));

                    appBar.setTitleText("Downloads");

                    appBar.getActionItems()
                            .add(MaterialDesignIcon.DELETE.button(e ->
                                    delete()));

                    appBar.getActionItems()
                            .add(MaterialDesignIcon.UPDATE.button(e ->
                                    update()));

                    new FloatingActionButton(MaterialDesignIcon.ADD.text,
                            e -> addLink()).showOn(downloads);
                }
            }
        });
    }

    private void addLink() {
        Dialog dialog = new Dialog();
        dialog.setTitle(new Label("Add link"));
        TextField name = new TextField();
        dialog.setContent(name);

        Button apply = new Button("Apply");
        apply.setOnAction(event -> {
            try {
                repository.add(name.getText());
                update();
            } catch (IOException e) {
                new Alert(AlertType.ERROR, "Add link is failed!").showAndWait();
            }
            dialog.hide();
        });

        dialog.getButtons().add(apply);
        dialog.showAndWait();
    }

    private void delete() {
        String selectedName = (String) downloadsList.getSelectionModel().getSelectedItem();
        if (selectedName != null) {
            com.gluonhq.charm.glisten.control.Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure want delete " + selectedName + "?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    repository.delete(selectedName.substring(0, selectedName.indexOf(" / ")));
                    update();
                } catch (IOException e) {
                    new Alert(AlertType.ERROR, "Download is't deleted").showAndWait();
                }
            }
        }
    }

    private void update() {
        try {
            ObservableList<String> list = downloadsList.getItems();
            list.clear();

            for (DownloadProperty property : repository.getAll()) {
                String name = property.getName() + " / " + property.getLength() / 1000 + "KB";
                list.add(name);
            }
        } catch (IOException | NullPointerException e) {
            new Alert(AlertType.ERROR, "Update is failed").showAndWait();
        }
    }
}
