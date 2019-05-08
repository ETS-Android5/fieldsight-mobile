package org.bcss.collect.naxa.project.ui;

import android.view.View;
import android.widget.TextView;

import org.bcss.collect.android.R;

public class MoviesViewHolder extends ChildViewHolder {

    private TextView mMoviesTextView;

    public MoviesViewHolder(View itemView) {
        super(itemView);
        mMoviesTextView = (TextView) itemView.findViewById(R.id.tv_movies);
    }

    public void bind(Movies movies) {
        mMoviesTextView.setText(movies.getName());
    }
}
