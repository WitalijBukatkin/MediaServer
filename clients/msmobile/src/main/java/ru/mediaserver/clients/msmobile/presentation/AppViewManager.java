package ru.mediaserver.clients.msmobile.presentation;

import com.gluonhq.charm.glisten.afterburner.AppView;
import com.gluonhq.charm.glisten.afterburner.AppViewRegistry;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.afterburner.Utils;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;
import ru.mediaserver.clients.msmobile.MSMobile;
import ru.mediaserver.clients.msmobile.business.util.SecurityUtil;
import ru.mediaserver.clients.msmobile.presentation.auth.auth.AuthPresenter;
import ru.mediaserver.clients.msmobile.presentation.downloads.DownloadsPresenter;
import ru.mediaserver.clients.msmobile.presentation.files.FilesPresenter;

import java.util.Locale;

import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.*;

public class AppViewManager {
    public static final AppViewRegistry REGISTRY = new AppViewRegistry();

    public static final AppView FILES_VIEW = view("Files", FilesPresenter.class, MaterialDesignIcon.INSERT_DRIVE_FILE, SHOW_IN_DRAWER, HOME_VIEW, SKIP_VIEW_STACK);
    public static final AppView DOWNLOADS_VIEW = view("Downloads", DownloadsPresenter.class, MaterialDesignIcon.FILE_DOWNLOAD, SHOW_IN_DRAWER);
    public static final AppView LOGOUT = view("Logout", AuthPresenter.class, MaterialDesignIcon.CANCEL, SHOW_IN_DRAWER);

    private static AppView view(String title, Class<? extends GluonPresenter<?>> presenterClass, MaterialDesignIcon menuIcon, AppView.Flag... flags ) {
        return REGISTRY.createView(name(presenterClass), title, presenterClass, menuIcon, flags);
    }

    private static String name(Class<? extends GluonPresenter<?>> presenterClass) {
        return presenterClass.getSimpleName().toUpperCase(Locale.ROOT).replace("PRESENTER", "");
    }
    
    public static void registerViewsAndDrawer(MobileApplication app) {
        for (AppView view : REGISTRY.getViews()) {
            view.registerView(app);
        }

        NavigationDrawer.Header header = new NavigationDrawer.Header("MediaServer Home",
                SecurityUtil.getUserName(),
                new Avatar(21, new Image(MSMobile.class.getResourceAsStream("/icon.png"))));

        header.subtitleProperty().bind(SecurityUtil.userNameProperty());
        Utils.buildDrawer(app.getDrawer(), header, REGISTRY.getViews());
    }
}
