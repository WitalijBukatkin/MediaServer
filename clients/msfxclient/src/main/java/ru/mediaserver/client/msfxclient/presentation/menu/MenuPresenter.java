package ru.mediaserver.client.msfxclient.presentation.menu;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import ru.mediaserver.client.msfxclient.business.util.SecurityUtil;
import ru.mediaserver.client.msfxclient.presentation.menu.downloads.DownloadsPresenter;
import ru.mediaserver.client.msfxclient.presentation.menu.downloads.DownloadsView;
import ru.mediaserver.client.msfxclient.presentation.menu.files.FilesPresenter;
import ru.mediaserver.client.msfxclient.presentation.menu.files.FilesView;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuPresenter implements Initializable {

    public BorderPane pane;
    public Label name;

    private FilesView filesView = new FilesView();
    private FilesPresenter filesPresenter = (FilesPresenter) filesView.getPresenter();

    private DownloadsView downloadsView = new DownloadsView();
    private DownloadsPresenter downloadsPresenter = (DownloadsPresenter) downloadsView.getPresenter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pane.setCenter(filesView.getView());

        name.textProperty().bind(SecurityUtil.userNameProperty());
    }

    public void clickHomeButton(ActionEvent actionEvent) {
        pane.setCenter(filesView.getView());
        filesPresenter.openRootDirectory();
    }

    public void clickDocumentsButton(ActionEvent actionEvent) {
        pane.setCenter(filesView.getView());
        filesPresenter.openDocumentDirectory();
    }

    public void clickDownloadsButton(ActionEvent actionEvent) {
        pane.setCenter(downloadsView.getView());
        downloadsPresenter.update();
    }
}
