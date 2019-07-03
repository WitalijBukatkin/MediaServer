package ru.mediaserver.client.msdesktop.presentation.menu.downloads;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.mediaserver.client.msdesktop.business.downloads.model.DownloadProperty;
import ru.mediaserver.client.msdesktop.business.downloads.repository.DownloadRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DownloadsPresenter implements Initializable {

    public ListView downloadsList;
    public Label process;

    @Inject
    private DownloadRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        downloadsList.setItems(FXCollections.observableArrayList());

        update();
    }

    public void addLink() {
        TextInputDialog inputDialog = new TextInputDialog();

        inputDialog.setHeaderText("Add link");
        inputDialog.setTitle("Add link");
        Optional<String> result = inputDialog.showAndWait();

        result.ifPresent(url ->{
            try{
                repository.add(url);
                update();
            } catch (IOException e){
                new Alert(Alert.AlertType.ERROR, "Add link is failed!").showAndWait();
            }
        });
    }

    public void delete() {
        String selectedName = (String) downloadsList.getSelectionModel().getSelectedItem();
        if(selectedName != null) {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want delete " + selectedName + "?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    repository.delete(selectedName.substring(0, selectedName.indexOf(" / ")));
                    update();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Download is't deleted").showAndWait();
                }
            }
        }
    }

    public void update(){
        try {
            ObservableList<String> list = downloadsList.getItems();
            list.clear();

            for (DownloadProperty property : repository.getAll()) {
                String name = property.getName() + " / " + property.getLength() / 1000 + "KB";
                list.add(name);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Update is failed").showAndWait();
        }
    }
}
