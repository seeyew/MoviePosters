package com.seeyewmo.movieposters.api;

public class SearchResponse {
    private final String term;
    private final SearchResult data;
    private final Throwable throwable;
    private final boolean success;

    private SearchResponse(String term, boolean success, SearchResult data, Throwable t) {
        this.term = term;
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

    static <T> SearchResponse success(String term, SearchResult data) {
        return new SearchResponse(term,true, data, null);
    }

    static <T> SearchResponse fail(String term, Throwable throwable) {
        return new SearchResponse(term,false, null, throwable);
    }
}
