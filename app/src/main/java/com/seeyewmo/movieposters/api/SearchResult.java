package com.seeyewmo.movieposters.api;

import com.google.gson.annotations.SerializedName;

public class SearchResult {

    @SerializedName("totalResults")
    private int totalResults;
    @SerializedName("Response")
    private boolean response;

    public SearchResult(int totalResults, boolean response) {
        this.totalResults = totalResults;
        this.response = response;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public boolean isResponse() {
        return response;
    }

}
