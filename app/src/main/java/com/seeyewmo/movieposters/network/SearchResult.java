package com.seeyewmo.movieposters.network;

import com.seeyewmo.movieposters.network.api.ApiSearchResponse;

public class SearchResult {
    private final String term;
    private final ApiSearchResponse data;
    private final String error;
    private final boolean success;

    private SearchResult(String term, boolean success, ApiSearchResponse data, String error) {
        this.term = term;
        this.data = data;
        this.success = success;
        this.error = error;
    }

    public ApiSearchResponse getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTerm() {
        return term;
    }

    static SearchResult success(String term, ApiSearchResponse data) {
        return new SearchResult(term,true, data, null);
    }

    static SearchResult fail(String term, String throwable) {
        return new SearchResult(term,false, null, throwable);
    }
}
