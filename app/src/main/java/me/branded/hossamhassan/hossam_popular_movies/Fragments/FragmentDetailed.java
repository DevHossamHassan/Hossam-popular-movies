package me.branded.hossamhassan.hossam_popular_movies.Fragments;

/**
 * Created by HossamHassan on 4/24/2016.
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import me.branded.hossamhassan.hossam_popular_movies.Api.ApiRequests;
import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;
import me.branded.hossamhassan.hossam_popular_movies.Data.DBManager;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import me.branded.hossamhassan.hossam_popular_movies.Models.ResultArray;
import me.branded.hossamhassan.hossam_popular_movies.Models.VideoResult;
import me.branded.hossamhassan.hossam_popular_movies.R;
import me.branded.hossamhassan.hossam_popular_movies.Utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hossam on 4/5/2016.
 */
public class FragmentDetailed extends Fragment {
    //Find views
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
    @Bind(R.id.detailed_fragment_container)
    CoordinatorLayout main;

    @OnClick(R.id.backgroundMovieOverlay)
    void onVideoClicked() {
        requestVideoId();
    }

    @Bind(R.id.backgroundMovie)
    ImageView imgBackgroundMovie;
    @Bind(R.id.moviePoster)
    ImageView imgMoviePoster;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    boolean isFavorite = false;
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
        onFabClickListener.onFabClick();

    }

    //
    private HossPopularMoviesApp appController;
    private Retrofit retrofit;
    private Context context;
    private ApiRequests apiRequests;
    @Bind(R.id.firstTimeView)
    FrameLayout firstTimeView;
    List<Result> favMovies;

    public interface OnFabClickListener {
        void onFabClick();
    }

    public void setOnFabClickListener(OnFabClickListener onFabClickListener) {
        this.onFabClickListener = onFabClickListener;
    }

    OnFabClickListener onFabClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_full, container, false);
        ButterKnife.bind(this, view);
        init();
        if (savedInstanceState != null) {
            hideDefaultView();
            selectedMovie = savedInstanceState.getParcelable(Const.SELECTED_MOVIE_FOR_DETAILED);
            updateViews(selectedMovie, 1);
        } else
            hideMain();
        return view;
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void showMain() {
        main.setVisibility(View.VISIBLE);
    }

    private void hideMain() {
        main.setVisibility(View.GONE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void init() {
        context = getContext();
        appController = HossPopularMoviesApp.getInstance();
        retrofit = appController.getRetrofitInstance();
        apiRequests = retrofit.create(ApiRequests.class);
        dbManager = HossPopularMoviesApp.getDbManagerInstance();
        favMovies = new ArrayList<>();

    }

    public void updateViews(Result movie, int i) {
        this.selectedMovie = movie;
        getFavoritesList();
        isFavorite = isFavoriteMovie(selectedMovie.getId());
        setFavoriteButtonState();
        tvMovieReleaseDate.setText("Release date:");
        tvMovieratingText.setText("Rating ");
        setDataToViews(movie);
    }

    public void hideDefaultView() {
        if (firstTimeView.getVisibility() == View.VISIBLE) {
            firstTimeView.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
        }

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
        setFragment(new FragmentReviews(), Const.FRAGMENT_MOVIE_REVIEWS, selectedMovie.getId());
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

    void requestVideoId() {
        Call<VideoResult> callForVideo = apiRequests.getMovieVideo(selectedMovie.getId());
        if (Internet.isConnectingToInternet(context)) {
            //
            progressFrame.setVisibility(View.VISIBLE);

            callForVideo.enqueue(new Callback<VideoResult>() {
                @Override
                public void onResponse(Call<VideoResult> call, Response<VideoResult> VideoIdResponse) {

                    if (VideoIdResponse.isSuccessful()) {//True if status code (200-300)
                        if (VideoIdResponse.body() != null) {//True if response can be parsed in POJO

                            ResultArray[] resultArray = VideoIdResponse.body().getResults();
                            watchYoutubeVideo(resultArray[0].getKey());

                        }

                    }
                    if (VideoIdResponse.code() >= 500) {
                        //Server error
                        Toast.makeText(context, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();

                    }
                    progressFrame.setVisibility(View.INVISIBLE);


                }

                @Override
                public void onFailure(Call<VideoResult> call, Throwable t) {
                    progressFrame.setVisibility(View.INVISIBLE);

                    t.printStackTrace();
                    if (t instanceof SocketTimeoutException || t instanceof SocketException)
                        Toast.makeText(context, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            Toast.makeText(context, "Please: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

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

    private void setFragment(Fragment fragment, String tag, int movieId) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.MOVIE_ID_FOR_REVIEW, movieId);
        fragment.setArguments(bundle);
        ft.replace(R.id.reviewsContainer, fragment, tag).commit();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Const.SELECTED_MOVIE_FOR_DETAILED, selectedMovie);
    }
}
