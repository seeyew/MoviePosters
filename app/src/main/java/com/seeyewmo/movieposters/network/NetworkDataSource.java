package com.seeyewmo.movieposters.network;

import android.util.Log;

import com.seeyewmo.movieposters.network.api.MovieWebService;
import com.seeyewmo.movieposters.network.api.ApiSearchResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@Singleton
public class NetworkDataSource {
    private static final String TAG = NetworkDataSource.class.getSimpleName();
    private final MovieWebService webService;
    private Call<ApiSearchResponse> searchResultCall;

    @Inject
    public NetworkDataSource(Retrofit retrofit) {
        // Create an instance of our MovieWebService API interface.
        webService = retrofit.create(MovieWebService.class);
    }

    public void searchWithCallback(final String key, NetworkCallback<SearchResult> callback) {
        //support one search at a time!
        if (searchResultCall != null) {
            searchResultCall.cancel();
        }

        searchResultCall = webService.searchPoster(key);
        searchResultCall.enqueue(new Callback<ApiSearchResponse>() {
            @Override
            public void onResponse(Call<ApiSearchResponse> call,
                                   Response<ApiSearchResponse> response) {
                if (callback != null) {
                    ApiSearchResponse result = response.body();
                    if (response.isSuccessful() && result != null && result.isSuccessful()) {
                        Log.d(TAG, "Success returned from Retrofit");
                        callback.onResponse(SearchResult.success(key, result));
                    } else {
                        Log.d(TAG, "Failure returned from Retrofit");
                        callback.onResponse(SearchResult.fail(key, result != null ?
                                result.getError() : response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiSearchResponse> call, Throwable t) {
                if (!call.isCanceled() && callback != null) {
                    //Only post result if it's cancelled
                    Log.d(TAG, "Failure returned by Retrofit" + t.getLocalizedMessage());
                    callback.onResponse(SearchResult.fail(key,t.getLocalizedMessage()));
                }
            }
        });
    }
}
