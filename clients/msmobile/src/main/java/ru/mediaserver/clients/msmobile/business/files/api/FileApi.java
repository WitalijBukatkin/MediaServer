package ru.mediaserver.clients.msmobile.business.files.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;

import java.util.List;

public interface FileApi {
    @GET("/file/get/{user}/{path}")
    Call<List<FileProperty>> get(@Path("user") String user, @Path("path") String path);

    @DELETE("/file/{user}/{path}")
    Call<ResponseBody> delete(@Path("user") String user, @Path("path") String pat);

    @Multipart
    @POST("/file")
    Call<ResponseBody> upload(@Part MultipartBody.Part file, @Part("user") String user, @Part("path") String path);

    @Streaming
    @GET("/file/download/{user}/{path}")
    Call<ResponseBody> download(@Path("user") String user, @Path("path") String path);

    @GET("/file/copy/{user}/from/{from}/to/{to}")
    Call<ResponseBody> copy(@Path("user") String user, @Path("from") String from, @Path("to") String to);

    @GET("/file/move/{user}/from/{from}/to/{to}")
    Call<ResponseBody> move(@Path("user") String user, @Path("from") String from, @Path("to") String to);

    @GET("/file/create/{user}/{path}")
    Call<ResponseBody> createDirectory(@Path("user") String user, @Path("path") String from);
}