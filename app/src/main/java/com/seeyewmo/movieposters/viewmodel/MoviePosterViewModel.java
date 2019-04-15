package com.seeyewmo.movieposters.viewmodel;

import android.util.Log;

import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;
import com.seeyewmo.movieposters.respository.MoviePostersRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MoviePosterViewModel extends ViewModel {
    private static final String TAG = MoviePosterViewModel.class.getSimpleName();
    private String currentSearchTerm;
    private MutableLiveData<String> query = new MutableLiveData<>();
    private LiveData<Resource<List<MoviePoster>>> results;

    MoviePosterViewModel(final MoviePostersRepository repository) {
        results = Transformations.switchMap(query, term -> {
            currentSearchTerm = term;
            return repository.searchMoviePosters(term);
        });
    }

    public void searchText(final String term) {
        if (term == null || term.isEmpty()) {
            Log.d(TAG, "Not setting quary because it's  null ");
            return;
        }
        query.setValue(term.toLowerCase());
    }

    public LiveData<Resource<List<MoviePoster>>> getResults() {
        return results;
    }
}
