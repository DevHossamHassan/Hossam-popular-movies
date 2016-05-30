package me.branded.hossamhassan.hossam_popular_movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.branded.hossamhassan.hossam_popular_movies.Adapters.AdapterSearchList;
import me.branded.hossamhassan.hossam_popular_movies.Api.ApiRequests;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;
import me.branded.hossamhassan.hossam_popular_movies.Models.DataBase;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivitySearch extends AppCompatActivity {
    String TAG = HossPopularMoviesApp.TAG;
    private HossPopularMoviesApp appController;
    private Retrofit retrofit;
    private List<Result> moviesList;
    @Bind(R.id.appBar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.edt_search)
    EditText ourSearchView;
    private AdapterSearchList searchAdapter;
    private LinearLayoutManager linearLayoutManager;
    ApiRequests apiRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        setActionBar();
        setRecycler();
        setSearchAdapter(moviesList);
        startSearch();

    }

    private void startSearch() {
        ourSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.length() != 0) {
                    getMoviesFromSearch(s+"");
                    Log.d(TAG, "onTextChanged: " + s);
                }/* else {
                    getCompaniesFromSearch("qatar");
                    Log.d(TAG, "onTextChanged: s.length =0");

                }*/

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        moviesList = new ArrayList<>();
        appController = HossPopularMoviesApp.getInstance();
        retrofit = appController.getRetrofitInstance();
        apiRequests = retrofit.create(ApiRequests.class);

    }

    private void setRecycler() {
        //set recycler view for get all companies
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setSearchAdapter(List<Result> moviesList) {
        searchAdapter = new AdapterSearchList(moviesList, getBaseContext());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // TODO: 4/20/2016 Start Animate The Search view


    }


    private void getMoviesFromSearch(String keyForSearch) {
        Call<DataBase> call = apiRequests.getSearchResultFor(keyForSearch,1);
        call.enqueue(new Callback<DataBase>() {
            @Override
            public void onResponse(Call<DataBase> call, Response<DataBase> moviesResponseDatabase) {
                //hide();
                //swipeLayout.setRefreshing(false);
                if (moviesResponseDatabase.isSuccessful()) {//True if status code (200-300)
                    if (moviesResponseDatabase.body() != null) {//True if response can be parsed in POJO

                        if (moviesResponseDatabase.body().getResults().size() == 0) {
                            Log.d(TAG, "onResponse empty :) ");
                        }
                        // showEmpty();
                        else {
                            //setCompaniesAdapter(companiesResponse.body());
                            //String[] resultCompanies = convertCompaniesListToArray(companiesResponse.body());
                            //Log.d(TAG, "onResponse list : " + companiesResponse.body().toString());
                            //Log.d(TAG, "onResponse array: " + resultCompanies.length + resultCompanies[0]);
                            //searchView.setAdapter(new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1, resultCompanies));
                            //searchView.setSuggestions(resultCompanies);
                            //searchView.showSuggestions();

                            //add search  result to recycler view
                            /*companiesList.clear();
                            companiesList=companiesResponse.body();
                            searchAdapter.notifyDataSetChanged();*/
                            setSearchAdapter(moviesResponseDatabase.body().getResults());


                        }

                    } else {
                        Log.d(TAG, "onResponse .body = null :) ");
                    }

                }
                if (moviesResponseDatabase.code() >= 500) {
                    //Server error
                    Log.d(TAG, "onResponse: server error >=500");

                }

            }

            @Override
            public void onFailure(Call<DataBase> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
                if (t instanceof SocketTimeoutException || t instanceof SocketException)
                    Toast.makeText(getBaseContext(), "Error: " + "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
