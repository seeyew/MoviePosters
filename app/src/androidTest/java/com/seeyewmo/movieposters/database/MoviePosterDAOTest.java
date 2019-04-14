package com.seeyewmo.movieposters.database;

import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.testutils.LiveDataTestUtil;
import com.seeyewmo.movieposters.testutils.TestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MoviePosterDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MoviePosterDB database;

    private MoviePosterDAO moviePosterDAO;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                MoviePosterDB.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
        moviePosterDAO = database.moviePosterDAO();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void getMoviePostersBeforeInsert() throws InterruptedException {
        List<MoviePoster> moviePosters = LiveDataTestUtil.getValue(
                moviePosterDAO.searchMoviePosters("S"));

        assertTrue(moviePosters.isEmpty());
    }


    @Test
    public void getMoviePostersAfterInsert() throws InterruptedException {
        moviePosterDAO.bulkInsert(TestData.POSTERS.stream().toArray(MoviePoster[]::new));

        List<MoviePoster> moviePosters = LiveDataTestUtil.getValue(
                moviePosterDAO.searchMoviePosters(TestData.MOVIE_POSTER1.getSearchTerm()));

        assertThat(moviePosters.size(), is(1));
    }

    @Test
    public void getMoviePosterByImdbId() throws InterruptedException {
        moviePosterDAO.bulkInsert(TestData.POSTERS.stream().toArray(MoviePoster[]::new));

        MoviePoster moviePoster = LiveDataTestUtil.getValue(
                moviePosterDAO.getMoviePosterByTerm(TestData.MOVIE_POSTER2.getImdbId()));

        assertNotNull(moviePoster);
        assertEquals(TestData.MOVIE_POSTER2.getImdbId(), moviePoster.getImdbId());
    }

    @Test
    public void testDelete() throws InterruptedException {
        moviePosterDAO.bulkInsert(TestData.POSTERS.stream().toArray(MoviePoster[]::new));


        moviePosterDAO.deleteOldMoviePosters(TestData.MOVIE_POSTER2.getImdbId());

        List<MoviePoster> moviePoster = LiveDataTestUtil.getValue(
                moviePosterDAO.searchMoviePosters(TestData.MOVIE_POSTER2.getSearchTerm()));
        assertEquals(0, moviePoster.size() );
    }
}
