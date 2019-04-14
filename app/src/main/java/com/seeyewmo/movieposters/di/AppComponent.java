package com.seeyewmo.movieposters.di;

import com.seeyewmo.movieposters.MainActivity;
import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.database.MoviePosterDB;
import com.seeyewmo.movieposters.respository.MoviePostersRepository;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules={AppModule.class, NetModule.class, RoomModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    // void inject(MainActivityTest test);

    MoviePosterDAO moviePosterDAO();
    MoviePosterDB moviePosterDB();
    MoviePostersRepository repository();
    NetworkDataSource networkDataSource();
    OkHttpClient httpclient();
}