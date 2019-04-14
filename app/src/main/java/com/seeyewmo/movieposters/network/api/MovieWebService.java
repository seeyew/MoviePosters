package com.seeyewmo.movieposters.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieWebService {

    @GET("/")
    Call<ApiSearchResponse> searchPoster(@Query("s")  String term);
}
