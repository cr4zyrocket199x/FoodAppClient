package com.cr4zyrocket.foodappclient.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvFoodName,tvFoodPrice;
    public ImageView ivFoodImage,ivFavorite;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        tvFoodName=itemView.findViewById(R.id.tvFoodName);
        tvFoodPrice=itemView.findViewById(R.id.tvFoodPrice);
        ivFoodImage=itemView.findViewById(R.id.ivFoodImage);
        ivFavorite=itemView.findViewById(R.id.ivFavorite);
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
