package me.branded.hossamhassan.hossam_popular_movies.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import me.branded.hossamhassan.hossam_popular_movies.Models.DataBase;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import me.branded.hossamhassan.hossam_popular_movies.R;
import me.branded.hossamhassan.hossam_popular_movies.Utils.Internet;
import me.branded.hossamhassan.hossam_popular_movies.Utils.PreferencesUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hossam on 4/5/2016.
 */
public class FragmentMoviesList extends FragmentParent implements SwipeRefreshLayout.OnRefreshListener, AdapterResultsList.OnMovieClickListener {
    String TAG = HossPopularMoviesApp.TAG;
    //Find views
    @Bind(R.id.progressBar1)
    ProgressBar progressBar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.main)
    RelativeLayout main;
    @Bind(R.id.btnFilter)
    ImageView btnFilter;
    int requestPage;

    @OnClick(R.id.btnFilter)
    void openFilterMenu() {
        showFilterPopUp(btnFilter);
    }

    //
    DBManager dbManager;
    private List<Result> resultsList;
    private AdapterResultsList resultsAdapter;
    private HossPopularMoviesApp appController;
    private Retrofit retrofit;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private View mainView;
    private int selectedSector;
    private PopupWindow popupFilter;
    private String[] filterArray;
    private int lastIndex, filterId;
    private ApiRequests apiRequests;
    int deviceType;
    OnMovieClickListenerFragment onMovieClickListenerFragment;

    public interface OnMovieClickListenerFragment {
        void onMovieClickFrag(View v, Result movie);
    }

    public void setOnMovieClickListenerFrag(OnMovieClickListenerFragment onMovieClickListenerFragment) {
        this.onMovieClickListenerFragment = onMovieClickListenerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            selectedSector = getArguments().getInt(Const.sortTypeSelected);
            deviceType = getArguments().getInt(Const.DEVICE_TYPE);
        }
        mainView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mainView);
        init();
        setRecycler();
        setSwipe();
        if (savedInstanceState != null) {
            filterId = savedInstanceState.getInt(Const.FILTER_ID);
            lastIndex = savedInstanceState.getInt(Const.FILTER_LAST_INDEX);
            resultsList = savedInstanceState.getParcelableArrayList(Const.MOVIES_LIST);
            setResultsAdapter(resultsList);
        } else {
            checkFilter(filterId);
        }


        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.fragment_movies_list;
    }

    private void showMain() {
        main.setVisibility(View.VISIBLE);
        swipeLayout.setRefreshing(true);
    }


    private void setSwipe() {
        swipeLayout.measure(View.MEASURED_SIZE_MASK, View.MEASURED_HEIGHT_STATE_SHIFT);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void setRecycler() {
        //set recycler view 
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 4);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == resultsAdapter.getItemCount() - 1) {
                    switch (filterId) {
                        case 1://Popular
                            Call<DataBase> callPopular = apiRequests.getPopularDataBaseForPage(++requestPage);
                            getMoreMovies(callPopular);
                            break;
                        case 2://TopRated
                            Call<DataBase> callTopRated = apiRequests.getTopRatedDataBaseForPage(++requestPage);
                            getMoreMovies(callTopRated);
                            break;
                    }


                }
            }
        });

    }

    private void init() {
        lastIndex = 1;//variable to identify sort index(by default 1 = Popular )
        filterId = 1;//Variable to identify filter index(by default 1 = Popular)
        requestPage = 1;//Variable to identify movies page index(by default 1 = first page )
        context = getContext();
        resultsList = new ArrayList<>();
        appController = HossPopularMoviesApp.getInstance();
        retrofit = appController.getRetrofitInstance();
        apiRequests = retrofit.create(ApiRequests.class);
        dbManager = HossPopularMoviesApp.getDbManagerInstance();
    }

    private void showFilterPopUp(View v) {
        //
        final PopupMenu popupMenu = new PopupMenu(context, v);
        //Inflate the menu from Xml
        popupMenu.getMenuInflater().inflate(R.menu.pop_up_filter, popupMenu.getMenu());
        //Set title color for popMenu header(Filter By)
        SpannableString s = new SpannableString(context.getString(R.string.filter));
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, s.length(), 0);
        popupMenu.getMenu().getItem(0).setTitle(s);

        //
        popupMenu.getMenu().getItem(lastIndex).setChecked(true);

        //Set up menu Selection
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.filter_popular://Popular
                        if (lastIndex != 1) {
                            filterId = 1;
                            checkFilter(filterId);
                            lastIndex = 1;
                        }
                        return true;

                    case R.id.filter_topRated://Top Rated
                        if (lastIndex != 2) {
                            filterId = 2;
                            checkFilter(filterId);
                            lastIndex = 2;
                        }
                        return true;
                    case R.id.filter_favorite://Favorite
                        if (lastIndex != 3) {
                            filterId = 3;
                            checkFilter(filterId);
                            lastIndex = 3;
                        }
                        return true;


                    default:
                        return false;
                }

            }
        });
        popupMenu.show();
    }

    private void checkFilter(int filterId) {
        //
        switch (selectedSector) {
            case 1://Movies
                switch (filterId) {
                    case 1://Popular
                        Call<DataBase> callPopular = apiRequests.getPopularDataBaseForPage(1);
                        getMovies(callPopular);
                        break;
                    case 2://TopRated
                        Call<DataBase> callTopRated = apiRequests.getTopRatedDataBaseForPage(1);
                        getMovies(callTopRated);
                        break;
                    case 3://Favorite
                        getFavoriteMovies();
                        break;
                }
                break;

        }
    }


    private void getMovies(Call<DataBase> call) {

        if (Internet.isConnectingToInternet(context)) {
            //
            hide();
            showMain();
            call.enqueue(new Callback<DataBase>() {
                @Override
                public void onResponse(Call<DataBase> call, Response<DataBase> resultMoviesResponse) {
                    btnFilter.setVisibility(View.VISIBLE);
                    hide();
                    swipeLayout.setRefreshing(false);
                    if (resultMoviesResponse.isSuccessful()) {//True if status code (200-300)
                        if (resultMoviesResponse.body() != null) {//True if response can be parsed in POJO

                            List<Result> results = resultMoviesResponse.body().getResults();
                            if (results.size() == 0) {
                                main.setVisibility(View.GONE);
                                showEmpty();
                            } else {
                                resultsList = results;
                                setResultsAdapter(resultsList);
                            }

                        }

                    }
                    if (resultMoviesResponse.code() >= 500) {
                        //Server error
                        Toast.makeText(getContext(), "Error: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DataBase> call, Throwable t) {
                    hide();
                    swipeLayout.setRefreshing(false);
                    t.printStackTrace();
                    if (t instanceof SocketTimeoutException || t instanceof SocketException)
                        Toast.makeText(getContext(), "Error: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showError();
            main.setVisibility(View.GONE);
        }

    }

    public void getFavoriteMovies() {
        List<Result> results = dbManager.getFavoriteMoviesList();
        if (results.size() == 0) {
            main.setVisibility(View.GONE);
            showEmpty();
        } else {
            hide();
            main.setVisibility(View.VISIBLE);
            resultsList = results;
            setResultsAdapter(resultsList);
        }
        swipeLayout.setRefreshing(false);
    }


    private void getMoreMovies(Call<DataBase> call) {

        if (Internet.isConnectingToInternet(context)) {
            //get more
            progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<DataBase>() {
                @Override
                public void onResponse(Call<DataBase> call, Response<DataBase> companiesResponse) {
                    if (companiesResponse.isSuccessful()) {//True if status code (200-300)
                        if (companiesResponse.body() != null) {//True if response can be parsed in POJO

                            List<Result> results = companiesResponse.body().getResults();
                            if (results.size() == 0) {
                                Toast.makeText(context, "No More Data", Toast.LENGTH_SHORT).show();
                            } else {
                                addToResultsAdapter(results);
                            }

                        }

                    }
                    if (companiesResponse.code() >= 500) {
                        //Server error
                        Toast.makeText(context, "Sorry it seems like server is not available", Toast.LENGTH_SHORT).show();

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<DataBase> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    t.printStackTrace();
                    if (t instanceof SocketTimeoutException || t instanceof SocketException)
                        Toast.makeText(getContext(), "Please : " + "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please : " + "Check your internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void setResultsAdapter(List<Result> resultsList) {
        resultsAdapter = new AdapterResultsList(resultsList, getContext(), deviceType);
        resultsAdapter.setOnMovieClickListener(this);
        recyclerView.setAdapter(resultsAdapter);
    }

    private void addToResultsAdapter(List<Result> moviesList) {
        //add more movies to movies list
        resultsList.addAll(moviesList);
        //notify only added movies for more efficiency
        int curSize = resultsAdapter.getItemCount();
        resultsAdapter.notifyItemRangeInserted(curSize, resultsList.size() - 1);
    }

    //Swipe to refresh
    @Override
    public void onRefresh() {
        checkFilter(filterId);
    }


    //handle btnRefresh to reload data after internet connection
    @Override
    public void onRefreshClick() {
        checkFilter(filterId);
    }

    @Override
    public void onMovieClick(View v, Result movie) {
        onMovieClickListenerFragment.onMovieClickFrag(v, movie);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isChanged = PreferencesUtils.isFavoriteChanged();
        Log.d(TAG, "onResume: " + isChanged);
        if (isChanged && filterId == 3) {
            onRefresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Const.FILTER_ID, filterId);
        outState.putInt(Const.FILTER_LAST_INDEX, lastIndex);
        outState.putParcelableArrayList(Const.MOVIES_LIST, new ArrayList<Result>(resultsList));
    }

    public int getFilterID() {
        return filterId;
    }

}
