package me.branded.hossamhassan.hossam_popular_movies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.branded.hossamhassan.hossam_popular_movies.ActivityScrollingMovieDetails;
import me.branded.hossamhassan.hossam_popular_movies.App.Const;
import me.branded.hossamhassan.hossam_popular_movies.Models.Result;
import me.branded.hossamhassan.hossam_popular_movies.R;

/**
 * Created by Hossam on 4/6/2016.
 */
public class AdapterSearchList extends RecyclerView.Adapter<AdapterSearchList.MyViewHolder> {
    private List<Result> moviesList;
    private Context context;


    public AdapterSearchList(List<Result> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterSearchList.MyViewHolder holder, final int position) {
        final Result movie = moviesList.get(position);
        //set dynamic data
        holder.movieName.setText(movie.getTitle());
        String url = movie.getPosterPath();
        url = "http://image.tmdb.org/t/p/w185/" + url;

        Glide
                .with(this.context)
                .load(url)
                .error(R.drawable.img_not_found)
                .into(holder.moviePoster);

        //holder.companyImage.setImageResource(R.drawable.company2);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open activity detailed and pass the selected movie data
                Intent intent = new Intent(context, ActivityScrollingMovieDetails.class);
                intent.putExtra(Const.selectedMovie, movie);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    //my view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView movieName;
        ImageView moviePoster;

        public MyViewHolder(View view) {
            super(view);
            movieName = (TextView) view.findViewById(R.id.tvComNameCard);
            moviePoster = (ImageView) view.findViewById(R.id.imgComCard);

        }


    }
}
