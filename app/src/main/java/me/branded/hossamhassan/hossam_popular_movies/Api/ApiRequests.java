package me.branded.hossamhassan.hossam_popular_movies.Api;

import me.branded.hossamhassan.hossam_popular_movies.Models.DataBase;
import me.branded.hossamhassan.hossam_popular_movies.Models.ReviewsResults;
import me.branded.hossamhassan.hossam_popular_movies.Models.VideoResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Hossam on 11/04/2016.
 */
public interface ApiRequests {
    /*
    * popular movies
    * */
    @GET("movie/popular?api_key=8e630989b36d359dcf7d077d1487584e")
    Call<DataBase> getPopularDataBaseForPage(@Query("page") int page);

    /*
    * Top Rated movies
    * */
    @GET("movie/top_rated?api_key=8e630989b36d359dcf7d077d1487584e")
    Call<DataBase> getTopRatedDataBaseForPage(@Query("page") int page);

    /*
    * movies video by id
    * */
    @GET("movie/{id}/videos?api_key=8e630989b36d359dcf7d077d1487584e")
    Call<VideoResult> getMovieVideo(@Path("id") int movieId);

    /*
    * movies review by id
     * */
    @GET("movie/{id}/reviews?api_key=8e630989b36d359dcf7d077d1487584e")
    Call<ReviewsResults> getReviews(@Path("id") int movieId);

    /*
    * search for movies
    * */
    @GET("search/movie?api_key=8e630989b36d359dcf7d077d1487584e")
    Call<DataBase> getSearchResultFor(@Query("query")String query,@Query("page") int page);

}
