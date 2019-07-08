package ru.mediaserver.clients.msmobile.business.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SecurityUtil {
    private static String accessToken;
    private static StringProperty userName = new SimpleStringProperty();
    private static boolean demo = false;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        SecurityUtil.accessToken = accessToken;
    }

    public static String getUserName() {
        return userName.get();
    }

    public static void setUserName(String userName) {
        SecurityUtil.userName.set(userName);
    }

    public static StringProperty userNameProperty() {
        return userName;
    }

    public static boolean isDemo() {
        return demo;
    }

    public static void setDemo(boolean demo) {
        SecurityUtil.demo = demo;
    }
}
