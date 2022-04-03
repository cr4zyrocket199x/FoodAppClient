package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Model.Food;
import com.cr4zyrocket.foodappclient.Model.Order;
import com.cr4zyrocket.foodappclient.Model.Rating;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.CommentAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FoodDetailActivity extends AppCompatActivity{
    RecyclerView rvComment;
    RecyclerView.LayoutManager layoutManager;
    CommentAdapter adapter;
    List<Rating> ratingList;
    TextView tvFoodName,tvFoodPrice,tvFoodDescription,tvCount;
    ImageView ivFoodImage,ivAdd,ivRemove;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabCart,fabRating;
    String foodID="",categoryID;
    FirebaseDatabase database;
    DatabaseReference food,ratingData;
    Food currentFood;
    RatingBar ratingBar;
    float sum,count;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_food_detail);
        setTitle(getString(R.string.t_fooddetail));

        //Get Intent
        if (getIntent()!=null){
            categoryID=getIntent().getStringExtra("CategoryID");
        }
        //Firebase
        database=FirebaseDatabase.getInstance();
        food=database.getReference("Food").child(Common.currentLanguage);
        ratingData=database.getReference("Rating");

        //Init view
        rvComment=findViewById(R.id.rvListComment);
        rvComment.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        rvComment.setLayoutManager(layoutManager);
        fabCart=findViewById(R.id.btnCart);
        fabRating=findViewById(R.id.fabRating);
        tvFoodDescription=findViewById(R.id.tvFoodDescription);
        tvFoodName=findViewById(R.id.tvFoodName);
        tvFoodPrice=findViewById(R.id.tvFoodPrice);
        ivFoodImage=findViewById(R.id.iv_Food);
        ivAdd=findViewById(R.id.ivAdd);
        ivRemove=findViewById(R.id.ivRemove);
        tvCount=findViewById(R.id.tvCount);
        ratingBar=findViewById(R.id.ratingBar);
        collapsingToolbarLayout=findViewById(R.id.ctlFoodDetail);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get FoodID from Intent
        if(getIntent()!=null){
            foodID=getIntent().getStringExtra("FoodID");
            if (foodID!=null && foodID.length()>0){
                getFoodDetail(foodID);
                getRatingFood(foodID);
                showAllComment(foodID);
            }
        }

        ivAdd.setOnClickListener(view -> {
            int count=Integer.parseInt(tvCount.getText().toString());
            count++;
            tvCount.setText(String.valueOf(count));
        });
        ivRemove.setOnClickListener(view -> {
            int count=Integer.parseInt(tvCount.getText().toString());
            if (count>1)
                count--;
            tvCount.setText(String.valueOf(count));
        });
        fabCart.setOnClickListener(v -> {
            new Database(getBaseContext()).addToCart(
                    new Order(
                            foodID,
                            currentFood.getName(),
                            currentFood.getImage(),
                            tvCount.getText().toString(),
                            currentFood.getPrice(),
                            currentFood.getDiscount()));
            Toast.makeText(FoodDetailActivity.this, "Added to cart !", Toast.LENGTH_SHORT).show();
        });

        fabRating.setOnClickListener(v -> showRatingDialog());
    }
    private void showAllComment(String foodID){
        ratingData.orderByChild("foodID").equalTo(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ratingList=new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ratingList.add(dataSnapshot.getValue(Rating.class));
                }
                adapter=new CommentAdapter(ratingList,getBaseContext());
                rvComment.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getRatingFood(String foodID) {
        com.google.firebase.database.Query foodRating=ratingData.orderByChild("foodID").equalTo(foodID);
        foodRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Rating item=dataSnapshot.getValue(Rating.class);
                    assert item != null;
                    sum+=Float.parseFloat(item.getRateValue());
                    count++;
                }
                if (count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFoodDetail(String foodID) {
        food.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood=snapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(ivFoodImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        ivFoodImage.setImageResource(R.drawable.image_notfound);
                    }
                });
                Locale locale=new Locale("vi","VN");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                tvFoodPrice.setText(fmt.format(Integer.parseInt(currentFood.getPrice())));
                tvFoodDescription.setText(currentFood.getDescription());
                tvFoodName.setText(currentFood.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showRatingDialog() {
        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_changePwd=inflater.inflate(R.layout.rating_dialog_layout,null);
        EditText edtYourComment=layout_changePwd.findViewById(R.id.edtYourComment);
        RatingBar ratingBar=layout_changePwd.findViewById(R.id.ratingBar);
        String userID=Common.getCurrentUserID();
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("Food rating")
                .setView(layout_changePwd)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(FoodDetailActivity.this);
                    waitingDialog.show();
                    ratingData.orderByChild("userID_foodID").equalTo(userID+"_"+foodID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                dataSnapshot.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    ratingData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Rating rating=new Rating();
                            rating.setFoodID(foodID);
                            rating.setRateValue(ratingBar.getRating()+"");
                            rating.setComment(edtYourComment.getText().toString());
                            rating.setUserID_foodID(userID+"_"+foodID);
                            rating.setUserName(Common.getCurrentUserName());
                            rating.setUserID(Common.getCurrentUserID());
                            ratingData.child(String.valueOf(System.currentTimeMillis())).setValue(rating);
                            Toast.makeText(FoodDetailActivity.this, "Thank you for submit rating !", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                    waitingDialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        alertDialog.show();
    }


}