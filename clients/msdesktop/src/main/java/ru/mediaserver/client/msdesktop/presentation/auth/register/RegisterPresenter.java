package ru.mediaserver.client.msdesktop.presentation.auth.register;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mediaserver.client.msdesktop.business.auth.model.User;
import ru.mediaserver.client.msdesktop.business.auth.repository.AuthRepository;

import javax.inject.Inject;
import java.io.IOException;

public class RegisterPresenter {
    public TextField login;
    public PasswordField password;
    public PasswordField passwordRepeat;

    @Inject
    private AuthRepository repository;

    public Stage stage;

    @FXML
    private void register() {
        if (login.getText().isEmpty() || password.getText().isEmpty() || passwordRepeat.getText().isEmpty()) {
            new Alert(AlertType.ERROR, "Enter login and password to fields").showAndWait();
            return;
        }

        if(!password.getText().equals(passwordRepeat.getText())){
            new Alert(AlertType.ERROR, "Password and Password repeat are not equals").showAndWait();
            return;
        }

        try {
            User user = new User(0, login.getText(), password.getText());

            if(repository.register(user) != null){
                new Alert(AlertType.INFORMATION, "Congratulations! Now you are MediaServer user").showAndWait();
                stage.close();
            }
            else {
                new Alert(AlertType.ERROR, "Is there something wrong!").showAndWait();
                return;
            }
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "Server is down, repeat please").showAndWait();
        }
    }

}
