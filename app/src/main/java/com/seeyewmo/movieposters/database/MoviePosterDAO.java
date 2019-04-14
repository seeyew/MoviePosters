package com.seeyewmo.movieposters.database;

import com.seeyewmo.movieposters.dto.MoviePoster;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MoviePosterDAO {

    @Query("SELECT title, year, imdbId, type, poster, searchTerm FROM movieposter WHERE searchTerm= :term")
    LiveData<List<MoviePoster>> searchMoviePosters(String term);

    @Query("SELECT * FROM movieposter WHERE imdbId = :id")
    LiveData<MoviePoster> getMoviePosterByTerm(String id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(MoviePoster... moviePosters);

    @Query("DELETE FROM movieposter WHERE searchTerm = :term")
    void deleteOldMoviePosters(String term);

    @Query("DELETE FROM movieposter")
    void deleteAll();

}
