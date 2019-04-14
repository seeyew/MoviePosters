package com.seeyewmo.movieposters.dto;

/**
 * Generic wrapper for Transporting DTOs
 * @param <T>
 */
public class Resource<T> {
    public enum Status {
        SUCCESS, ERROR, LOADING
    }

    private final Status status;
    private final T data;
    private final String error;

    private Resource(Status status, T data, String throwable) {
        this.status = status;
        this.data = data;
        this.error = throwable;
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getException() {
        return error;
    }

    public static <T> Resource<T> success( T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String exception, T data) {
        return new Resource<>(Status.ERROR, data, exception);
    }

    public static <T> Resource<T> loading( T data) {
        return new Resource<>(Status.LOADING, data, null);
    }
}