package ru.mediaserver.clients.msmobile.presentation.auth.auth;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import ru.mediaserver.clients.msmobile.business.auth.model.OAuthToken;
import ru.mediaserver.clients.msmobile.business.auth.model.Principal;
import ru.mediaserver.clients.msmobile.business.auth.repository.AuthRepository;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthPresenter extends GluonPresenter<MobileApplication> implements Initializable {
    public View auth;
    public TextField login;
    public TextField password;

    @Inject
    private AuthRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SecurityUtil.setUserName(null);
        SecurityUtil.setAccessToken(null);

        new FloatingActionButton(MaterialDesignIcon.CHECK.text, e -> {
            if (auth()) {
                MobileApplication.getInstance()
                        .switchView(MobileApplication.HOME_VIEW);
            }
        }).showOn(auth);

        auth.showingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setTitleText("Authorization");
            }
        });
    }

    public boolean auth() {
        if (login.getText().isEmpty() || password.getText().isEmpty()) {
            new Alert(AlertType.ERROR, "Enter login and password to fields").showAndWait();
            return false;
        }

        try {
            OAuthToken authToken = repository.requestToken("password", login.getText(), password.getText());
            if (authToken == null) {
                new Alert(AlertType.ERROR, "Login or password are incorrect").showAndWait();
                return false;
            }

            SecurityUtil.setAccessToken(authToken.getAccess_token());

            Principal current = repository.current();
            if (current == null) {
                new Alert(AlertType.ERROR, "Login or password are incorrect").showAndWait();
                return false;
            }

            SecurityUtil.setUserName(current.getName());
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Server is down, repeat please").showAndWait();
            return false;
        }
        return true;
    }

    public void register() {
        MobileApplication.getInstance()
                .switchView("Register");
    }
}
