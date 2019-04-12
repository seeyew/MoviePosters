package com.seeyewmo.movieposters.Respository;

import com.seeyewmo.movieposters.api.NetworkCallback;
import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.api.SearchResponse;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.dto.Resource;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

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

        final LiveData<List<MoviePoster>> source = moviePosterDAO.searchMoviePoster(term);

        //We need to wrap resource in resource
        result.addSource(source, new Observer<List<MoviePoster>>() {
            @Override
            public void onChanged(List<MoviePoster> moviePosters) {
                if (moviePosters.size() > 0) {
                    result.setValue(Resource.success(moviePosters));
                } else {
                    result.setValue(Resource.loading(moviePosters));
                    networkDataSource.searchWithCallback(term, new NetworkCallback<SearchResponse>() {
                        @Override
                        public void onResponse(SearchResponse data) {
                            if (data.isSuccess()) {
                                executor.execute(()-> {
                                    MoviePoster[] posters = data.getData().getMoviePosters();
                                    for (MoviePoster poster : posters) {
                                        poster.setTerm(term);
                                    }
                                    moviePosterDAO.bulkInsert(posters);
                                });
                            } else {
                                result.postValue(Resource.error(data.getError(), null));
                            }
                        }
                    });
                }
            }
        });

        return result;
    }

//    public LiveData<User> getUser(String userLogin) {
//        refreshUser(userLogin); // try to refresh data if possible from Github Api
//        return userDao.load(userLogin); // return a LiveData directly from the database.
//    }
//
//    // ---
//
//    private void refreshUser(final String userLogin) {
//        executor.execute(() -> {
//            // Check if user was fetched recently
//            boolean userExists = (userDao.hasUser(userLogin, getMaxRefreshTime(new Date())) != null);
//            // If user have to be updated
//            if (!userExists) {
//                webservice.getUser(userLogin).enqueue(new Callback<User>() {
//                    @Override
//                    public void onResponse(Call<User> call, Response<User> response) {
//                        Log.e("TAG", "DATA REFRESHED FROM NETWORK");
//                        Toast.makeText(App.context, "Data refreshed from network !", Toast.LENGTH_LONG).show();
//                        executor.execute(() -> {
//                            User user = response.body();
//                            user.setLastRefresh(new Date());
//                            userDao.save(user);
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Call<User> call, Throwable t) { }
//                });
//            }
//        });
//    }

    // ---

//    private static int FRESH_TIMEOUT_IN_MINUTES = 1;
//    private Date getMaxRefreshTime(Date currentDate){
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(currentDate);
//        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
//        return cal.getTime();
//    }
}
