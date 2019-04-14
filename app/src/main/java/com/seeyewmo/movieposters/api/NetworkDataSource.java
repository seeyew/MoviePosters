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

    private final MovieWebService webService;
    private Call<SearchResult> searchResultCall;

    public NetworkDataSource(Retrofit retrofit) {
        // Create an instance of our GitHub API interface.
        webService = retrofit.create(MovieWebService.class);
    }

    public void searchWithCallback(final String key, NetworkCallback<SearchResponse> callback) {
        //support one search at a time!
        if (searchResultCall != null) {
            searchResultCall.cancel();
        }

        searchResultCall = webService.searchPoster(key);
        searchResultCall.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, retrofit2.Response<SearchResult> response) {
                if (callback != null) {
                    SearchResult result = response.body();
                    if (result.isSuccessful()) {
                        callback.onResponse(SearchResponse.success(key, result));
                    } else {
                        callback.onResponse(SearchResponse.fail(key, result.getError()));
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                if (!call.isCanceled()) {
                    //Only post result if it's cancelled
                    callback.onResponse(SearchResponse.fail(key,t.getLocalizedMessage()));
                }
            }
        });
    }
}
