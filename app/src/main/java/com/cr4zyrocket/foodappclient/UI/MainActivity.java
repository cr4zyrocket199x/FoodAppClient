package com.cr4zyrocket.foodappclient.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Model.FacebookUser;
import com.cr4zyrocket.foodappclient.Model.User;
import com.cr4zyrocket.foodappclient.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FacebookLogin";
    CallbackManager callbackManager;
    LoginButton btnFBLogin;
    TextView txtSlogan;
    Button btnSignIn,btnSignUp;
    DatabaseReference facebookUsers;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Food App");

        mAuth=FirebaseAuth.getInstance();
        //Init Firebase database
        database=FirebaseDatabase.getInstance();

        // Initialize Facebook Login button
        callbackManager= CallbackManager.Factory.create();
        btnFBLogin=findViewById(R.id.btnFBLogin);
        btnFBLogin.setPermissions("email", "public_profile");
        btnFBLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        //Init Paper
        Paper.init(this);
        initView();
        //Check remember
        String phone=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);
        if (phone!=null && pwd!=null){
            if (!phone.isEmpty() && !pwd.isEmpty())
                login(phone,pwd);
        }

        //Slogan
        Typeface typeface= Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        txtSlogan.setTypeface(typeface);

        btnSignIn.setOnClickListener(view -> {
            Common.systemUserLogin=true;
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
        });
        btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,SignUpActivity.class));
        });
    }

    private void initView() {
        txtSlogan=findViewById(R.id.txtSlogan);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);
    }
    private void login(final String phone, final String pwd) {
        Common.systemUserLogin=true;
        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("Users");
        if (Common.isConnectedToInternet(getBaseContext())){
            final ProgressDialog dialog=new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please waiting...");
            dialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Check if user does not exist in database
                    if(snapshot.child(phone).exists()){
                        //Get User Information
                        dialog.dismiss();
                        User user=snapshot.child(phone).getValue(User.class);
                        assert user != null;
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)){
                            Intent homeIntent=new Intent(MainActivity.this,HomeActivity.class);
                            Common.currentUser=user;
                            startActivity(homeIntent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(MainActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        updateUI(user,token);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUI(final FirebaseUser user, AccessToken token) {
        //UserInfo userInfo= (UserInfo) user.getProviderData();
        Common.systemUserLogin=false;
        Common.currentFacebookUser=new FacebookUser(Profile.getCurrentProfile().getId(),user.getDisplayName(),user.getPhoneNumber(),"","");
        String photoUrl = user.getPhotoUrl()+"/picture?height=1000&width=1000&access_token="+token.getToken();
        Common.currentFacebookUser.setImageURI(photoUrl);
        facebookUsers=database.getReference("FacebookUser");
        facebookUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(Objects.requireNonNull(Profile.getCurrentProfile().getId())).exists()){
                    Intent homeIntent=new Intent(MainActivity.this,AddInfoFacebookLogin.class);
                    startActivity(homeIntent);
                }else {
                    Common.currentFacebookUser.setPhone(snapshot.child(Profile.getCurrentProfile().getId()).child("phone").getValue(String.class));
                    Intent homeIntent=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(homeIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}