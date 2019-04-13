package com.seeyewmo.movieposters.respository;

import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class MoviePostersRepository {

    private final NetworkDataSource networkDataSource;
    private final MoviePosterDAO moviePosterDAO;
    private final Executor executor;


    public MoviePostersRepository(NetworkDataSource networkDataSource, MoviePosterDAO moviePosterDAO,
                                  Executor executor) {
        this.networkDataSource = networkDataSource;
        this.moviePosterDAO = moviePosterDAO;
        this.executor = executor;
    }

    public LiveData<Resource<List<MoviePoster>>> searchMoviePosters(final String term) {
        final MediatorLiveData<Resource<List<MoviePoster>>> result = new MediatorLiveData<>();
        if (term == null || term.length() == 0) {
            result.setValue(Resource.success(Collections.emptyList()));
            return result;
        }

        final LiveData<List<MoviePoster>> source = moviePosterDAO.searchMoviePosters(term);

        //We need to wrap resource in resource
        result.addSource(source, moviePosters -> {
            if (moviePosters.size() > 0) {
                result.postValue(Resource.success(moviePosters));
            } else {
               fetchFromNetwork(result, term);
            }
        });

        return result;
    }

    private void fetchFromNetwork(final MutableLiveData<Resource<List<MoviePoster>>> result,
                                  final String term) {
        result.setValue(Resource.loading(Collections.emptyList()));
        networkDataSource.searchWithCallback(term, data ->  {
            if (data.isSuccess()) {
                executor.execute(() -> {
                    MoviePoster[] posters = data.getData().getMoviePosters();
                    for (MoviePoster poster : posters) {
                        poster.setTerm(term);
                    }
                    moviePosterDAO.bulkInsert(posters);
                });
            } else {
                result.postValue(Resource.error(data.getError(), null));
            }
        });

    }
}
