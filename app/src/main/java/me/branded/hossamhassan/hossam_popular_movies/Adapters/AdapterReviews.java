package me.branded.hossamhassan.hossam_popular_movies.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import me.branded.hossamhassan.hossam_popular_movies.Models.Reviews;
import me.branded.hossamhassan.hossam_popular_movies.R;

/**
 * Created by HossamHassan on 4/25/2016.
 */
public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.ReviewsHolder> {
    private List<Reviews> reviewsList;

    public AdapterReviews(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_movie_review, parent, false);

        return new ReviewsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterReviews.ReviewsHolder holder, int position) {
        final Reviews review = reviewsList.get(position);
        holder.author.setText(review.getAuthor());
        holder.expTv1.setText(review.getContent().trim());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        TextView author;
        //TextView reviewContent;
        ExpandableTextView expTv1;

        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        // expTv1.setText(getString(R.string.dummy_text1));
        public ReviewsHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tvReviewAuther);
            expTv1 = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
        }
    }


}
