package com.cr4zyrocket.foodappclient.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.cr4zyrocket.foodappclient.Model.FavouriteFood;
import com.cr4zyrocket.foodappclient.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DBName="SQLiteDB.db";
    private static final int DBVersion=1;
    Order order;
    FavouriteFood favourite;
    public Database(Context context) {
        super(context,DBName,null,DBVersion);
    }
    @SuppressLint("Range")
    public List<Order> getCarts(){
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        String[] sqlSelect={"FoodID","FoodName","FoodImage","Quantity","Price","Discount"};
        String sqlTable="OrderDetail";
        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        final List<Order> result=new ArrayList<>();
        if (c.moveToFirst()){
            do{
                result.add(order=new Order(
                        c.getString(c.getColumnIndex("FoodID")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))));
            }while (c.moveToNext());
        }
        return  result;
    }

    @SuppressLint("Range")
    public void addToCart(Order order){
        int quantity=Integer.parseInt(order.getQuantity());
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        String[] sqlSelect={"FoodID","FoodName","FoodImage","Quantity","Price","Discount"};
        String sqlTable="OrderDetail";
        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);
        if (c.moveToFirst()){
            do{
                if (c.getString(c.getColumnIndex("FoodID")).equals(order.getFoodID())){
                    quantity+=Integer.parseInt(c.getString(c.getColumnIndex("Quantity")));
                }
            }while (c.moveToNext());
        }
        String queryDelete=String.format("DELETE FROM OrderDetail WHERE FoodID LIKE '%S';",order.getFoodID());
        String queryInsert=String.format("INSERT INTO OrderDetail(FoodID,FoodName,FoodImage,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s','%s');",
                order.getFoodID(),order.getFoodName(),order.getImage(),quantity,order.getPrice(),order.getDiscount());
        db.execSQL(queryDelete);
        db.execSQL(queryInsert);
    }

    public void cleanCart(){
        SQLiteDatabase db=getReadableDatabase();
        String query= "DELETE FROM OrderDetail";
        db.execSQL(query);
    }

    //Favorites
    public void addToFavorite(FavouriteFood food){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Favourite VALUES ('%s','%s','%s','%s','%s','%s','%s','%s');"
                ,food.getUserID(),food.getFoodID(),food.getFoodName(),food.getFoodImage(),food.getFoodDescription(),food.getFoodPrice(),food.getFoodDiscount(),food.getCategoryID());
        db.execSQL(query);
    }
    public void removeFromFavorite(FavouriteFood food){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM Favourite WHERE UserID LIKE '%s' AND FoodID LIKE '%s';",food.getUserID(),food.getFoodID());
        db.execSQL(query);
    }
    public boolean isFavorite(FavouriteFood food){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Favourite WHERE UserID LIKE '%s' AND FoodID LIKE '%s';",food.getUserID(),food.getFoodID());
        Cursor cursor=db.rawQuery(query,null);
        if (cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    @SuppressLint("Range")
    public List<FavouriteFood> getAllFavourites(String userID){
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        String[] sqlSelect={"UserID","FoodID","FoodName","FoodImage","FoodDescription","FoodPrice","FoodDiscount","CategoryID"};
        String sqlTable="Favourite";
        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,"UserID = ?",new String[]{userID},null,null,null);
        final List<FavouriteFood> result=new ArrayList<>();
        if (c.moveToFirst()){
            do{
                favourite=new FavouriteFood(
                        c.getString(c.getColumnIndex("UserID")),
                        c.getString(c.getColumnIndex("FoodID")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("FoodDescription")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodDiscount")),
                        c.getString(c.getColumnIndex("CategoryID")));
//                DatabaseReference foods= FirebaseDatabase.getInstance().getReference("Food");
//                foods.orderByKey().equalTo(c.getString(c.getColumnIndex("FoodID"))).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        if (snapshot.exists())
//                            result.add(favourite);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
                result.add(favourite);
            }while (c.moveToNext());
        }
        return  result;
    }

    public int getCountCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM OrderDetail";
        Cursor cursor = db.rawQuery(query, null);
        int count=cursor.getCount();
        cursor.close();
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity=%s WHERE FoodID LIKE '%s'",order.getQuantity(),order.getFoodID());
        db.execSQL(query);
    }
}
