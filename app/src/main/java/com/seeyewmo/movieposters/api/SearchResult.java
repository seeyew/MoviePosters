package com.seeyewmo.movieposters.api;

import com.google.gson.annotations.SerializedName;
import com.seeyewmo.movieposters.dto.MoviePoster;

public class SearchResult {

    @SerializedName("totalResults")
    private int totalResults;
    @SerializedName("Response")
    private boolean response;

    @SerializedName("Search")
    private MoviePoster[] moviePosters;

    @SerializedName("Error")
    private String error;

    public SearchResult(int totalResults, boolean response) {
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

    public String getError() {
        return error;
    }
}
