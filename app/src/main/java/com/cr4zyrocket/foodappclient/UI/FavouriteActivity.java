package com.cr4zyrocket.foodappclient.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.FavouriteAdapter;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FavouriteActivity extends AppCompatActivity {
    FavouriteAdapter adapter;
    RecyclerView recyclerFavFood;
    RecyclerView.LayoutManager layoutManager;
    LayoutAnimationController controller;
    String userID= Common.getCurrentUserID();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_favourite);
        setTitle(getString(R.string.t_ff));

        //Load food
        recyclerFavFood=findViewById(R.id.recyclerFavouriteFood);
        recyclerFavFood.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        controller= AnimationUtils.loadLayoutAnimation(recyclerFavFood.getContext(),R.anim.layout_from_right);
        recyclerFavFood.setLayoutAnimation(controller);
        recyclerFavFood.setLayoutManager(layoutManager);
        recyclerFavFood.startAnimation(controller.getAnimation());

        loadFavourites();
    }
    private void loadFavourites() {
        adapter=new FavouriteAdapter(new Database(this).getAllFavourites(userID),getBaseContext());
        recyclerFavFood.setAdapter(adapter);
    }
}