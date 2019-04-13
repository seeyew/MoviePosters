package com.seeyewmo.movieposters.viewmodel;

import com.seeyewmo.movieposters.respository.MoviePostersRepository;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static org.mockito.Mockito.when;

public class ViewModelTest {
    //https://github.com/ericntd/Github-Search/blob/step-2-mvvm-databinding/app/src/test/java/tech/ericntd/githubsearch/search/SearchViewModelTest.java

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private MoviePosterViewModel viewModel;

    @Mock
    private MoviePostersRepository repository;

    @Mock
    private Observer<Resource<List<MoviePoster>>> observer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);// required for the "@Mock" annotations

        // Make viewModel a mock while using mock repository and viewContract created above
//        viewModel = Mockito.spy(new MoviePosterViewModel(repository));
        viewModel = new MoviePosterViewModel(repository);

        //Need to do this to ensure MediatorliveData broadcast an event.
        viewModel.getResults().observeForever(observer);
    }

    @Test
    public void searchMoviePoster_noQuery() {
        String searchQuery = null;

        // Trigger
        viewModel.searchText(searchQuery);

        // Validation
        Mockito.verify(repository, Mockito.never()).searchMoviePosters(searchQuery);

    }

    @Test
    public void searchGitHubRepos() {
        String searchQuery = "test query";
        when(repository.searchMoviePosters(searchQuery)).thenReturn(Mockito.mock(LiveData.class));
        // Trigger
        viewModel.searchText(searchQuery);

        // Validation
        Mockito.verify(repository, Mockito.times(1)).searchMoviePosters(searchQuery);
    }

}