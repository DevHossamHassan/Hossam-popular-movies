package me.branded.hossamhassan.hossam_popular_movies.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;

/**
 * Created by HossamHassan on 1/7/2016.
 */
public class DBManager extends SQLiteOpenHelper {
    public static final String TABLE_MOVIES = "MOVIES";
    public static final String COLUMN_TITLE = "_TITLE";
    public static final String COLUMN_TITLE_OFFICIAL = "_OFFICIAL";
    public static final String COLUMN_MOVIE_POSTER_PATH = "_POSTER_PATH";
    public static final String COLUMN_MOVIE_RATE_VALUE = "_RATE_VALUE";
    public static final String COLUMN_MOVIE_RATE_COUNT = "_RATE_COUNT";
    public static final String COLUMN_MOVIE_ID = "_ID";
    public static final String COLUMN_MOVIE_RELEASE_DATE = "_RELEASE_DATE";
    public static final String COLUMN_MOVIE_OVERVIEW = "_OVERVIEW";

    private static final String TAG = Const.TAG;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HOSSAM_POPULAR_MOVIES.db";


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    private static String createFavoriteMoviesTable() {
        String query = "CREATE TABLE " + TABLE_MOVIES + " (" +
                COLUMN_MOVIE_ID + " INTEGER  ," +
                COLUMN_TITLE + " TEXT ," +
                COLUMN_TITLE_OFFICIAL + " TEXT ," +
                COLUMN_MOVIE_POSTER_PATH + " TEXT ," +
                COLUMN_MOVIE_RATE_VALUE + " TEXT ," +
                COLUMN_MOVIE_RATE_COUNT + " INTEGER ," +
                COLUMN_MOVIE_RELEASE_DATE + " TEXT ," +
                COLUMN_MOVIE_OVERVIEW + " TEXT " +
                ");";
        return query;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createFavoriteMoviesTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

    public void addFavoriteMovie(Result movie) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movie.getId());
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_TITLE_OFFICIAL, movie.getOriginalTitle());
        values.put(COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        values.put(COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        values.put(COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        values.put(COLUMN_MOVIE_RATE_COUNT, movie.getVoteCount());
        values.put(COLUMN_MOVIE_RATE_VALUE, movie.getVoteAverage());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MOVIES, null, values);
        db.close();
    }

    public void deleteFavoriteMovie(int movieId) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_MOVIES + " WHERE " + COLUMN_MOVIE_ID + " =" + movieId + ";";
        db.execSQL(query);
    }


    public List<Result> getFavoriteMoviesList() {
        List moviesList = new ArrayList<Result>();
        Result movie;
        int movieId = -1, movieRateCount = -1;
        String movieTitle, movieTitleOfficial, moviePosterPath, movieReleaseDate, movieOverview, movieRateValue;
        movieTitle = movieTitleOfficial = moviePosterPath = movieReleaseDate = movieOverview = movieRateValue = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOVIES + " WHERE 1";//1 MEANS SELECT EVERY  COLUMN AND EVERY ROW
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            movie = new Result();
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_ID)) != null) {
                movieId = c.getInt(c.getColumnIndex(COLUMN_MOVIE_ID));
            }
            if (c.getString(c.getColumnIndex(COLUMN_TITLE)) != null) {
                movieTitle = c.getString(c.getColumnIndex(COLUMN_TITLE));
            }
            if (c.getString(c.getColumnIndex(COLUMN_TITLE_OFFICIAL)) != null) {
                movieTitleOfficial = c.getString(c.getColumnIndex(COLUMN_TITLE_OFFICIAL));
            }
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_POSTER_PATH)) != null) {
                moviePosterPath = c.getString(c.getColumnIndex(COLUMN_MOVIE_POSTER_PATH));
            }
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_RATE_COUNT)) != null) {
                movieRateCount = c.getInt(c.getColumnIndex(COLUMN_MOVIE_RATE_COUNT));
            }
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_RATE_VALUE)) != null) {
                movieRateValue = c.getString(c.getColumnIndex(COLUMN_MOVIE_RATE_VALUE));
            }
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_RELEASE_DATE)) != null) {
                movieReleaseDate = c.getString(c.getColumnIndex(COLUMN_MOVIE_RELEASE_DATE));
            }
            if (c.getString(c.getColumnIndex(COLUMN_MOVIE_OVERVIEW)) != null) {
                movieOverview = c.getString(c.getColumnIndex(COLUMN_MOVIE_OVERVIEW));
            }
            movie.setId(movieId);
            movie.setTitle(movieTitle);
            movie.setOriginalTitle(movieTitleOfficial);
            movie.setPosterPath(moviePosterPath);
            movie.setReleaseDate(movieReleaseDate);
            movie.setVoteCount(movieRateCount);
            movie.setVoteAverage(Double.parseDouble(movieRateValue));
            movie.setOverview(movieOverview);


            moviesList.add(movie);

            c.moveToNext();
        }
        db.close();
        return moviesList;
    }

}
