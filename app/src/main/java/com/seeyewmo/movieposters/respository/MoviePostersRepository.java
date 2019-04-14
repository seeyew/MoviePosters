package com.seeyewmo.movieposters.respository;

import android.util.Log;

import com.seeyewmo.movieposters.network.NetworkDataSource;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

@Singleton
public class MoviePostersRepository {
    private static final String TAG = MoviePostersRepository.class.getSimpleName();
    private final NetworkDataSource networkDataSource;
    private final MoviePosterDAO moviePosterDAO;
    private final Executor executor;

    @Inject
    public MoviePostersRepository(NetworkDataSource networkDataSource, MoviePosterDAO moviePosterDAO,
                                  Executor executor) {
        this.networkDataSource = networkDataSource;
        this.moviePosterDAO = moviePosterDAO;
        this.executor = executor;
    }

    /**
     * Search MoviePosters by term. This function returns data from Room Database. If there's no
     * data, we will download from the server.
     *
     * TODO: Need to implement cache expiration
     * @param term
     * @return LiveData of a Resource<List<MoviePoster>>. Guaranteeed to not be null.
     */
    public LiveData<Resource<List<MoviePoster>>> searchMoviePosters(final String term) {
        final MediatorLiveData<Resource<List<MoviePoster>>> result = new MediatorLiveData<>();
        if (term == null || term.length() == 0) {
            result.setValue(Resource.success(Collections.emptyList()));
            return result;
        }

        final LiveData<List<MoviePoster>> source = moviePosterDAO.searchMoviePosters(term);

        //We need to wrap result in resource
        result.addSource(source, moviePosters -> {
            if (moviePosters.size() > 0) {
                Log.d(TAG, "Returning cache results");
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
                    Log.d(TAG, "Caching online results");
                    MoviePoster[] posters = data.getData().getMoviePosters();
                    for (MoviePoster poster : posters) {
                        poster.setSearchTerm(term);
                    }
                    moviePosterDAO.bulkInsert(posters);
                });
            } else {
                Log.d(TAG, "Online download failed");
                result.postValue(Resource.error(data.getError(), null));
            }
        });
    }
}
