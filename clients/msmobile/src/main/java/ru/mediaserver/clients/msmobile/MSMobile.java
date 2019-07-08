package ru.mediaserver.clients.msmobile;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;
import ru.mediaserver.clients.msmobile.presentation.AppViewManager;
import ru.mediaserver.clients.msmobile.presentation.auth.auth.AuthView;
import ru.mediaserver.clients.msmobile.presentation.auth.register.RegisterView;

public class MSMobile extends MobileApplication {

    @Override
    public void init() {
        addViewFactory("Auth", () -> {
            AuthView authView = new AuthView();
            return (View) authView.getView();
        });

        addViewFactory("Register", () -> {
            RegisterView registerView = new RegisterView();
            return (View) registerView.getView();
        });

        AppViewManager.registerViewsAndDrawer(this);

        if (SecurityUtil.isDemo()) {
            SecurityUtil.setAccessToken("");
        }
    }

    @Override
    public void postInit(Scene scene) {
        switch ((int) (Math.random() * 4)) {
            case 0:
                Swatch.BLUE.assignTo(scene);
                break;
            case 1:
                Swatch.RED.assignTo(scene);
                break;
            case 2:
                Swatch.BROWN.assignTo(scene);
                break;
            case 3:
                Swatch.YELLOW.assignTo(scene);
                break;
        }

        if (SecurityUtil.getAccessToken() == null) {
            switchView("Auth");
        }
    }
}
