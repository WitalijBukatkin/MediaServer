package ru.mediaserver.client.msfxclient.business.auth.repository;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.mediaserver.client.msfxclient.business.auth.model.User;

public interface AuthApi {

    @GET("/file/get/{user}/{path}")
    Call<User> get(@Path("user") String user);
}
