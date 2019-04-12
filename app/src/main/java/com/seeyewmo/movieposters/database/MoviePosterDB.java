package com.seeyewmo.movieposters.database;


import com.seeyewmo.movieposters.dto.MoviePoster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MoviePoster.class}, version = 1, exportSchema = false)
public abstract class MoviePosterDB extends RoomDatabase {
    public abstract MoviePosterDAO moviePosterDAO() ;
}
