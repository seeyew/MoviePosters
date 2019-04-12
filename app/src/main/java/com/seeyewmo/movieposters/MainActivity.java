package com.seeyewmo.movieposters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import android.net.Network;
import android.os.Bundle;
import android.widget.TextView;

import com.seeyewmo.movieposters.Respository.MoviePostersRepository;
import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.api.SearchResponse;
import com.seeyewmo.movieposters.database.MoviePosterDB;
import com.seeyewmo.movieposters.dto.Resource;
import com.seeyewmo.movieposters.viewmodel.MoviePosterViewModel;
import com.seeyewmo.movieposters.viewmodel.MoviePosterViewModelFactory;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "movies_db";
    private MoviePosterDB movieDatabase;
    private MoviePosterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieDatabase = Room.databaseBuilder(getApplicationContext(),
                MoviePosterDB.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        viewModel = ViewModelProviders.of(this,
                new MoviePosterViewModelFactory(new MoviePostersRepository(new NetworkDataSource(),
                        movieDatabase.moviePosterDAO(), Executors.newCachedThreadPool())))
                .get(MoviePosterViewModel.class);

        final TextView result = findViewById(R.id.result);

        viewModel.getResults().observe(this, searchResponse -> {
            if (searchResponse.getStatus() == Resource.Status.SUCCESS) {
                result.setText("Total Results" + searchResponse.getData().size());
            } else if (searchResponse.getStatus() == Resource.Status.ERROR){
                result.setText(searchResponse.getException().getMessage());
            }
        });

        viewModel.searchText("the last of the");

//        NetworkDataSource source = new NetworkDataSource();
//        source.search("my name is").observe(this, new Observer<SearchResponse>() {
//            @Override
//            public void onChanged(SearchResponse searchResponse) {
//
//                if (searchResponse.isSuccess()) {
//                    result.setText("Total Results" + searchResponse.getData().getTotalResults());
//                } else {
//                    result.setText(searchResponse.getThrowable().getMessage());
//                }
//            }
//        });
    }
}
