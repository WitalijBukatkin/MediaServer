package ru.mediaserver.service.downloadservice.util;

import ru.mediaserver.service.downloadservice.model.DownloadProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static String getRoot(String rootPath, String userName){
        return rootPath.concat(userName).concat("/Downloads");
    }

    public static String getNameOfPath(String path){
        return path.contains("/") ?
                path.substring(path.lastIndexOf("/") + 1) : path;
    }

    public static List<DownloadProperty> createListDownloads(String root){
        File rootFile = new File(root);

        List<DownloadProperty> downloadPropertyList = new ArrayList<>();

        if(rootFile.exists() && rootFile.isDirectory()){
            File[] files = rootFile.listFiles();

            for (File file : files) {
                downloadPropertyList.add(createPropertyOfFile(file));
            }

            return downloadPropertyList;
        }

        return null;
    }

    public static DownloadProperty createPropertyOfFile(File file){
        DownloadProperty property = new DownloadProperty();
        property.setName(file.getName());
        property.setLength(file.length());
        return property;
    }
}
