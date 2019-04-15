package com.seeyewmo.movieposters.viewmodel;

import com.seeyewmo.movieposters.respository.MoviePostersRepository;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ViewModelTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private MoviePosterViewModel viewModel;

    @Mock
    private MoviePostersRepository repository;

    @Mock
    private Observer<Resource<List<MoviePoster>>> observer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        viewModel = new MoviePosterViewModel(repository);
        //Need to do this to ensure MediatorLiveData broadcast an event.
        viewModel.getResults().observeForever(observer);
    }

    @Test
    public void testSearchWithEmptyTextReturnsQuickly() {
        String searchQuery = null;

        // Trigger
        viewModel.searchText(searchQuery);

        // Validation
        Mockito.verify(repository, Mockito.never()).searchMoviePosters(searchQuery);

    }

    @Test
    public void testSearchTextTriggersRepositoryCall() {
        String searchQuery = "test query";
        when(repository.searchMoviePosters(searchQuery)).thenReturn(Mockito.mock(LiveData.class));
        // Trigger
        viewModel.searchText(searchQuery);

        // Validation
        Mockito.verify(repository, Mockito.times(1)).searchMoviePosters(searchQuery);
    }

    @Test
    public void testSearchTextRepeatQuery() {
        String searchQuery = "test query";
        when(repository.searchMoviePosters(searchQuery)).thenReturn(Mockito.mock(LiveData.class));
        // Trigger
        viewModel.searchText(searchQuery);
        // Trigger the second time
        viewModel.searchText(searchQuery);

        // Validation
        Mockito.verify(repository, Mockito.times(1)).searchMoviePosters(searchQuery);
    }

}