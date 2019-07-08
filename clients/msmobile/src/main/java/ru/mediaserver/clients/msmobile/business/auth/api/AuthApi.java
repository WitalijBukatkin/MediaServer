package ru.mediaserver.clients.msmobile.business.auth.api;

import retrofit2.Call;
import retrofit2.http.*;
import ru.mediaserver.clients.msmobile.business.auth.model.OAuthToken;
import ru.mediaserver.clients.msmobile.business.auth.model.Principal;
import ru.mediaserver.clients.msmobile.business.auth.model.User;

public interface AuthApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<OAuthToken> requestToken(
            @Field("grant_type") String grantType,
            @Field("username") String userName,
            @Field("password") String password);

    @GET("/oauth/current")
    Call<Principal> current();

    @POST("/oauth/register")
    Call<User> register(@Body User user);
}
