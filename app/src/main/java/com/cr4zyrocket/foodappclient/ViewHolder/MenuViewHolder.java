package com.cr4zyrocket.foodappclient.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        txtMenuName=itemView.findViewById(R.id.menu_name);
        imageView=itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
    }



}
