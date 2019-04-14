package com.seeyewmo.movieposters.di.modules;

import android.app.Application;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    private final String mBaseUrl;
    private final String mApiKey;

    // Constructor needs one parameter to instantiate.
    public NetModule(String baseUrl, String apiKey) {
        this.mBaseUrl = baseUrl;
        this.mApiKey = apiKey;
    }

    @Provides
    @Singleton
    protected Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    protected Executor providesExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Provides
    @Singleton
    protected OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.cache(cache);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("apikey", mApiKey)
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

    @Provides
    @Singleton
    protected Retrofit provideRetrofit(/*Gson gson,*/ OkHttpClient okHttpClient) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .baseUrl(mBaseUrl)
//                .client(okHttpClient)
//                .build();
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
               // .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(okHttpClient) // OkHttp auto retires on connections issues anyway.
                .build();
        return retrofit;
    }
}
