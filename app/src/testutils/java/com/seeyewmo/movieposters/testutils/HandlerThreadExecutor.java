package com.seeyewmo.movieposters.testutils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class HandlerThreadExecutor implements Executor {
    private final Handler mHandler;
    public HandlerThreadExecutor(Handler optionalHandler) {
        mHandler = optionalHandler != null ? optionalHandler : new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
