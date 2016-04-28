package me.branded.hossamhassan.hossam_popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;
import me.branded.hossamhassan.hossam_popular_movies.Fragments.FragmentDetailed;
import me.branded.hossamhassan.hossam_popular_movies.Fragments.FragmentMoviesList;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;

public class ActivityMain extends AppCompatActivity implements FragmentMoviesList.OnMovieClickListenerFragment, FragmentDetailed.OnFabClickListener {
    FragmentMoviesList fragmentMoviesList;
    FragmentDetailed fragmentDetailed;
    boolean mTwoPane;
    String TAG = HossPopularMoviesApp.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(Const.FRAGMENT_MOVIES_LIST);
        if (fragment == null) {
            //create fragment
            fragmentMoviesList = new FragmentMoviesList();
            fragmentMoviesList.setOnMovieClickListenerFrag(this);
            //for fragment movies list
            setFragment(R.id.mainContainer, fragmentMoviesList, Const.FRAGMENT_MOVIES_LIST, 1);
        } else {
            // get our old fragment back !
            fragmentMoviesList = (FragmentMoviesList) fragment;
            fragmentMoviesList.setOnMovieClickListenerFrag(this);
        }


        /////////////////////////Handel mTwoPane
        if (findViewById(R.id.inch_5_layout) != null) {
            mTwoPane = false;
            // Handle Phone layout setup here
        } else {
            mTwoPane = true;
            Fragment fragmentD = fragmentManager.findFragmentByTag(Const.FRAGMENT_MOVIES_DETAILS);

            // Handle  Tablet (inch_7_layout ,inch_10_layout)setup here
            if (fragmentD == null) {

                fragmentDetailed = new FragmentDetailed();
                fragmentDetailed.setOnFabClickListener(this);
                //for tablet
                setFragment(R.id.mainContainerRight, fragmentDetailed, Const.FRAGMENT_MOVIES_DETAILS, 1);
            } else {
                // get our old fragment back !
                fragmentDetailed = (FragmentDetailed) fragmentD;
                fragmentDetailed.setOnFabClickListener(this);
            }
        }
        ///////////////////////////////////////
    }


    private void setFragment(int resourceID, Fragment fragment, String tag, int sector) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.sortTypeSelected, sector);
        int type = mTwoPane ? Const.DEVICE_TYPE_TABLET : Const.DEVICE_TYPE_PHONE;
        bundle.putInt(Const.DEVICE_TYPE, type);
        fragment.setArguments(bundle);
        ft.replace(resourceID, fragment, tag).commit();
        //        ft.replace(R.id.mainContainer, fragment, tag).commit();


    }


    void openDetailedActivity(Result movie) {
        Intent intent = new Intent(getBaseContext(), ActivityScrollingMovieDetails.class);
        intent.putExtra(Const.selectedMovie, movie);
        startActivity(intent);
    }

    @Override
    public void onMovieClickFrag(View v, Result movie) {
        if (mTwoPane) {
            fragmentDetailed.hideDefaultView();
            fragmentDetailed.updateViews(movie, Const.SET_VIEWS);
        } else {
            openDetailedActivity(movie);
        }

    }

    @Override
    public void onFabClick() {
        if (fragmentMoviesList.getFilterID() == 3) {
            fragmentMoviesList.getFavoriteMovies();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, Const.FRAGMENT_MOVIES_LIST, fragmentMoviesList);
        if (mTwoPane)
            getSupportFragmentManager().putFragment(outState, Const.FRAGMENT_MOVIES_DETAILS, fragmentDetailed);

    }

}
