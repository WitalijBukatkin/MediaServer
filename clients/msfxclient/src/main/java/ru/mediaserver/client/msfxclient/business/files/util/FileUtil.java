package ru.mediaserver.client.msfxclient.business.files.util;

public class FileUtil {

    public static String getNameOfPath(String path){
        return path.contains("/") ?
                path.substring(path.lastIndexOf("/") + 1) : path;
    }

    public static String getPathWithOutName(String path){
        return path.contains("/") ?
                path.substring(0, path.lastIndexOf("/")) : "";
    }
}
