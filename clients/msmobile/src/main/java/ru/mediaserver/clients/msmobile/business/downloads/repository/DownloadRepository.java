package ru.mediaserver.clients.msmobile.business.downloads.repository;

import ru.mediaserver.clients.msmobile.business.downloads.api.DownloadApi;
import ru.mediaserver.clients.msmobile.business.downloads.model.DownloadProperty;
import ru.mediaserver.clients.msmobile.business.downloads.model.converter.URLEncoder;
import ru.mediaserver.clients.msmobile.business.util.RetrofitBuilderUtil;
import ru.mediaserver.clients.msmobile.business.util.ServerUtil;

import java.io.IOException;
import java.util.List;

public class DownloadRepository {
    private DownloadApi downloadApi;

    public DownloadRepository() {
        downloadApi = RetrofitBuilderUtil.getRetrofit(ServerUtil.DownloadServicePort)
                .create(DownloadApi.class);
    }

    public List<DownloadProperty> getAll() throws IOException {
        return downloadApi.getAll().execute().body();
    }

    public void add(String url) throws IOException {
        downloadApi.add(url).execute();
    }

    public void delete(String name) throws IOException {
        downloadApi.delete(URLEncoder.encode(name)).execute();
    }
}
