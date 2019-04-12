package com.seeyewmo.movieposters.api;

import java.io.IOException;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkDataSource {

    private static final String API = "http://www.omdbapi.com/";
    private static final String API_KEY = "82c4cc71";
    private final MovieWebService webService;
    private final MutableLiveData<SearchResponse> searchResults;
    private Call<SearchResult> searchResultCall;

    public NetworkDataSource() {
        searchResults = new MutableLiveData<>();

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(httpClient.build()) // OkHttp auto retires on connections issues anyway.
                .build();

        // Create an instance of our GitHub API interface.
        webService = retrofit.create(MovieWebService.class);
    }

    public LiveData<SearchResponse> search(final String key) {
        if (searchResultCall != null) {
            searchResultCall.cancel();
        }

        searchResultCall = webService.searchPoster(key);
        searchResultCall.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, retrofit2.Response<SearchResult> response) {
                searchResults.postValue(SearchResponse.success(key,response.body()));
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                if (!call.isCanceled()) {
                    //Only post result if it's cancelled
                    searchResults.postValue(SearchResponse.fail(key, t));
                }
            }
        });
        return searchResults;
    }

    public void searchWithCallback(final String key, NetworkCallback<SearchResponse> callback) {
        //support one search at a time!
        if (searchResultCall != null) {
            searchResultCall.cancel();
        }

        webService.searchPoster(key).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, retrofit2.Response<SearchResult> response) {
                if (callback != null) {
                    callback.onResponse(SearchResponse.success(key, response.body()));
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                if (!call.isCanceled()) {
                    //Only post result if it's cancelled
                    callback.onResponse(SearchResponse.fail(key,t));
                }
            }
        });
    }
}
