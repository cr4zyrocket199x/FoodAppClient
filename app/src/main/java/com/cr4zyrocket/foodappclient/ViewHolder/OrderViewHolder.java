package com.cr4zyrocket.foodappclient.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public TextView tvOrderID,tvOrderStatus,tvOrderPhone,tvOrderAddress,tvOrderComment;
    public ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOrderAddress=itemView.findViewById(R.id.tvOrderAddress);
        tvOrderID=itemView.findViewById(R.id.tvOrderID);
        tvOrderStatus=itemView.findViewById(R.id.tvOrderStatus);
        tvOrderPhone=itemView.findViewById(R.id.tvOrderPhone);
        tvOrderComment=itemView.findViewById(R.id.tvOrderComment);

        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),true);
        return true;
    }
}
