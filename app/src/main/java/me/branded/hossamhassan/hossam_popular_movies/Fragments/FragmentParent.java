package me.branded.hossamhassan.hossam_popular_movies.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import me.branded.hossamhassan.hossam_popular_movies.R;


/**
 * Created by Hossam on 14/04/2016.
 */
public abstract class FragmentParent extends Fragment {
    private View errorView;
    private TextView emptyView;
    private ImageButton btnRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(getLayoutResources(), container, false);
        findViews(mainView);
        return mainView;
    }

    private void findViews(View mainView) {
        errorView = mainView.findViewById(R.id.errorView);
        emptyView = (TextView) mainView.findViewById(R.id.emptyView);
        btnRefresh = (ImageButton) errorView.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshClick();
            }
        });

    }


    protected void showError() {
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

    }

    protected void showEmpty() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    protected void hide() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    protected abstract void onRefreshClick();

    protected abstract int getLayoutResources();
}
