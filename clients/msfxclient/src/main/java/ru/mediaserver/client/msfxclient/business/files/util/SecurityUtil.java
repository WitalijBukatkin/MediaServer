package ru.mediaserver.client.msfxclient.business.files.util;

public class SecurityUtil {
    private final static String server = "http://localhost:8100/";
    private final static String userName = "rock64";
    private final static String password = "rock64";

    public static String getServer() {
        return server;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getPassword() {
        return password;
    }
}