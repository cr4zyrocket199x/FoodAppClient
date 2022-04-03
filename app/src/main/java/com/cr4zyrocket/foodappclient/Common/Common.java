package com.cr4zyrocket.foodappclient.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cr4zyrocket.foodappclient.Model.FacebookUser;
import com.cr4zyrocket.foodappclient.Model.Request;
import com.cr4zyrocket.foodappclient.Model.User;
import com.cr4zyrocket.foodappclient.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class Common {
    public static String currentLanguage="en";
    public static boolean systemUserLogin=true;
    public static String currentPhonePlace="";
    public static void setFont(){
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/res_font.ttf")
                                .setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath)
                                .build()))
                .build());
    }
    public static String getCurrentUserID(){
        if (!systemUserLogin){
            if (currentFacebookUser!=null)
                return currentFacebookUser.getFacebookID();
            else return "null";
        }else {
            if (currentUser!=null)
                return currentUser.getPhone();
            else return "null";
        }
    }

    public static String getCurrentUserPhone(){
        if (!systemUserLogin){
            if (currentFacebookUser!=null)
                return currentFacebookUser.getPhone();
            else return "null";
        }else {
            if (currentUser!=null)
                return currentUser.getPhone();
            else return "null";
        }
    }
    public static String getCurrentUserName(){
        if (!systemUserLogin){
            if (currentFacebookUser!=null)
                return currentFacebookUser.getName();
            else return "null";
        }else {
            if (currentUser!=null)
                return currentUser.getName();
            else return "null";
        }
    }
    public static FacebookUser currentFacebookUser;
    public static User currentUser;
    public static Request currentRequest;
    public static String convertCodeToStatus(String status){
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "Processing";
            case "2":
                return "On my way";
            case "3":
                return "Shipped";
            default:
                return "Cancel";
        }
    }
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";
}
