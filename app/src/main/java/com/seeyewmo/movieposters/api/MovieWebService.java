package com.seeyewmo.movieposters.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieWebService {

    @GET("/")
    Call<SearchResult> searchPoster(
            @Query("s")  String term);
}
