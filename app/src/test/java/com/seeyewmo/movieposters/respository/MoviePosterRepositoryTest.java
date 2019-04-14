package com.seeyewmo.movieposters.respository;

import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;
import com.seeyewmo.movieposters.network.NetworkCallback;
import com.seeyewmo.movieposters.network.NetworkDataSource;
import com.seeyewmo.movieposters.network.SearchResult;
import com.seeyewmo.movieposters.network.api.ApiSearchResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MoviePosterRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock private NetworkDataSource networkDataSource;
    @Mock private MoviePosterDAO moviePosterDAO;
    @Mock private Executor executor;
    @Mock private Observer<Resource<List<MoviePoster>>> observer;

    private MoviePostersRepository repository;
    private MutableLiveData liveDataToReturn;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        repository = new MoviePostersRepository(networkDataSource,
                                    moviePosterDAO, executor);
        liveDataToReturn = new MutableLiveData<>();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchMoviePosterReturnCache() {
        String searchQuery = "test query";
        List<MoviePoster> posters = Mockito.mock(List.class);
        liveDataToReturn.setValue(posters);

        when(posters.size()).thenReturn(10);
        when(moviePosterDAO.searchMoviePosters(searchQuery)).thenReturn(liveDataToReturn);

        LiveData<Resource<List<MoviePoster>>> results = repository.searchMoviePosters(searchQuery);
        results.observeForever(observer);

        verifyZeroInteractions(networkDataSource);
        verify(moviePosterDAO, Mockito.times(1)).searchMoviePosters(searchQuery);
        Assert.assertEquals(Resource.Status.SUCCESS, results.getValue().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchMoviePosterFromNetworkFail() {
        final SearchResult searchResult = Mockito.mock(SearchResult.class);
        when(searchResult.isSuccess()).thenReturn(false);
        searchMoviePosterFromNetworkHelper(searchResult, Resource.Status.ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchMoviePosterFromNetworkSuccess() {
        //Captures Executor.execute and run it!
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(executor).execute(Mockito.any());

        final SearchResult searchResponse = Mockito.mock(SearchResult.class);
        when(searchResponse.isSuccess()).thenReturn(true);

        ApiSearchResponse apiSearchResponse = Mockito.mock(ApiSearchResponse.class);
        when(searchResponse.getData()).thenReturn(apiSearchResponse);

        final MoviePoster[] moviePosters = new MoviePoster[]{new MoviePoster()};
        when(apiSearchResponse.getMoviePosters()).thenReturn(moviePosters);

        //It is loading because we are faking insertiong into database. That's why the call back
        //from database did not happen and hence we are not setting status back to success
        searchMoviePosterFromNetworkHelper(searchResponse, Resource.Status.LOADING);
        verify(executor, Mockito.times(1)).execute(Mockito.any());
        verify(moviePosterDAO, Mockito.times(1)).bulkInsert(moviePosters);
    }

    @SuppressWarnings("unchecked")
    public void searchMoviePosterFromNetworkHelper(SearchResult searchResult,
                                                   Resource.Status expectedStatus) {
        String searchQuery = "test query";
        List<MoviePoster> posters = Mockito.mock(List.class);
        liveDataToReturn.setValue(posters);

        when(posters.size()).thenReturn(0);
        when(moviePosterDAO.searchMoviePosters(searchQuery)).thenReturn(liveDataToReturn);

        doAnswer(invocation -> {
            ((NetworkCallback<SearchResult>) invocation.getArguments()[1])
                    .onResponse(searchResult);
            return null;
        }).when(networkDataSource).searchWithCallback(eq(searchQuery), any());

        //Run the test
        LiveData<Resource<List<MoviePoster>>> results = repository.searchMoviePosters(searchQuery);
        results.observeForever(observer);

        //Validation
        verify(moviePosterDAO, Mockito.times(1))
                .searchMoviePosters(searchQuery);
        verify(networkDataSource, Mockito.times(1))
                .searchWithCallback(eq(searchQuery), any());
        Assert.assertEquals(expectedStatus, results.getValue().getStatus());
    }

}
