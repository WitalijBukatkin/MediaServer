package ru.mediaserver.service.downloadservice.repository;

import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.stereotype.Repository;
import ru.mediaserver.service.downloadservice.configuration.DownloadServiceConfiguration;
import ru.mediaserver.service.downloadservice.model.DownloadProperty;
import ru.mediaserver.service.downloadservice.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Repository
public class DownloadRepository {
    private final DownloadServiceConfiguration configuration;

    public DownloadRepository(DownloadServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<DownloadProperty> information(String user) {
        String root = FileUtil.getRoot(configuration.getRootFilePath(), user);

        return FileUtil.createListDownloads(root);
    }

    public boolean add(String user, String url) {
        String root = FileUtil.getRoot(configuration.getRootFilePath(), user);
        String name = FileUtil.getNameOfPath(url);

        new Thread(() ->{
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                File file = new File(root.concat("/").concat(name));

                try (FileOutputStream fos = new FileOutputStream(file);
                     InputStream inputStream = urlConnection.getInputStream()) {

                    int totalSize = urlConnection.getContentLength();
                    int downloadedSize = 0;

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();

        return false;
    }

    public void delete(String user, String name) throws IOException {
        String root = FileUtil.getRoot(configuration.getRootFilePath(), user);

        File file = new File(root.concat("/").concat(name));

        if(!file.delete()) {
            FileDeleteStrategy.FORCE.delete(file);
        }
    }
}
