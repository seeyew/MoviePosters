package com.seeyewmo.movieposters.di.modules;

import android.app.Application;

import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.database.MoviePosterDB;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private static final String DATABASE_NAME = "movies_db";

    @Provides
    @Singleton
    protected MoviePosterDB provideMoviePosterDB(Application application) {
        return Room.databaseBuilder(application,
                MoviePosterDB.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
    }

    @Provides
    @Singleton
    protected MoviePosterDAO provideMoviePosterDAO(MoviePosterDB db) {
        return db.moviePosterDAO();
    }
}
