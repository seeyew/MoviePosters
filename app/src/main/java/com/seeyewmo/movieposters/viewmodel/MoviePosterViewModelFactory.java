package com.seeyewmo.movieposters.viewmodel;

import com.seeyewmo.movieposters.Respository.MoviePostersRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MoviePosterViewModelFactory implements ViewModelProvider.Factory {
    private final MoviePostersRepository repository;
    public MoviePosterViewModelFactory(MoviePostersRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MoviePosterViewModel(repository);
    }
}