package com.cr4zyrocket.foodappclient.UI;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    Button btnForgotPass,btnSignIn;
    EditText edtPhone,edtPassword;
    CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.t_signin);
        initView();

        //Init Firebase
        DatabaseReference tableUsers= FirebaseDatabase.getInstance().getReference("Users");

        btnForgotPass.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this,InputPhoneActivity.class));
        });

        btnSignIn.setOnClickListener(view -> {
            ProgressDialog dialogWaiting=new ProgressDialog(SignInActivity.this);
            dialogWaiting.setMessage("Please waiting...");
            dialogWaiting.show();

            tableUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(Objects.requireNonNull(edtPhone.getText()).toString()).exists()){
                        if (cbRemember.isChecked()){
                            Paper.book().write(Common.USER_KEY, Objects.requireNonNull(edtPhone.getText()).toString());
                            Paper.book().write(Common.PWD_KEY, Objects.requireNonNull(edtPassword.getText()).toString());
                        }
                        //Get User Information
                        dialogWaiting.dismiss();
                        User user=snapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        assert user != null;
                        user.setPhone(edtPhone.getText().toString());
                        if (user.getPassword().equals(Objects.requireNonNull(edtPassword.getText()).toString())){
                            Common.currentUser=user;
                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                            tableUsers.removeEventListener(this);
                        }else{
                            Toast.makeText(SignInActivity.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        dialogWaiting.dismiss();
                        Toast.makeText(SignInActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void initView() {
        btnForgotPass=findViewById(R.id.btnForgotPassword);
        btnSignIn=findViewById(R.id.btnSignIn);
        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
        cbRemember=findViewById(R.id.cbRemember);
    }
}