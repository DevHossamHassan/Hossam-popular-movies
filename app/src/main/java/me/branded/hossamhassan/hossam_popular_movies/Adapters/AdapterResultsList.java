package me.branded.hossamhassan.hossam_popular_movies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import me.branded.hossamhassan.hossam_popular_movies.R;

/**
 * Created by HossamHassan on 4/22/2016.
 */
public class AdapterResultsList extends RecyclerView.Adapter<AdapterResultsList.MyViewHolder> {
    private List<Result> moviesList;
    private Context context;
    int deviceType;
    OnMovieClickListener onMovieClickListener;


    public interface OnMovieClickListener {
        void onMovieClick(View v, Result Movie);
    }

    public void setOnMovieClickListener(OnMovieClickListener onMovieClickListener) {
        this.onMovieClickListener = onMovieClickListener;
    }

    public AdapterResultsList(List<Result> moviesList, Context context, int deviceType) {
        this.moviesList = moviesList;
        this.context = context;
        this.deviceType = deviceType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_movies_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterResultsList.MyViewHolder holder, final int position) {
        final Result movie = moviesList.get(position);
        //set dynamic data
        //supposed to get image from
        holder.movieTitle.setText(movie.getTitle());
        String url = movie.getPosterPath();
        url = "http://image.tmdb.org/t/p/w185/" + url;

        Glide
                .with(this.context)
                .load(url)
                .error(R.drawable.img_not_found)
                .into(holder.moviePoster);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieClickListener.onMovieClick(v, movie);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    //my view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView moviePoster;
        TextView movieTitle;

        public MyViewHolder(View view) {
            super(view);
            moviePoster = (ImageView) view.findViewById(R.id.imgMovie);
            movieTitle = (TextView) view.findViewById(R.id.tvMovieTitle);
        }


    }

}
