package com.example.plantscanapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDataBase extends SQLiteOpenHelper {
    public static final String DName="PlantScanApp.db";
    public static final String TName="HISTORY";

    public static final String c1="DCODE";
    public static final String c2="NAME";
    public static  final  String c3="DATE";

    public AppDataBase(Context context) {
        super(context, DName, null, 1);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TName+" (DCODE VARCHAR PRIMARY KEY, NAME VARCHAR, DATE VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean insertData(String dcode,String name,String date){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(c1,dcode);
        cv.put(c2,name);
        cv.put(c3,date);
        long result=db.insert(TName,null,cv);
        if(result==-1){
            return false;
        }
        return true;
    }

    public boolean isPresent(String dcode){
        SQLiteDatabase db=this.getWritableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TName + " WHERE " + c1 + "='" + dcode+"'", null);
            if(c.moveToNext()){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public boolean updateDate(String dcode,String date){
        ContentValues cv= new ContentValues();
        cv.put("date",date);
        SQLiteDatabase db=this.getReadableDatabase();
        int rows=db.update(TName,cv,c1+"=?",new String[]{dcode});
        if(rows==0) return false;
        return true;
    }

    public Integer deleteData(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        int rows=db.delete(TName,c1+"=?",new String[]{Integer.toString(id)});
        return rows;
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TName,null);
        return c;
    }
}
