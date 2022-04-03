package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Interface.ItemClickListener;
import com.cr4zyrocket.foodappclient.Model.Request;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderStatusActivity extends AppCompatActivity {
    String phone= Common.getCurrentUserPhone();
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests_databases;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_order_status);
        setTitle(getString(R.string.or_status));

        //Firebase
        database=FirebaseDatabase.getInstance();
        requests_databases =database.getReference("Requests");
        recyclerView=findViewById(R.id.recyclerListOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent().getStringExtra("userPhone")==null){
            loadOrderList(phone);
        }else {
            loadOrderList(getIntent().getStringExtra("userPhone"));
        }
    }
    private void loadOrderList(String phone) {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests_databases.orderByChild("phone").equalTo(phone), Request.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Request model) {
                String orID= "Order ID: "+adapter.getRef(position).getKey();
                String orS= "Order status: "+Common.convertCodeToStatus(model.getStatus());
                String orA="Order address: "+model.getAddress();
                String orP="Order phone: "+model.getPhone();
                String orC="Order comment: "+model.getComment();
                holder.tvOrderID.setText(orID);
                holder.tvOrderStatus.setText(orS);
                holder.tvOrderAddress.setText(orA);
                holder.tvOrderPhone.setText(orP);
                holder.tvOrderComment.setText(orC);

                Common.currentRequest=model;
                holder.setItemClickListener((view, position1, isLongClick) -> {
                    if (isLongClick){
                        Intent orderDetailIntent=new Intent(OrderStatusActivity.this,OrderDetailActivity.class);
                        orderDetailIntent.putExtra("OrderID",adapter.getRef(position1).getKey());
                        startActivity(orderDetailIntent);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}