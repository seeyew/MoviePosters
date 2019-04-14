package com.seeyewmo.movieposters.network.api;

import com.google.gson.annotations.SerializedName;
import com.seeyewmo.movieposters.dto.MoviePoster;

public class ApiSearchResponse {

    @SerializedName("totalResults")
    private int totalResults;
    @SerializedName("Response")
    private boolean response;

    @SerializedName("Search")
    private MoviePoster[] moviePosters;

    @SerializedName("Error")
    private String error;

    public ApiSearchResponse(int totalResults, boolean response) {
        this.totalResults = totalResults;
        this.response = response;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public boolean isSuccessful() {
        return response;
    }

    public MoviePoster[] getMoviePosters() {
        return moviePosters;
    }

    public void setMoviePosters(MoviePoster[] movies) {
        this.moviePosters = movies;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
