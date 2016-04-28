package me.branded.hossamhassan.hossam_popular_movies.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hossam on 11/04/2016.
 */

public class RetrofitInit {


    private final static String API_BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit createRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}