package com.cr4zyrocket.foodappclient.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ChangeInfoFragment extends Fragment {
    private static final int USER_ADDRESS_ACTIVITY_CODE=112;
    Button btnChangeAddress,btnChange,btnCancelChange;
    TextView tvUserAddress;
    EditText edtUserName;
    String apartmentNumber,provinceCity,countyDistrict,wardCommune;
    FirebaseDatabase database;
    DatabaseReference requests;

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==USER_ADDRESS_ACTIVITY_CODE){
            if (resultCode == Activity.RESULT_OK){
                assert data != null;
                apartmentNumber=data.getStringExtra("ApartmentNumber");
                provinceCity=data.getStringExtra("ProvinceCity");
                countyDistrict=data.getStringExtra("CountyDistrict");
                wardCommune=data.getStringExtra("WardCommune");

                tvUserAddress.setText(apartmentNumber+", "+wardCommune+", "+countyDistrict+", "+provinceCity);
            }
        }

    }
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_change_info,container,false);
        btnChangeAddress=view.findViewById(R.id.btnChangeAddress);
        btnChange=view.findViewById(R.id.btnChangeInfo);
        btnCancelChange=view.findViewById(R.id.btnCancelChange);
        tvUserAddress=view.findViewById(R.id.tvUserAddress);
        edtUserName=view.findViewById(R.id.edtUserName);
        if (Common.getCurrentUserName()!=null)
            edtUserName.setText(Common.getCurrentUserName());

        if (!Common.systemUserLogin){
            edtUserName.setEnabled(false);
        }
        //Firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        btnChange.setOnClickListener(v -> {
            String ref="";
            if (apartmentNumber==null||wardCommune==null||countyDistrict==null||provinceCity==null){
                Toast.makeText(getActivity(), "Please fill all information before change !", Toast.LENGTH_SHORT).show();
            } else {
                if (Common.systemUserLogin){
                    ref="Users";
                }
                else{
                    ref="FacebookUser";
                }

                final android.app.AlertDialog waitingDialog=new SpotsDialog(getActivity());
                waitingDialog.show();
                Map<String, Object> addressUpdate = new HashMap<>();
                addressUpdate.put("address", tvUserAddress.getText().toString());
                Map<String, Object> userNameUpdate = new HashMap<>();
                userNameUpdate.put("name", edtUserName.getText().toString());
                DatabaseReference users = FirebaseDatabase.getInstance().getReference(ref);
                users.child(Common.getCurrentUserID())
                        .updateChildren(addressUpdate)
                        .addOnCompleteListener(task -> {
                            waitingDialog.dismiss();
                            if (Common.systemUserLogin)
                                Common.currentUser.setAddress(tvUserAddress.getText().toString());
                            else
                                Common.currentFacebookUser.setAddress(tvUserAddress.getText().toString());
                        })
                        .addOnFailureListener(e -> {
                            waitingDialog.dismiss();
                            Toast.makeText(getActivity(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                if (Common.systemUserLogin) {
                    users.child(Common.getCurrentUserID())
                            .updateChildren(userNameUpdate)
                            .addOnCompleteListener(task -> {
                                waitingDialog.dismiss();
                                Common.currentUser.setName(edtUserName.getText().toString());
                            })
                            .addOnFailureListener(e -> {
                                waitingDialog.dismiss();
                                Toast.makeText(getActivity(), "Error: " + e, Toast.LENGTH_SHORT).show();
                            });
                }
                Toast.makeText(getActivity(), "Updated !", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home,new HomeFragment()).commit();

            }
        });
        btnCancelChange.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home,new HomeFragment()).commit();
        });
        btnChangeAddress.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), UserAddressActivity.class), USER_ADDRESS_ACTIVITY_CODE);
        });
        return view;
    }


    @Override
    public void onStop() {
        super.onStop();

    }
}
