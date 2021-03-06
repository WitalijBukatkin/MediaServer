package ru.mediaserver.clients.msmobile.business.files.repository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.mediaserver.clients.msmobile.business.files.api.FileApi;
import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.util.FileUtil;
import ru.mediaserver.clients.msmobile.business.util.RetrofitBuilderUtil;
import ru.mediaserver.clients.msmobile.business.util.ServerUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class FileRepositoryImpl implements FileRepository {
    private FileApi fileApi;

    @Inject
    public FileRepositoryImpl() {
        fileApi = RetrofitBuilderUtil.getRetrofit(ServerUtil.FileServicePort)
                .create(FileApi.class);
    }

    @Override
    public List<FileProperty> get(String user, String path) {
        try {
            return fileApi.get(user, path).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(String user, String path) {
        try {
            return fileApi.delete(user, path).execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean upload(String user, String path, String name, InputStream file) throws IOException {
        RequestBody requestFile = RequestBody.create(null, FileUtil.getAllBytes(file));

        MultipartBody.Part body = MultipartBody.Part
                .createFormData("value", name, requestFile);

        return fileApi.upload(body, user, path).execute().isSuccessful();
    }

    @Override
    public InputStream download(String user, String path) throws IOException {
        return Objects.requireNonNull(fileApi.download(user, path)
                .execute().body()).byteStream();
    }

    @Override
    public boolean copy(String user, String pathOf, String pathTo) {
        try {
            return fileApi.copy(user, pathOf, pathTo).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean move(String user, String pathOf, String pathTo) {
        try {
            return fileApi.move(user, pathOf, pathTo).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createDirectory(String user, String path) {
        try {
            return fileApi.createDirectory(user, path).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
}