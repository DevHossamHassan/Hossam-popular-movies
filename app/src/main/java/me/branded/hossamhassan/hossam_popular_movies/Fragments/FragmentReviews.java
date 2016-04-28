package me.branded.hossamhassan.hossam_popular_movies.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.branded.hossamhassan.hossam_popular_movies.Adapters.AdapterReviews;
import me.branded.hossamhassan.hossam_popular_movies.Api.ApiRequests;
import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;
import me.branded.hossamhassan.hossam_popular_movies.Models.Reviews;
import me.branded.hossamhassan.hossam_popular_movies.Models.ReviewsResults;
import me.branded.hossamhassan.hossam_popular_movies.R;
import me.branded.hossamhassan.hossam_popular_movies.Utils.Internet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by HossamHassan on 4/25/2016.
 */
public class FragmentReviews extends Fragment {
    @Bind(R.id.recyclerReviews)
    RecyclerView recyclerView;
    @Bind(R.id.reviewsMainView)
    LinearLayout main;
    private List<Reviews> reviewsList;
    private AdapterReviews adapterReviews;
    private HossPopularMoviesApp appController;
    private Retrofit retrofit;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private ApiRequests apiRequests;
    int movieId;
    Call<ReviewsResults> callReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        if (getArguments() != null) {
            movieId = getArguments().getInt(Const.MOVIE_ID_FOR_REVIEW);
        }
        ButterKnife.bind(this, view);
        init();
        setRecycler();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callReviews = apiRequests.getReviews(movieId);
        getReviews(callReviews);
    }

    private void init() {

        context = getContext();
        reviewsList = new ArrayList<>();
        appController = HossPopularMoviesApp.getInstance();
        retrofit = appController.getRetrofitInstance();
        apiRequests = retrofit.create(ApiRequests.class);
    }

    private void setRecycler() {
        //set recycler view
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getContext(), 1);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getReviews(Call<ReviewsResults> call) {

        if (Internet.isConnectingToInternet(context)) {
            //
            call.enqueue(new Callback<ReviewsResults>() {
                @Override
                public void onResponse(Call<ReviewsResults> call, Response<ReviewsResults> resultReviewsResponse) {
                    if (resultReviewsResponse.isSuccessful()) {//True if status code (200-300)
                        if (resultReviewsResponse.body() != null) {//True if response can be parsed in POJO

                            List<Reviews> results = resultReviewsResponse.body().getResults();
                            if (results.size() == 0) {
                                // TODO: 4/25/2016 no reviews found hide main or msg tells no reviews
                                main.setVisibility(View.GONE);
                            } else {
                                reviewsList = results;
                                setReviewsAdapter(reviewsList);
                            }

                        }

                    }
                    if (resultReviewsResponse.code() >= 500) {
                        //Server error
                        main.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ReviewsResults> call, Throwable t) {
                    t.printStackTrace();
                    if (t instanceof SocketTimeoutException || t instanceof SocketException)
                        main.setVisibility(View.GONE);
                }
            });
        } else {
            //// TODO: 4/25/2016 hide main or msg tells no internet
            main.setVisibility(View.GONE);
        }

    }

    private void setReviewsAdapter(List<Reviews> reviewsList) {
        adapterReviews = new AdapterReviews(reviewsList);
        recyclerView.setAdapter(adapterReviews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callReviews.cancel();
    }
}
