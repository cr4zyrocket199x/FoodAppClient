package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class UserAddressActivity extends AppCompatActivity {
    Button btnCancelAddress,btnConfirmAddress;
    EditText edtApartmentNumber;
    Spinner spProvinceCity,spCountyDistrict,spWardCommune;
    FirebaseDatabase database;
    DatabaseReference VNCities;
    ArrayList<String> provinceCities,countyDistricts,wardCommunes;
    String currentProvinceCity;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();
        setContentView(R.layout.activity_user_address);
        setTitle(getString(R.string.t_uaddress));

        database=FirebaseDatabase.getInstance();
        VNCities=database.getReference("VNCities");
        spProvinceCity=findViewById(R.id.spProvinceCity);
        spCountyDistrict=findViewById(R.id.spCountyDistrict);
        spWardCommune=findViewById(R.id.spWardCommune);
        btnCancelAddress=findViewById(R.id.btnCancelAddress);
        btnConfirmAddress=findViewById(R.id.btnConfirmAddress);
        edtApartmentNumber=findViewById(R.id.edtApartmentNumber);



        provinceCities=new ArrayList<>();
        countyDistricts=new ArrayList<>();
        wardCommunes=new ArrayList<>();
        spProvinceCity.setSelected(false);

        loadProvinceCity();
        spProvinceCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentProvinceCity=provinceCities.get(position);
                loadCountyDistrict(currentProvinceCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCountyDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWardCommune(countyDistricts.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCancelAddress.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
        btnConfirmAddress.setOnClickListener(v -> {
            if(edtApartmentNumber.getText().toString().split(" ").length<2){
                edtApartmentNumber.setError("Apartment number must must contain 2 words or more !");
            }else {
                final Intent intent = new Intent();
                intent.putExtra("ApartmentNumber", edtApartmentNumber.getText().toString());
                intent.putExtra("ProvinceCity", spProvinceCity.getSelectedItem().toString());
                intent.putExtra("CountyDistrict", spCountyDistrict.getSelectedItem().toString());
                intent.putExtra("WardCommune", spWardCommune.getSelectedItem().toString());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }
    private void loadWardCommune(String countyDistrict) {
        VNCities.orderByChild("Name").equalTo(currentProvinceCity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                wardCommunes.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.child("Districts").getChildren()){
                        if (Objects.equals(dataSnapshot1.child("Name").getValue(String.class), countyDistrict)) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Wards").getChildren()){
                                wardCommunes.add(dataSnapshot2.child("Name").getValue(String.class));
                            }
                        }
                    }
                }
                ArrayAdapter<String> arrayAdapter3=new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,wardCommunes);
                arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spWardCommune.setAdapter(arrayAdapter3);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadCountyDistrict(String provinceCity) {
        VNCities.orderByChild("Name").equalTo(provinceCity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                countyDistricts.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.child("Districts").getChildren()){
                        countyDistricts.add(dataSnapshot1.child("Name").getValue(String.class));
                    }
                }
                ArrayAdapter<String> arrayAdapter2=new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,countyDistricts);
                arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCountyDistrict.setAdapter(arrayAdapter2);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    public void loadProvinceCity(){
        VNCities.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    provinceCities.add(dataSnapshot.child("Name").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,provinceCities);
                arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spProvinceCity.setAdapter(arrayAdapter1);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}