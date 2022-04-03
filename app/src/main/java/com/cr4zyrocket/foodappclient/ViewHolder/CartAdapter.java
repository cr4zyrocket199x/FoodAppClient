package com.cr4zyrocket.foodappclient.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.Model.Order;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.UI.CartActivity;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public ImageView ivCartFoodImage,ivAdd,ivRemove;
    public TextView tvCartFoodName,tvCartFoodPrice,tvCount;

    private ItemClickListener itemClickListener;

    public void setTvCartFoodName(TextView tvCartFoodName) {
        this.tvCartFoodName = tvCartFoodName;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCartFoodName=itemView.findViewById(R.id.tvCartFoodName);
        tvCartFoodPrice=itemView.findViewById(R.id.tvCartFoodPrice);
        tvCount=itemView.findViewById(R.id.tvCount);
        ivAdd=itemView.findViewById(R.id.ivAdd);
        ivRemove=itemView.findViewById(R.id.ivRemove);
        ivCartFoodImage=itemView.findViewById(R.id.ivCartFoodImage);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAbsoluteAdapterPosition(), Common.DELETE);
    }
}
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private final List<Order> orderList;
    private final CartActivity cart;

    public CartAdapter(List<Order> orderList, CartActivity cart) {
        this.orderList = orderList;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(cart);
        View itemView=inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        Locale locale=new Locale("vi","VN");
        final NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        int price=(Integer.parseInt(orderList.get(position).getPrice()));
        holder.tvCartFoodPrice.setText(fmt.format(price));
        Picasso.with(cart.getBaseContext()).load(orderList.get(position).getImage()).into(holder.ivCartFoodImage);
        holder.tvCount.setText(orderList.get(position).getQuantity());
        holder.ivAdd.setOnClickListener(view -> {
            int count=Integer.parseInt(holder.tvCount.getText().toString());
            count++;
            holder.tvCount.setText(String.valueOf(count));
            Order order=orderList.get(position);
            order.setQuantity(String.valueOf(count));
            new Database(cart).updateCart(order);
            //Calculate total price
            int total=0;
            List<Order> orders=new Database(cart).getCarts();
            for (Order order1:orders){
                total+=(Integer.parseInt(order1.getPrice()))*(Integer.parseInt(order1.getQuantity()));
            }
            Locale locale1 =new Locale("vi","VN");
            NumberFormat fmt1 =NumberFormat.getCurrencyInstance(locale1);
            cart.tvTotal.setText(fmt1.format(total));
        });
        holder.ivRemove.setOnClickListener(view -> {
            int count=Integer.parseInt(holder.tvCount.getText().toString());
            if (count>1)
                count--;
            holder.tvCount.setText(String.valueOf(count));
            Order order=orderList.get(position);
            order.setQuantity(String.valueOf(count));
            new Database(cart).updateCart(order);
            //Calculate total price
            int total=0;
            List<Order> orders=new Database(cart).getCarts();
            for (Order order1:orders){
                total+=(Integer.parseInt(order1.getPrice()))*(Integer.parseInt(order1.getQuantity()));
            }
            Locale locale1 =new Locale("vi","VN");
            NumberFormat fmt1 =NumberFormat.getCurrencyInstance(locale1);
            cart.tvTotal.setText(fmt1.format(total));
        });
        holder.tvCartFoodName.setText(orderList.get(position).getFoodName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
