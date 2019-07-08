package ru.mediaserver.clients.msdesktop.business.auth.repository;

import ru.mediaserver.clients.msdesktop.business.auth.api.AuthApi;
import ru.mediaserver.clients.msdesktop.business.auth.model.OAuthToken;
import ru.mediaserver.clients.msdesktop.business.auth.model.Principal;
import ru.mediaserver.clients.msdesktop.business.auth.model.User;
import ru.mediaserver.clients.msdesktop.business.util.RetrofitBuilderUtil;
import ru.mediaserver.clients.msdesktop.business.util.ServerUtil;

import java.io.IOException;

public class AuthRepository {
    private AuthApi getAuthApi() {
        return RetrofitBuilderUtil.getRetrofit(ServerUtil.AuthServicePort)
                .create(AuthApi.class);
    }

    public OAuthToken requestToken(String grantType, String userName, String password) throws IOException {
        return getAuthApi().requestToken(grantType, userName, password).execute().body();
    }

    public Principal current() throws IOException {
        return getAuthApi().current().execute().body();
    }

    public User register(User user) throws IOException {
        return getAuthApi().register(user).execute().body();
    }
}
