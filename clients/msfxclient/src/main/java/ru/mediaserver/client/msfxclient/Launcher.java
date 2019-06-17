package ru.mediaserver.client.msfxclient;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mediaserver.client.msfxclient.presentation.menu.MenuView;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MenuView menuView = new MenuView();

        var scene = new Scene(menuView.getView());
        scene.getStylesheets()
                .add(getClass().getResource("launcher.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }
}