package com.cr4zyrocket.foodappclient.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.Model.FavouriteFood;
import com.cr4zyrocket.foodappclient.R;

import com.cr4zyrocket.foodappclient.UI.FoodDetailActivity;
import com.cr4zyrocket.foodappclient.UI.FoodListActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvFavFoodName,tvFavFoodPrice;
    public ImageView ivFavFoodImage,ivFavourite;
    private ItemClickListener itemClickListener;

    public FavouriteViewHolder(@NonNull View itemView) {
        super(itemView);
        tvFavFoodName=itemView.findViewById(R.id.tvFavFoodName);
        tvFavFoodPrice=itemView.findViewById(R.id.tvFavFoodPrice);
        ivFavFoodImage=itemView.findViewById(R.id.ivFavFoodImage);
        ivFavourite=itemView.findViewById(R.id.ivFavourite);
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
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteViewHolder>{
    private static final String TAG = "FavouriteAdapter";
    private List<FavouriteFood>favourites;
    private Context context;
    Database localDB;
    public FavouriteAdapter() {
    }

    public FavouriteAdapter(List<FavouriteFood> favourites, Context context) {
        this.favourites = favourites;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.favourite_item,parent,false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteViewHolder holder, final int position) {
        holder.tvFavFoodName.setText(favourites.get(position).getFoodName());
        Locale locale=new Locale("vi","VN");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        holder.tvFavFoodPrice.setText(fmt.format(Integer.parseInt(favourites.get(position).getFoodPrice())));
        Picasso.with(context).load(Uri.parse(favourites.get(position).getFoodImage())).into(holder.ivFavFoodImage, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                holder.ivFavFoodImage.setImageResource(R.drawable.image_notfound);
            }
        });
        holder.ivFavourite.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
        FavouriteFood favouriteFood=new FavouriteFood();
        favouriteFood.setFoodID(favourites.get(position).getFoodID());
        favouriteFood.setUserID(favourites.get(position).getUserID());
        holder.ivFavourite.setOnClickListener(view -> {
            removeItem(position);
            localDB=new Database(context);
            localDB.removeFromFavorite(favouriteFood);
        });
        holder.setItemClickListener((view, position1, isLongClick) -> {
            //Start new Activity
            Intent foodDetailIntent=new Intent(context, FoodDetailActivity.class);
            foodDetailIntent.putExtra("FoodID",favourites.get(position1).getFoodID());
            foodDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(foodDetailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public void removeItem(int position){
        favourites.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }
    public void restoreItem(FavouriteFood favouriteFood,int pos){
        favourites.add(pos,favouriteFood);
        notifyItemInserted(pos);
    }
}