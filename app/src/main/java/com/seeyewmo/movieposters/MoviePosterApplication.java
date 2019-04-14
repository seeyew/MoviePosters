package com.seeyewmo.movieposters;

import android.app.Application;

import com.seeyewmo.movieposters.di.AppComponent;
import com.seeyewmo.movieposters.di.AppModule;
import com.seeyewmo.movieposters.di.DaggerAppComponent;
import com.seeyewmo.movieposters.di.NetModule;
import com.seeyewmo.movieposters.di.RoomModule;

import retrofit2.Retrofit;

public class MoviePosterApplication extends Application {
    // https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2
    private static Retrofit retrofit;
    private static final String API = "http://www.omdbapi.com/";
    private static final String API_KEY = "82c4cc71";

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        setComponent(DaggerAppComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(API, API_KEY))
                .roomModule(new RoomModule())
                .build());
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public void setComponent(AppComponent component) {
        this.mAppComponent = component;
    }
}
