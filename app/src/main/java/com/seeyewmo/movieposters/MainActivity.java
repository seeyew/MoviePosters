package com.seeyewmo.movieposters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.seeyewmo.movieposters.api.MovieWebService;
import com.seeyewmo.movieposters.api.SearchResult;
import com.seeyewmo.movieposters.respository.MoviePostersRepository;
import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.database.MoviePosterDB;
import com.seeyewmo.movieposters.dto.Resource;
import com.seeyewmo.movieposters.ui.MoviePosterAdapter;
import com.seeyewmo.movieposters.viewmodel.MoviePosterViewModel;
import com.seeyewmo.movieposters.viewmodel.MoviePosterViewModelFactory;

import java.util.concurrent.Executors;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
//    private static final String DATABASE_NAME = "movies_db";

    @Inject
    MoviePostersRepository repository;

    private MoviePosterViewModel viewModel;
    private RecyclerView mRecyclerView;
    private MoviePosterAdapter mAdapter;
    private TextView resultView;
    private String queryTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MoviePosterApplication) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_main);

//        movieDatabase = Room.databaseBuilder(getApplicationContext(),
//                MoviePosterDB.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        viewModel = ViewModelProviders.of(this,
                new MoviePosterViewModelFactory(repository,
                        savedInstanceState == null ? null : savedInstanceState.getString(QUERY_KEY)))
                .get(MoviePosterViewModel.class);

        resultView = findViewById(R.id.status);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MoviePosterAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getResults().observe(this, searchResponse -> {
            if (searchResponse.getStatus() == Resource.Status.SUCCESS) {

                //TODO this might have to be moved into viewmodel
                if (searchResponse.getData().size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    resultView.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    resultView.setVisibility(View.VISIBLE);
                    resultView.setText(R.string.no_results);
                }
                mAdapter.setData(searchResponse.getData());
                return;
            }

            //In other cases, it's loading, so let's show status
            mRecyclerView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);

            if (searchResponse.getStatus() == Resource.Status.ERROR){
                resultView.setText(getString(R.string.error, searchResponse.getException()));
            } else {
                resultView.setText(getString(R.string.loading));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Query: " + query);
            queryTerm = query;
            viewModel.searchText(query);
            mAdapter.clearData();
        }
    }

    private static final String QUERY_KEY = "query";
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (!TextUtils.isEmpty(queryTerm)) {
            outState.putString(QUERY_KEY, queryTerm);
        }
        super.onSaveInstanceState(outState);
    }
}
