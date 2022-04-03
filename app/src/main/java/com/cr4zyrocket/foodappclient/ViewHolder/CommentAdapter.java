package com.cr4zyrocket.foodappclient.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.Model.FavouriteFood;
import com.cr4zyrocket.foodappclient.Model.Rating;
import com.cr4zyrocket.foodappclient.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvNameUserCmt,tvComment;
    public RatingBar ratingBarComment;
    private ItemClickListener itemClickListener;

    public CommentViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        tvNameUserCmt=itemView.findViewById(R.id.tvNameUserCmt);
        tvComment=itemView.findViewById(R.id.tvComment);
        ratingBarComment=itemView.findViewById(R.id.ratingBarComment);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
    }
}
public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    private List<Rating> ratingList;
    private Context context;

    public CommentAdapter() {
    }

    public CommentAdapter(List<Rating> ratingList, Context context) {
        this.ratingList = ratingList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position) {
        holder.tvNameUserCmt.setText(ratingList.get(position).getUserName());
        holder.ratingBarComment.setRating(Float.parseFloat(ratingList.get(position).getRateValue()));
        holder.tvComment.setText(ratingList.get(position).getComment());
        holder.setItemClickListener((view, position1, isLongClick) -> {

        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}
