package com.seeyewmo.movieposters;

import android.app.Application;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviePosterApplication extends Application {
    private static Retrofit retrofit;
    private static final String API = "http://www.omdbapi.com/";
    private static final String API_KEY = "82c4cc71";

    @Override
    public void onCreate() {
        super.onCreate();

        int cacheSize = 10 * 1024 * 1024; // 10MB
        buildRetrofit(getOkhttpClient(new Cache(getCacheDir(),cacheSize)), API);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static OkHttpClient getOkhttpClient(Cache cache) {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.cache(cache);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("apikey", API_KEY)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return httpClient.build();
    }

    // TODO: This should be done by dagger
    public static Retrofit buildRetrofit(OkHttpClient httpClient, String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(httpClient) // OkHttp auto retires on connections issues anyway.
                .build();
        return retrofit;
    }
}
