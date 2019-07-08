package ru.mediaserver.client.msdesktop.presentation.auth.auth;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mediaserver.client.msdesktop.business.auth.model.OAuthToken;
import ru.mediaserver.client.msdesktop.business.auth.model.Principal;
import ru.mediaserver.client.msdesktop.business.auth.repository.AuthRepository;
import ru.mediaserver.client.msdesktop.business.util.SecurityUtil;
import ru.mediaserver.client.msdesktop.presentation.auth.register.RegisterPresenter;
import ru.mediaserver.client.msdesktop.presentation.auth.register.RegisterView;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthPresenter implements Initializable {
    public TextField login;
    public PasswordField password;

    public Stage stage;

    @Inject
    private AuthRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SecurityUtil.setUserName(null);
        SecurityUtil.setAccessToken(null);
    }

    public void auth() {
        if (login.getText().isEmpty() || password.getText().isEmpty()) {
            new Alert(AlertType.ERROR, "Enter login and password to fields").showAndWait();
            return;
        }

        try {
            OAuthToken authToken = repository.requestToken("password", login.getText(), password.getText());
            if (authToken == null) {
                new Alert(AlertType.ERROR, "Login or password are incorrect").showAndWait();
                return;
            }

            SecurityUtil.setAccessToken(authToken.getAccess_token());

            Principal current = repository.current();
            if (current == null) {
                new Alert(AlertType.ERROR, "Login or password are incorrect").showAndWait();
                return;
            }

            SecurityUtil.setUserName(current.getName());

            stage.close();
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Server is down, repeat please").showAndWait();
        }
    }

    public void register() {
        Stage stage = new Stage();

        RegisterView registerView = new RegisterView();
        stage.setScene(new Scene(registerView.getView()));

        RegisterPresenter presenter = (RegisterPresenter) registerView.getPresenter();
        presenter.stage = stage;

        stage.showAndWait();
    }
}
