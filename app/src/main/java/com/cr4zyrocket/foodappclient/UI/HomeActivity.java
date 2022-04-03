package com.cr4zyrocket.foodappclient.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.R;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.cr4zyrocket.foodappclient.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseDatabase database;
    DatabaseReference category;
    ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;
    TextView tvUserName,tvUserPhone;
    NavigationView navigationView;
    DrawerLayout drawer;
    ImageView ivProfile;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.setFont();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar);



        binding.appBarHome.fab.setOnClickListener(view -> {
            Intent cartIntent=new Intent(HomeActivity.this,CartActivity.class);
            startActivity(cartIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        initView();

        //Set information
        tvUserName.setText(Common.getCurrentUserName());
        tvUserPhone.setText(Common.getCurrentUserID());
        if (!Common.systemUserLogin){
            navigationView.getMenu().findItem(R.id.nav_changePassword).setVisible(false);
            Picasso.with(getBaseContext()).load(Common.currentFacebookUser.getImageURI()).into(ivProfile);
        }else {
            navigationView.getMenu().findItem(R.id.nav_changePassword).setVisible(true);
            ivProfile.setImageResource(R.drawable.default_user_image);
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_changeInfo,R.id.nav_cart,R.id.nav_changeLanguage,R.id.nav_changePassword,R.id.nav_favoriteFood,
                R.id.nav_log_out,R.id.nav_myLocation,R.id.nav_orders)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_home);
        assert navHostFragment != null;
        navController =navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        //Init Firebase
        database= FirebaseDatabase.getInstance();
        category=database.getReference("Category").child(Common.currentLanguage);



    }

    private void initView() {
        View headerView=navigationView.getHeaderView(0);
        tvUserName=headerView.findViewById(R.id.tvUserName);
        tvUserPhone=headerView.findViewById(R.id.tvUserPhone);
        ivProfile=headerView.findViewById(R.id.ivProfile);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.refreshMenu){

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromFacebook();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_home){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home,new HomeFragment()).commit();
            drawer.closeDrawers();
        }
        else if (id==R.id.nav_myLocation){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home,new MyLocationFragment()).commit();
            drawer.closeDrawers();
        }else if(id==R.id.nav_cart){
            Intent cartIntent=new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(id==R.id.nav_orders){
            Intent orderIntent=new Intent(HomeActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(id==R.id.nav_changePassword){
            showChangePasswordDialog();
        }
        else if(id==R.id.nav_changeInfo){
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home,new ChangeInfoFragment()).commit();
            drawer.closeDrawers();
        }
        else if(id==R.id.nav_changeLanguage){
            showChangeLanguageDialog();
        }
        else if(id==R.id.nav_log_out){
            //Delete Remember User
            Paper.book().destroy();

            disconnectFromFacebook();
            Intent signInIntent=new Intent(HomeActivity.this,MainActivity.class);
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signInIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }else if (id==R.id.nav_favoriteFood){
            Intent favouriteIntent=new Intent(HomeActivity.this, FavouriteActivity.class);
            startActivity(favouriteIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return true;
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_changePwd=inflater.inflate(R.layout.change_password_layout,null);
        EditText etCurPass=layout_changePwd.findViewById(R.id.etCurrentPassword);
        EditText etNewPass=layout_changePwd.findViewById(R.id.etNewPassword);
        EditText etNewPassRepeat=layout_changePwd.findViewById(R.id.etNewPasswordRepeat);
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("Change password")
                .setMessage("Fill all information to change !")
                .setView(layout_changePwd)
                .setPositiveButton("Change", (dialog, which) -> {
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(HomeActivity.this);
                    waitingDialog.show();
                    if (Objects.requireNonNull(etCurPass.getText()).toString().length()==0 || Objects.requireNonNull(etNewPass.getText()).toString().length()==0 || Objects.requireNonNull(etNewPassRepeat.getText()).toString().length()==0){
                        Toast.makeText(HomeActivity.this, "Please fill all information to change !", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                    }
                    else {
                        if (etCurPass.getText().toString().equals(Common.currentUser.getPassword())){
                            if (etNewPass.getText().toString().equals(etNewPassRepeat.getText().toString())){
                                if (etNewPass.getText().length()>=8) {
                                    Map<String, Object> passwordUpdate = new HashMap<>();
                                    passwordUpdate.put("password", etNewPass.getText().toString());
                                    DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
                                    users.child(Common.currentUser.getPhone())
                                            .updateChildren(passwordUpdate)
                                            .addOnCompleteListener(task -> {
                                                waitingDialog.dismiss();
                                                Common.currentUser.setPassword(etNewPass.getText().toString());
                                                Toast.makeText(HomeActivity.this, "Password was changed !", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                waitingDialog.dismiss();
                                                Toast.makeText(HomeActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                                            });
                                }else {
                                    waitingDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "New password must be at least 8 characters !", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                waitingDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "New password not same !", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            waitingDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Wrong current password !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        alertDialog.show();
    }
    private void showChangeLanguageDialog() {
        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_changePwd=inflater.inflate(R.layout.change_language_layout,null);
        final RadioButton rbVietnamese=layout_changePwd.findViewById(R.id.rbVi);
        rbVietnamese.setChecked(true);
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("Change language")
                .setMessage("Select your language !")
                .setView(layout_changePwd)
                .setPositiveButton("Change", (dialog, which) -> {
                    final android.app.AlertDialog waitingDialog=new SpotsDialog(HomeActivity.this);
                    waitingDialog.show();
                    if (rbVietnamese.isChecked()){
                        changeLanguage("vi");
                        Common.currentLanguage="vi";
                    }else {
                        changeLanguage("en");
                        Common.currentLanguage="en";
                    }
                    waitingDialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        alertDialog.show();
    }

    public void changeLanguage(String language){
        Locale locale=new Locale(language);
        Configuration configuration= new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        Intent intent=new Intent(HomeActivity.this,HomeActivity.class);
        startActivity(intent);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromFacebook();
    }

    public void disconnectFromFacebook() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }
}