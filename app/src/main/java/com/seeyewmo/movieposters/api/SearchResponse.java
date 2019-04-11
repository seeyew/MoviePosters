package com.seeyewmo.movieposters.api;

public class SearchResponse {
    private final SearchResult data;
    private final Throwable throwable;
    private final boolean success;

    private SearchResponse(boolean success, SearchResult data, Throwable t) {
        this.data = data;
        this.success = success;
        this.throwable = t;
    }

    public SearchResult getData() {
        return data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccess() {
        return success;
    }

    static <T> SearchResponse success(SearchResult data) {
        return new SearchResponse(true, data, null);
    }

    static <T> SearchResponse fail(Throwable throwable) {
        return new SearchResponse(false, null, throwable);
    }
}
