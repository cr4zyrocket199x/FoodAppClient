package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Model.Request;
import com.cr4zyrocket.foodappclient.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderInfoActivity extends AppCompatActivity {
    private static final int orderPlaceCode=111;
    Button btnChangeAddress,btnPlace,btnCancelPlace;
    TextView tvUserAddress;
    EditText edtOrderComment;
    String recipientName,apartmentNumber,provinceCity,countyDistrict,wardCommune,orderPhone;
    FirebaseDatabase database;
    DatabaseReference requests,userInfo;
    String uInfo="",userAddress="",userName="",userPhone="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==orderPlaceCode){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                recipientName=data.getStringExtra("RecipientName");
                apartmentNumber=data.getStringExtra("ApartmentNumber");
                provinceCity=data.getStringExtra("ProvinceCity");
                countyDistrict=data.getStringExtra("CountyDistrict");
                wardCommune=data.getStringExtra("WardCommune");
                orderPhone=data.getStringExtra("OrderPhone");
                Common.currentPhonePlace=orderPhone;

                tvUserAddress.setText(recipientName+" - "+orderPhone+"\n"+apartmentNumber+", "+wardCommune+", "+countyDistrict+", "+provinceCity);
            }
        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_order_info);
        setTitle(getString(R.string.or_info));

        btnChangeAddress=findViewById(R.id.btnChangeAddress);
        btnPlace=findViewById(R.id.btnPlace);
        btnCancelPlace=findViewById(R.id.btnCancelPlace);
        tvUserAddress=findViewById(R.id.tvUserAddress);
        edtOrderComment=findViewById(R.id.edtOrderComment);

        //Firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        Common.currentPhonePlace=Common.getCurrentUserPhone();
        if (Common.systemUserLogin){
            uInfo="Users";
        }else {
            uInfo="FacebookUser";
        }
        userInfo=database.getReference(uInfo);
        userInfo.orderByKey().equalTo(Common.getCurrentUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot child:snapshot.getChildren()){
                    userAddress=child.child("address").getValue(String.class);
                    userName=child.child("name").getValue(String.class);
                    userPhone=child.child("phone").getValue(String.class);
                }
                tvUserAddress.setText(userName+" - "+userPhone+"\n"+userAddress);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        btnPlace.setOnClickListener(v -> {
            if (tvUserAddress.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please fill all information before place !", Toast.LENGTH_SHORT).show();
            } else {
                Request request = new Request(
                        Common.currentPhonePlace,
                        !Common.systemUserLogin ? Common.currentFacebookUser.getName() : Common.currentUser.getName(),
                        tvUserAddress.getText().toString(),
                        getIntent().getStringExtra("Total"),
                        "0",
                        edtOrderComment.getText().toString(),
                        new Database(getBaseContext()).getCarts()
                );

                //Submit
                //Using System.CurrentMilli to key
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                //Delete Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(OrderInfoActivity.this, "Thank you, order placed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OrderInfoActivity.this,HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btnCancelPlace.setOnClickListener(v -> {
            startActivity(new Intent(OrderInfoActivity.this, CartActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        btnChangeAddress.setOnClickListener(v -> {
            startActivityForResult(new Intent(OrderInfoActivity.this, OrderPlaceActivity.class), orderPlaceCode);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }
}