package com.seeyewmo.movieposters;

import android.app.Application;

import com.seeyewmo.movieposters.di.AppComponent;
import com.seeyewmo.movieposters.di.modules.AppModule;
import com.seeyewmo.movieposters.di.DaggerAppComponent;
import com.seeyewmo.movieposters.di.modules.NetModule;
import com.seeyewmo.movieposters.di.modules.RoomModule;

public class MoviePosterApplication extends Application {
    //TODO: For release, these information should be stored as resources and gradle BuildConfigs
    private static final String API = "http://www.omdbapi.com/";
    private static final String API_KEY = "82c4cc71";

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        setComponent(DaggerAppComponent.builder()
                // This also corresponds to the name of your module: %component_name%Module
                .appModule(new AppModule(this))
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
