package com.seeyewmo.movieposters.network;

public interface NetworkCallback<T> {

    void onResponse(T data);
}
