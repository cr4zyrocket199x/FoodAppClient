package com.cr4zyrocket.foodappclient.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.OrderDetailAdapter;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderDetailActivity extends AppCompatActivity {
    TextView tvOrderID,tvOrderPhone,tvOrderAddress,tvOrderTotal,tvOrderComment;
    String order_id_value,phone= Common.getCurrentUserPhone();
    RecyclerView rvListFood;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_order_detail);
        setTitle(getString(R.string.or));

        tvOrderID=findViewById(R.id.tvOrderID);
        tvOrderPhone=findViewById(R.id.tvOrderPhone);
        tvOrderAddress=findViewById(R.id.tvOrderAddress);
        tvOrderTotal=findViewById(R.id.tvOrderTotal);
        tvOrderComment=findViewById(R.id.tvOrderComment);
        rvListFood=findViewById(R.id.rvListFood);
        layoutManager=new LinearLayoutManager(this);
        rvListFood.setLayoutManager(layoutManager);

        if (getIntent()!=null){
            order_id_value=getIntent().getStringExtra("OrderID");
        }
        String orID="Order ID: "+order_id_value;
        String orP= "Order phone: "+phone;
        String orA="Order address: "+Common.currentRequest.getAddress();
        String orT="Order Total: "+Common.currentRequest.getTotal();
        String orC="Order comment: "+Common.currentRequest.getComment();
        tvOrderID.setText(orID);
        tvOrderPhone.setText(orP);
        tvOrderAddress.setText(orA);
        tvOrderTotal.setText(orT);
        tvOrderComment.setText(orC);

        OrderDetailAdapter adapter=new OrderDetailAdapter(Common.currentRequest.getFoodOrdered());
        adapter.notifyDataSetChanged();
        rvListFood.setAdapter(adapter);
    }
}