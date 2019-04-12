package com.seeyewmo.movieposters.viewmodel;

import com.seeyewmo.movieposters.Respository.MoviePostersRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MoviePosterViewModelFactory implements ViewModelProvider.Factory {
    private final MoviePostersRepository repository;
    private String queryTerm;
    public MoviePosterViewModelFactory(MoviePostersRepository repository, String queryTerm) {
        this.repository = repository;
        this.queryTerm = queryTerm;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        MoviePosterViewModel vm = new MoviePosterViewModel(repository);
        if (queryTerm != null) {
            vm.searchText(queryTerm);
        }
        return (T) vm;
    }
}