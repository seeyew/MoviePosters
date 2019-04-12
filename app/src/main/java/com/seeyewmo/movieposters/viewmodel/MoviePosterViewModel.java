package com.seeyewmo.movieposters.viewmodel;

import com.seeyewmo.movieposters.Respository.MoviePostersRepository;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import okhttp3.Response;

public class MoviePosterViewModel extends ViewModel {
    private final MoviePostersRepository repository;
    private MutableLiveData<String> query = new MutableLiveData<>();
    private LiveData<Resource<List<MoviePoster>>> results;

    MoviePosterViewModel(MoviePostersRepository repository) {
        this.repository = repository;
        results = Transformations.switchMap(query, term -> repository.searchMoviePosters(term));
    }

    public void searchText(final String term) {
        query.setValue(term);
    }

    public LiveData<Resource<List<MoviePoster>>> getResults() {
        return results;
    }
}
