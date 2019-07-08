package ru.mediaserver.clients.msmobile.presentation.auth.register;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.mediaserver.clients.msmobile.business.auth.model.User;
import ru.mediaserver.clients.msmobile.business.auth.repository.AuthRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterPresenter implements Initializable {
    public View register;

    public TextField login;
    public PasswordField password;
    public PasswordField passwordRepeat;

    @Inject
    private AuthRepository repository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new FloatingActionButton(MaterialDesignIcon.CHECK.text, e -> {
            if (register()) {
                MobileApplication.getInstance()
                        .switchToPreviousView();
            }
        }).showOn(register);

        register.showingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setTitleText("Registration");
            }
        });
    }

    private boolean register() {
        if (login.getText().isEmpty() || password.getText().isEmpty() || passwordRepeat.getText().isEmpty()) {
            new Alert(AlertType.ERROR, "Enter login and password to fields").showAndWait();
            return false;
        }

        if (!password.getText().equals(passwordRepeat.getText())) {
            new Alert(AlertType.ERROR, "Password and Password repeat are not equals").showAndWait();
            return false;
        }

        try {
            User user = new User(0, login.getText(), password.getText());

            if (repository.register(user) != null) {
                new Alert(AlertType.INFORMATION, "Congratulations! Now you are MediaServer user").showAndWait();
            } else {
                new Alert(AlertType.ERROR, "Is there something wrong!").showAndWait();
                return false;
            }
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Server is down, repeat please").showAndWait();
            return false;
        }
        return true;
    }

    public void cancel(ActionEvent event) {
        MobileApplication.getInstance()
                .switchToPreviousView();
    }
}
