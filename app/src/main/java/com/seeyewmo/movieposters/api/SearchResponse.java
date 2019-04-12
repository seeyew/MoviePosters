package com.seeyewmo.movieposters.api;

public class SearchResponse {
    private final String term;
    private final SearchResult data;
    private final String error;
    private final boolean success;

    private SearchResponse(String term, boolean success, SearchResult data, String error) {
        this.term = term;
        this.data = data;
        this.success = success;
        this.error = error;
    }

    public SearchResult getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    static <T> SearchResponse success(String term, SearchResult data) {
        return new SearchResponse(term,true, data, null);
    }

    static <T> SearchResponse fail(String term, String throwable) {
        return new SearchResponse(term,false, null, throwable);
    }
}
