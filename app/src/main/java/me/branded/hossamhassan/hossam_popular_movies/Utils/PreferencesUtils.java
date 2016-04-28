package me.branded.hossamhassan.hossam_popular_movies.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.App.HossPopularMoviesApp;

/**
 * Created by HossamHassan on 4/25/2016.
 */
public class PreferencesUtils {
    public static SharedPreferences getDefaultShared(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("me.branded.hossamhassan.hossam_popular_movies", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static boolean isFavoriteChanged() {
        SharedPreferences sp = HossPopularMoviesApp.getSharedPreferencesInstance();
        return sp.getBoolean(Const.FAVORITE_CHANGED, false);
    }

    public static void setFavoriteChanged() {
        SharedPreferences sp = HossPopularMoviesApp.getSharedPreferencesInstance();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Const.FAVORITE_CHANGED, true);
        editor.apply();
    }

    public static void resetFavoriteChanged() {
        SharedPreferences sp = HossPopularMoviesApp.getSharedPreferencesInstance();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Const.FAVORITE_CHANGED, false);
        editor.apply();
    }
}
