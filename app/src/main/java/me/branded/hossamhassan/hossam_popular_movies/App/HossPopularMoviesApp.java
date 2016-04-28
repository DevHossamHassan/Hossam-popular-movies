package me.branded.hossamhassan.hossam_popular_movies.App;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.v4.app.ShareCompat;

import me.branded.hossamhassan.hossam_popular_movies.Api.RetrofitInit;
import me.branded.hossamhassan.hossam_popular_movies.Data.DBManager;
import me.branded.hossamhassan.hossam_popular_movies.Utils.PreferencesUtils;
import retrofit2.Retrofit;

/**
 * Created by HossamHassan on 4/21/2016.
 */
public class HossPopularMoviesApp extends Application {
    public static final String TAG = "Hoss";
    private Retrofit retrofit;
    private static HossPopularMoviesApp appController;
    private static DBManager dbManager;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        retrofit = RetrofitInit.createRetrofit();
        dbManager = new DBManager(this, null, null, 1);
        sharedPreferences = PreferencesUtils.getDefaultShared(this);
    }

    public static HossPopularMoviesApp getInstance() {
        return appController;
    }

    public static DBManager getDbManagerInstance() {
        return dbManager;
    }

    public static SharedPreferences getSharedPreferencesInstance() {
        return sharedPreferences;
    }

    public Retrofit getRetrofitInstance() {
        return retrofit;
    }
    public static void shareMovie(Activity activity,String overView,String movieName,String key) {
        ShareCompat.IntentBuilder
                .from(activity)
                .setText(movieName+"\n"+overView+ "url: http://www.youtube.com/watch?v=" + key)
                .setType("text/plain")
                .setChooserTitle("Share Movie "+movieName.substring(0,15)+"...")
                .startChooser();
    }



}
