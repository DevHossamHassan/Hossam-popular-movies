package me.branded.hossamhassan.hossam_popular_movies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.branded.hossamhassan.hossam_popular_movies.Adapters.AdapterResultsList;
import me.branded.hossamhassan.hossam_popular_movies.Api.ApiRequests;
import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;
import me.branded.hossamhassan.hossam_popular_movies.Data.DBManager;
import me.branded.hossamhassan.hossam_popular_movies.Fragments.FragmentReviews;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import me.branded.hossamhassan.hossam_popular_movies.Models.ResultArray;
import me.branded.hossamhassan.hossam_popular_movies.Models.VideoResult;
import me.branded.hossamhassan.hossam_popular_movies.Utils.Internet;
import me.branded.hossamhassan.hossam_popular_movies.Utils.PreferencesUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivityScrollingMovieDetails extends AppCompatActivity {
    @Bind(R.id.progressFrame)
    FrameLayout progressFrame;
    @Bind(R.id.tvMovieTitleOfficial)
    TextView tvMovieOfficial;
    @Bind(R.id.tvMovieReleaseDate)
    TextView tvMovieReleaseDate;
    @Bind(R.id.tvRatingText)
    TextView tvMovieratingText;
    @Bind(R.id.rating_bar)
    RatingBar movieRatingBar;
    @Bind(R.id.tvMovieOverView)
    TextView tvMovieOverView;
    List<Result> favMovies;
    String TAG = HossPopularMoviesApp.TAG;

    @OnClick(R.id.backgroundMovieOverlay)
    void onVideoClicked() {
        requestVideoId(true);
    }

    private ApiRequests apiRequests;

    private List<Result> resultsList;
    private AdapterResultsList resultsAdapter;
    private HossPopularMoviesApp appController;
    private Retrofit retrofit;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.backgroundMovie)
    ImageView imgBackgroundMovie;
    @Bind(R.id.moviePoster)
    ImageView imgMoviePoster;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    boolean isFavorite = false;
    boolean isFavoriteStateChanged = false;
    boolean favoriteState;
    Result selectedMovie;
    DBManager dbManager;

    @OnClick(R.id.fab)
    void onFavorite() {
        String msg = isFavorite ? "removed from your favorite Movies list" : "added to your favorite movies list";
        Snackbar.make(fab, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (isFavorite) {
            //removed
            //change color to white as to add it to favorite movies list
            fab.setImageResource(R.drawable.ic_make_favorite);
            isFavorite = false;
            dbManager.deleteFavoriteMovie(selectedMovie.getId());
        } else {
            isFavorite = true;
            //added
            //change color to red as to remove it from favorite movies list
            fab.setImageResource(R.drawable.ic_remove_favorite);
            dbManager.addFavoriteMovie(selectedMovie);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_scrolling_movie_details);
        ButterKnife.bind(this);
        init();
        setActionBar();
        getExtras();
        getFavoritesList();
        isFavorite = isFavoriteMovie(selectedMovie.getId());
        favoriteState = isFavorite;
        setFavoriteButtonState();
        setFragment(new FragmentReviews(), Const.FRAGMENT_MOVIE_REVIEWS, selectedMovie.getId());

    }

    void setFavoriteButtonState() {
        if (!isFavorite) {
            //removed
            //change color to white as to add it to favorite movies list
            fab.setImageResource(R.drawable.ic_make_favorite);
        } else {
            //added
            //change color to red as to remove it from favorite movies list
            fab.setImageResource(R.drawable.ic_remove_favorite);
        }
    }

    void getFavoritesList() {
        favMovies = dbManager.getFavoriteMoviesList();
    }

    boolean isFavoriteMovie(int movieId) {
        if (favMovies.isEmpty()) return false;
        for (Result movie : favMovies) {
            if (movieId == movie.getId()) {
                return true;
            }
        }
        return false;
    }


    void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_scrolling_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                requestVideoId(false);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    void init() {
        resultsList = new ArrayList<>();
        appController = HossPopularMoviesApp.getInstance();
        retrofit = appController.getRetrofitInstance();
        apiRequests = retrofit.create(ApiRequests.class);
        dbManager = HossPopularMoviesApp.getDbManagerInstance();
        favMovies = new ArrayList<>();
        PreferencesUtils.resetFavoriteChanged();
    }

    void getExtras() {

        Intent intent = getIntent();
        selectedMovie = intent.getParcelableExtra(Const.selectedMovie);
        setDataToViews(selectedMovie);
    }

    private void setDataToViews(Result selectedMovie) {
        collapsingToolbarLayout.setTitle(selectedMovie.getTitle());
        tvMovieOfficial.setText(selectedMovie.getOriginalTitle());
        String releaseDate = tvMovieReleaseDate.getText() + selectedMovie.getReleaseDate();
        tvMovieReleaseDate.setText(releaseDate);
        String ratingText = tvMovieratingText.getText() + "" + selectedMovie.getVoteAverage() + "/10" + " \n(" + selectedMovie.getVoteCount() + " votes)";

        tvMovieratingText.setText(ratingText);
        String rateValue = selectedMovie.getVoteAverage() + "";
        Float rateFloat = Float.parseFloat(rateValue);
        rateFloat = rateFloat / 2.0f;
        movieRatingBar.setRating(rateFloat);
        tvMovieOverView.setText(selectedMovie.getOverview());

        String url = selectedMovie.getPosterPath();
        url = "http://image.tmdb.org/t/p/w185/" + url;

        //image for CollapsingToolbarLayout
        setImageFromUrl(url, imgMoviePoster);

        //String urlBG = selectedMovie.getBackdropPath();
        //urlBG = "http://image.tmdb.org/t/p/w185/" + urlBG;

        //image for video view below CollapsingToolbarLayout
        setImageFromUrl(url, imgBackgroundMovie);

    }

    void setImageFromUrl(String url, ImageView imageView) {
        Glide
                .with(this)
                .load(url)
                .error(R.drawable.img_not_found)
                .into(imageView);
    }

    void requestVideoId(final boolean watch) {
        Call<VideoResult> callForVideo = apiRequests.getMovieVideo(selectedMovie.getId());
        if (Internet.isConnectingToInternet(this)) {
            //
            progressFrame.setVisibility(View.VISIBLE);

            callForVideo.enqueue(new Callback<VideoResult>() {
                @Override
                public void onResponse(Call<VideoResult> call, Response<VideoResult> VideoIdResponse) {

                    if (VideoIdResponse.isSuccessful()) {//True if status code (200-300)
                        if (VideoIdResponse.body() != null) {//True if response can be parsed in POJO

                            ResultArray[] resultArray = VideoIdResponse.body().getResults();
                            if (watch)
                                watchYoutubeVideo(resultArray[0].getKey());
                            else
                                setMovieIdToShareMovie(resultArray[0].getKey());

                        }

                    }
                    if (VideoIdResponse.code() >= 500) {
                        //Server error
                        Toast.makeText(ActivityScrollingMovieDetails.this, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();

                    }
                    progressFrame.setVisibility(View.INVISIBLE);


                }

                @Override
                public void onFailure(Call<VideoResult> call, Throwable t) {
                    progressFrame.setVisibility(View.INVISIBLE);

                    t.printStackTrace();
                    if (t instanceof SocketTimeoutException || t instanceof SocketException)
                        Toast.makeText(ActivityScrollingMovieDetails.this, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            Toast.makeText(ActivityScrollingMovieDetails.this, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void setMovieIdToShareMovie(String key) {

        HossPopularMoviesApp.shareMovie(this, selectedMovie.getOverview(), selectedMovie.getTitle(), key);
    }


    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onDestroy: before set favorite" + PreferencesUtils.isFavoriteChanged());
        if (isFavorite != favoriteState) {
            PreferencesUtils.setFavoriteChanged();
            Log.d(TAG, "onDestroy: after set favorite" + PreferencesUtils.isFavoriteChanged());
        }
    }

    private void setFragment(Fragment fragment, String tag, int movieId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.MOVIE_ID_FOR_REVIEW, movieId);
        fragment.setArguments(bundle);
        ft.replace(R.id.reviewsContainer, fragment, tag).commit();


    }
}

