package com.seeyewmo.movieposters.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeyewmo.movieposters.GlideApp;
import com.seeyewmo.movieposters.R;
import com.seeyewmo.movieposters.dto.MoviePoster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private static final String TAG = "MovieSearchAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView mPosterImageView;
        TextView mTitleView;
        TextView mYearView;

        ViewHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
            mPosterImageView = itemView.findViewById(R.id.search_result_poster);
            mTitleView = itemView.findViewById(R.id.search_result_title);
            mYearView = itemView.findViewById(R.id.search_result_year);
        }
    }

    private List<MoviePoster> moviePosters = new ArrayList<>();

    public void setData(List<MoviePoster> posters) {
        Log.d(TAG, "Swap Data");
        moviePosters.clear();
        if (posters.size() > 0) {
            moviePosters.addAll(posters);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        Log.d(TAG, "Clear Data");
        setData(new ArrayList<>());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_poster_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoviePoster poster = moviePosters.get(position);
        String title = poster.getTitle();
        holder.mTitleView.setText(title);

        String year = poster.getYear();
        holder.mYearView.setText(year);

        String posterUrl = poster.getPoster();
        GlideApp
                .with(holder.mPosterImageView)
                .load(posterUrl)
                .centerCrop()
                .into(holder.mPosterImageView);

        final String id = poster.getImdbId();
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On click, we should open the item with the id
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Number of posters: " + moviePosters.size());
        return moviePosters.size();
    }

}