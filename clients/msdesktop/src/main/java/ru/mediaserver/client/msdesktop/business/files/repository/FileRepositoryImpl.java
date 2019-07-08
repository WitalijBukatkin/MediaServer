package ru.mediaserver.client.msdesktop.business.files.repository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.mediaserver.client.msdesktop.business.files.api.FileApi;
import ru.mediaserver.client.msdesktop.business.files.model.FileProperty;
import ru.mediaserver.client.msdesktop.business.files.util.FileUtil;
import ru.mediaserver.client.msdesktop.business.util.RetrofitBuilderUtil;
import ru.mediaserver.client.msdesktop.business.util.ServerUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Singleton
public class FileRepositoryImpl implements FileRepository {
    private FileApi repository;

    @Inject
    public FileRepositoryImpl() {
        this(ServerUtil.serverUrl);
    }

    public FileRepositoryImpl(String baseUrl) {
        repository = RetrofitBuilderUtil.getRetrofit(ServerUtil.FileServicePort)
                .create(FileApi.class);
    }

    @Override
    public List<FileProperty> get(String user, String path) {
        try {
            return repository.get(user, path).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(String user, String path) {
        try {
            return repository.delete(user, path).execute().isSuccessful();
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

        return repository.upload(body, user, path).execute().isSuccessful();
    }

    @Override
    public InputStream download(String user, String path) throws IOException {
        return Objects.requireNonNull(repository.download(user, path)
                .execute().body()).byteStream();
    }

    @Override
    public boolean copy(String user, String pathOf, String pathTo) {
        try {
            return repository.copy(user, pathOf, pathTo).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean move(String user, String pathOf, String pathTo) {
        try {
            return repository.move(user, pathOf, pathTo).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createDirectory(String user, String path) {
        try {
            return repository.createDirectory(user, path).execute().isSuccessful();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
}