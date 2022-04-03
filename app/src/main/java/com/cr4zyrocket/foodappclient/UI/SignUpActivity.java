package com.cr4zyrocket.foodappclient.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.Model.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUp";
    Button btnSignUp;
    EditText edtPhone,edtName,edtPassword;
    Dialog dialogVerify;
    ProgressDialog dialog;
    DatabaseReference tableUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.t_signup);

        //InitFirebase
        tableUsers= FirebaseDatabase.getInstance().getReference("Users");

        initView();

        btnSignUp.setOnClickListener(view -> {
            dialog=new ProgressDialog(SignUpActivity.this);
            dialog.setMessage("Please waiting...");
            dialog.show();
            tableUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String number = Objects.requireNonNull(edtPhone.getText()).toString();
                    if(number.isEmpty() || number.length() < 10){
                        dialog.dismiss();
                        edtPhone.setError("Enter a valid mobile !");
                        edtPhone.requestFocus();
                        return;
                    }
                    if(snapshot.child(edtPhone.getText().toString()).exists()){
                        dialog.dismiss();
                        edtPhone.setError("Phone Number already register !");
                        edtPhone.requestFocus();
                        return;
                    }
                    String phoneNumber="+84" + number;
                    if (Objects.requireNonNull(edtName.getText()).toString().trim().length()>0 && Objects.requireNonNull(edtPassword.getText()).toString().trim().length()>7){
                        //verify phone number
                        PhoneAuthProvider.verifyPhoneNumber(
                                PhoneAuthOptions.newBuilder()
                                        .setActivity(SignUpActivity.this)
                                        .setPhoneNumber(phoneNumber)
                                        .setTimeout(60L, TimeUnit.SECONDS)
                                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                dialog.dismiss();
                                                dialogVerify.dismiss();
                                                signInUser(phoneAuthCredential);
                                            }

                                            @Override
                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                dialog.dismiss();
                                                dialogVerify.dismiss();
                                                Toast.makeText(SignUpActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onVerificationFailed:"+e.getLocalizedMessage());
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull final String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);
                                                dialogVerify = new Dialog(SignUpActivity.this);
                                                dialogVerify.setContentView(R.layout.verify_popup);

                                                final EditText etVerifyCode = dialogVerify.findViewById(R.id.edtVerifyCode);
                                                Button btnVerifyCode = dialogVerify.findViewById(R.id.btnVerifyOTP);
                                                btnVerifyCode.setOnClickListener(v1 -> {
                                                    dialog.dismiss();
                                                    String verificationCode = etVerifyCode.getText().toString();
                                                    if(s.isEmpty()) return;
                                                    //create a credential
                                                    dialogVerify.dismiss();
                                                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(s,verificationCode);
                                                    signInUser(credential);
                                                });
                                                Button btnCancel=dialogVerify.findViewById(R.id.btnCancel);
                                                btnCancel.setOnClickListener(view1 -> {
                                                    dialog.dismiss();
                                                    dialogVerify.dismiss();
                                                });
                                                dialogVerify.show();
                                            }
                                        })
                                        .build()
                        );
                    }else {
                        dialog.dismiss();
                        if (Objects.requireNonNull(edtName.getText()).toString().trim().length()==0){
                            edtName.setError("Enter a valid name !");
                        }
                        if (Objects.requireNonNull(edtPassword.getText()).toString().trim().length()<8){
                            edtPassword.setError("Password must be least 8 characters !");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void initView() {
        btnSignUp=findViewById(R.id.btnSignUp);
        edtName=findViewById(R.id.edtName);
        edtPassword=findViewById(R.id.edtPassword);
        edtPhone=findViewById(R.id.edtPhone);
    }

    private void signInUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        User user=new User(Objects.requireNonNull(edtName.getText()).toString(), Objects.requireNonNull(edtPassword.getText()).toString(), Objects.requireNonNull(edtPhone.getText()).toString(),"");
                        tableUsers.child(edtPhone.getText().toString()).setValue(user);
                        Toast.makeText(SignUpActivity.this, "Sign up successful !", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Wrong code !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete:"+ Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });
    }
}