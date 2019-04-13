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

    @Query("SELECT id, title, year, imdbId, type, poster, term FROM movieposter WHERE term= :term")
    LiveData<List<MoviePoster>> searchMoviePosters(String term);

    @Query("SELECT * FROM movieposter WHERE id = :id")
    LiveData<MoviePoster> getMoviePosterByTerm(String id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(MoviePoster... moviePosters);

    @Query("DELETE FROM movieposter WHERE term = :term")
    void deleteOldMoviePosters(String term);

}
