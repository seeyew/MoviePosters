package com.seeyewmo.movieposters.api;

public interface NetworkCallback<T> {

    void onResponse(T data);

//    void onFailure(Throwable t);
}
