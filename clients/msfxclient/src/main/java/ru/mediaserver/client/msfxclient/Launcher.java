package ru.mediaserver.client.msfxclient;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mediaserver.client.msfxclient.business.util.SecurityUtil;
import ru.mediaserver.client.msfxclient.presentation.auth.auth.AuthPresenter;
import ru.mediaserver.client.msfxclient.presentation.auth.auth.AuthView;
import ru.mediaserver.client.msfxclient.presentation.menu.MenuView;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        if(SecurityUtil.getAccessToken() == null){
            var authView = new AuthView();

            Stage stageNotPrimary = new Stage();
            stageNotPrimary.setScene(new Scene(authView.getView()));

            var presenter = (AuthPresenter) authView.getPresenter();
            presenter.stage = stageNotPrimary;

            stageNotPrimary.showAndWait();
        }

        if(SecurityUtil.getAccessToken() != null){
            MenuView menuView = new MenuView();

            var scene = new Scene(menuView.getView());
            scene.getStylesheets()
                    .add(getClass().getResource("launcher.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("MediaServer Home");
            stage.show();
        }
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }
}