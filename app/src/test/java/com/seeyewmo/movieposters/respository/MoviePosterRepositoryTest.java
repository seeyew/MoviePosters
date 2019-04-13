package com.seeyewmo.movieposters.respository;

import com.seeyewmo.movieposters.api.NetworkCallback;
import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.api.SearchResponse;
import com.seeyewmo.movieposters.api.SearchResult;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import retrofit2.Callback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MoviePosterRepositoryTest {
//    https://github.com/ericntd/Github-Search/blob/step-2-mvvm-databinding/app/src/test/java/tech/ericntd/githubsearch/repositories/GitHubRepositoryTest.java
//    https://github.com/skydoves/TheMovies/blob/master/app/src/test/java/com/skydoves/themovies/api/repository/MovieRepositoryTest.kt

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
    public void searchMoviePoster_Query_Cache() {
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
    public void searchMoviePoster_Query_FromNetwork_Filure() {
        final SearchResponse searchResponse = Mockito.mock(SearchResponse.class);
        when(searchResponse.isSuccess()).thenReturn(false);
        searchMoviePoster_Query_FromNetwork(searchResponse, Resource.Status.ERROR);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void searchMoviePoster_Query_FromNetwork_Success() {
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(executor).execute(Mockito.any());
        final SearchResponse searchResponse = Mockito.mock(SearchResponse.class);
        when(searchResponse.isSuccess()).thenReturn(true);
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        when(searchResponse.getData()).thenReturn(searchResult);
        MoviePoster[] moviePosters = new MoviePoster[]{new MoviePoster()};
        when(searchResult.getMoviePosters()).thenReturn(moviePosters);

        //It is loading because we are faking insertiong into database. That's why the call back
        //from database did not happen and hence we are not setting status back to success
        searchMoviePoster_Query_FromNetwork(searchResponse, Resource.Status.LOADING);
        verify(executor, Mockito.times(1)).execute(Mockito.any());
        verify(moviePosterDAO, Mockito.times(1)).bulkInsert(moviePosters);
    }

    @SuppressWarnings("unchecked")
    public void searchMoviePoster_Query_FromNetwork(SearchResponse searchResponse, Resource.Status expectedStatus) {
        String searchQuery = "test query";
        List<MoviePoster> posters = Mockito.mock(List.class);
        liveDataToReturn.setValue(posters);

        when(posters.size()).thenReturn(0);
        when(moviePosterDAO.searchMoviePosters(searchQuery)).thenReturn(liveDataToReturn);


        doAnswer(invocation -> {
            ((NetworkCallback<SearchResponse>) invocation.getArguments()[1]).onResponse(searchResponse);
            return null;
        }).when(networkDataSource).searchWithCallback(eq(searchQuery), any());

        //Run the test
        LiveData<Resource<List<MoviePoster>>> results = repository.searchMoviePosters(searchQuery);
        results.observeForever(observer);

        //Validation
        verify(moviePosterDAO, Mockito.times(1)).searchMoviePosters(searchQuery);
        verify(networkDataSource, Mockito.times(1)).searchWithCallback(eq(searchQuery), any());

    }

}
