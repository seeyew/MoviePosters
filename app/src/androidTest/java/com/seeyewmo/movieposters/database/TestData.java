package com.seeyewmo.movieposters.database;

import com.seeyewmo.movieposters.dto.MoviePoster;

import java.util.Arrays;
import java.util.List;

public class TestData {

    static final MoviePoster MOVIE_POSTER1 = new MoviePoster();
    static final MoviePoster MOVIE_POSTER2 = new MoviePoster();

    static final List<MoviePoster> POSTERS = Arrays.asList(MOVIE_POSTER1, MOVIE_POSTER2);

    static {

        MoviePoster current;
        for (int i = 0; i < POSTERS.size(); i++){
            String value = Integer.toString(i);
            current = POSTERS.get(i);
            current.setImdbId(value);
            current.setTerm(value);
            current.setPoster("");
            current.setTitle(value);
            current.setType(value);
        }
    }

}
