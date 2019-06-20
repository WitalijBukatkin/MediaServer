package ru.mediaserver.client.msfxclient.business.util;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitBuilderUtil {
    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    if (SecurityUtil.getAccessToken() == null) {
                        request = request.newBuilder()
                                .header("Authorization",
                                        Credentials.basic(ServerUtil.userName, ServerUtil.password))
                                .build();
                    } else {
                        request = request.newBuilder()
                                .header("Authorization", "Bearer " + SecurityUtil.getAccessToken())
                                .build();
                    }
                    return chain.proceed(request);
                })
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    public static Retrofit getRetrofit(String port) {
        return new Retrofit.Builder()
                .baseUrl(ServerUtil.serverUrl + ":" + port)
                .client(getClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
