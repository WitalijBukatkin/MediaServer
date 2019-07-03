package ru.mediaserver.client.msdesktop.business.downloads.repository;

import ru.mediaserver.client.msdesktop.business.downloads.api.DownloadApi;
import ru.mediaserver.client.msdesktop.business.downloads.model.DownloadProperty;
import ru.mediaserver.client.msdesktop.business.downloads.model.converter.URLEncoder;
import ru.mediaserver.client.msdesktop.business.util.RetrofitBuilderUtil;
import ru.mediaserver.client.msdesktop.business.util.ServerUtil;

import java.io.IOException;
import java.util.List;

public class DownloadRepository {
    private DownloadApi getDownloadApi() {
        return RetrofitBuilderUtil.getRetrofit(ServerUtil.DownloadServicePort)
                .create(DownloadApi.class);
    }

    public List<DownloadProperty> getAll() throws IOException {
        return getDownloadApi().getAll().execute().body();
    }

    public void add(String url) throws IOException {
        getDownloadApi().add(url).execute();
    }

    public void delete(String name) throws IOException {
        getDownloadApi().delete(URLEncoder.encode(name)).execute();
    }
}
