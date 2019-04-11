package com.seeyewmo.movieposters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.net.Network;
import android.os.Bundle;
import android.widget.TextView;

import com.seeyewmo.movieposters.api.NetworkDataSource;
import com.seeyewmo.movieposters.api.SearchResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView result = findViewById(R.id.result);

        NetworkDataSource source = new NetworkDataSource();
        source.search("Hello").observe(this, new Observer<SearchResponse>() {
            @Override
            public void onChanged(SearchResponse searchResponse) {

                if (searchResponse.isSuccess()) {
                    result.setText(searchResponse.getData().getTotalResults());
                } else {
                    result.setText(searchResponse.getThrowable().getMessage());
                }
            }
        });
    }
}
