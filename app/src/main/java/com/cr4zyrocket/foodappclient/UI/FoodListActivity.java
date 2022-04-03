package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Model.FavouriteFood;
import com.cr4zyrocket.foodappclient.Model.Food;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FoodListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerFood;
    RecyclerView.LayoutManager layoutManager;
    String categoryID="",currentCategoryID="",userID= Common.getCurrentUserID();
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBarFood;
    Database localDB;
    SwipeRefreshLayout srlFoodList;
    LayoutAnimationController controller;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_food_list);
        setTitle(getString(R.string.t_fl));
        //Get Intent
        if (getIntent()!=null){
            categoryID=getIntent().getStringExtra("CategoryID");
        }
        currentCategoryID=categoryID;
        //Init Firebase
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Food").child(Common.currentLanguage);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("categoryID").equalTo(categoryID), Food.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                final FavouriteFood favouriteFood=new FavouriteFood();
                favouriteFood.setFoodID(adapter.getRef(position).getKey());
                favouriteFood.setUserID(userID);
                favouriteFood.setFoodName(model.getName());
                favouriteFood.setFoodImage(model.getImage());
                favouriteFood.setFoodDescription(model.getDescription());
                favouriteFood.setFoodPrice(model.getPrice());
                favouriteFood.setFoodDiscount(model.getDiscount());
                favouriteFood.setCategoryID(model.getCategoryID());
                holder.tvFoodName.setText(model.getName());
                Locale locale=new Locale("vi","VN");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                holder.tvFoodPrice.setText(fmt.format(Integer.parseInt(model.getPrice())));
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.ivFoodImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.ivFoodImage.setImageResource(R.drawable.image_notfound);
                    }
                });

//                //Click to share
//                holder.ivShare.setOnClickListener(v -> {
////                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
////                    Uri screenshotUri = Uri.parse(model.getImage());
////                    sharingIntent.setType("image/*");
////                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
////                    startActivity(Intent.createChooser(sharingIntent, "Share image using"));
//                    Picasso.with(getBaseContext()).load(model.getImage()).into(target);
//                });

                //Add Favorite
                if (localDB.isFavorite(favouriteFood)){
                    holder.ivFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                }

                //Click to add or remove favorite
                holder.ivFavorite.setOnClickListener(v -> {
                    if (!localDB.isFavorite(favouriteFood)){
                        localDB.addToFavorite(favouriteFood);
                        holder.ivFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Toast.makeText(FoodListActivity.this, model.getName()+" was added to Favorite", Toast.LENGTH_SHORT).show();
                    }else {
                        localDB.removeFromFavorite(favouriteFood);
                        holder.ivFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Toast.makeText(FoodListActivity.this, model.getName()+" was removed from Favorite", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.setItemClickListener((view, position1, isLongClick) -> {
                    //Start new Activity
                    Intent foodDetailIntent=new Intent(FoodListActivity.this,FoodDetailActivity.class);
                    foodDetailIntent.putExtra("FoodID",adapter.getRef(position1).getKey());
                    startActivity(foodDetailIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }

        };

        //Local DB
        localDB=new Database(this);

        //Load food
        recyclerFood=findViewById(R.id.recyclerFood);
        recyclerFood.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        controller= AnimationUtils.loadLayoutAnimation(recyclerFood.getContext(),R.anim.layout_from_right);
        recyclerFood.setLayoutAnimation(controller);
        recyclerFood.setLayoutManager(layoutManager);
        recyclerFood.startAnimation(controller.getAnimation());

        //View
        srlFoodList=findViewById(R.id.srlFoodList);
        srlFoodList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        srlFoodList.setOnRefreshListener(() -> {
            if (categoryID!=null&&categoryID.length()>0){
                srlFoodList.setRefreshing(true);
                loadFoodList();
            }
        });
        srlFoodList.post(() -> {
            if (categoryID!=null&&categoryID.length()>0){
                if (Common.isConnectedToInternet(getBaseContext())) {
                    srlFoodList.setRefreshing(true);
                    loadFoodList();
                }else {
                    Toast.makeText(FoodListActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Search
        materialSearchBarFood=findViewById(R.id.searchBarFood);
        materialSearchBarFood.setHint("Enter your food");
        loadSuggest();
        materialSearchBarFood.setLastSuggestions(suggestList);
        materialSearchBarFood.setCardViewElevation(10);
        materialSearchBarFood.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggests=new ArrayList<>();
                for (String search:suggestList){
                    if (search.toLowerCase().contains(materialSearchBarFood.getText().toLowerCase()))
                        suggests.add(search);
                }
                materialSearchBarFood.setLastSuggestions(suggests);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBarFood.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When searchBar is close
                //Restore original adapter
                if (!enabled)
                    recyclerFood.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearchFood(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFoodList();
        recyclerFood.startAnimation(controller.getAnimation());
    }

    private void loadSuggest() {
        foodList.orderByChild("categoryID").equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Food item=snapshot1.getValue(Food.class);
                    assert item != null;
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void startSearchFood(CharSequence food){
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("name").equalTo(food.toString()), Food.class)
                .build();
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position, @NonNull final Food model) {
                holder.tvFoodName.setText(model.getName());
                Locale locale=new Locale("vi","VN");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                holder.tvFoodPrice.setText(fmt.format(Integer.parseInt(model.getPrice())));
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.ivFoodImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.ivFoodImage.setImageResource(R.drawable.image_notfound);
                    }
                });
                holder.setItemClickListener((view, position12, isLongClick) -> {
                    //Start new Activity
                    Intent foodDetailIntent=new Intent(FoodListActivity.this,FoodDetailActivity.class);
                    foodDetailIntent.putExtra("FoodID",searchAdapter.getRef(position12).getKey());
                    foodDetailIntent.putExtra("CategoryID",categoryID);
                    startActivity(foodDetailIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };
        searchAdapter.startListening();
        recyclerFood.setAdapter(searchAdapter);
    }
    private void loadFoodList() {
        adapter.startListening();
        recyclerFood.setAdapter(adapter);
        srlFoodList.setRefreshing(false);

        recyclerFood.scheduleLayoutAnimation();
    }
}