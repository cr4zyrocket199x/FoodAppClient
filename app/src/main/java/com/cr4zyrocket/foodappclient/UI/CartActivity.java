package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Database.Database;
import com.cr4zyrocket.foodappclient.Model.Order;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.CartAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public TextView tvTotal;
    Button btnOrderPlace;
    int total=0;
    List<Order> cart=new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_cart);
        setTitle(getString(R.string.t_cart));

        //Init
        recyclerView=findViewById(R.id.recyclerListCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tvTotal=findViewById(R.id.tvTotal);
        btnOrderPlace=findViewById(R.id.btnPlaceOrder);

        loadListFoodOrder();
        btnOrderPlace.setOnClickListener(v -> {
            if (total==0)
                Toast.makeText(CartActivity.this, "Nothing to order !", Toast.LENGTH_LONG).show();
            else {
                Intent intentOrderInfo = new Intent(CartActivity.this, OrderInfoActivity.class);
                intentOrderInfo.putExtra("Total",tvTotal.getText().toString());
                startActivity(intentOrderInfo);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    private void loadListFoodOrder() {
        cart=new Database(getBaseContext()).getCarts();
        adapter=new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);
        //Calculate total price
        total=0;
        for (Order order:cart){
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale=new Locale("vi","VN");
        NumberFormat fmt= NumberFormat.getCurrencyInstance(locale);
        tvTotal.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();
        for (Order item:cart){
            new Database(this).addToCart(item);
        }
        loadListFoodOrder();
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CartActivity.this,HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}