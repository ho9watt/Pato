package com.example.pato;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class OptionDatabase extends SQLiteOpenHelper {
    public OptionDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableName = "option";
        String tableSql = "create table if not exists " + tableName +"(_id text, contestalarm text, boardalarm text)";
        sqLiteDatabase.execSQL(tableSql);

        this.addSetting(sqLiteDatabase,tableName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion>1){
            String tableName = "option";
            sqLiteDatabase.execSQL("drop table if exists "+tableName);

            String sql = "create table if not exists " + tableName +"(_id text, contestalarm text, boardalarm text)";
            sqLiteDatabase.execSQL(sql);
        }
    }

    public void addSetting(SQLiteDatabase db, String tableName) {
        String tableSql2 = "insert into "+ tableName + "(_id, contestalarm, boardalarm) values('option','off','off')";
        db.execSQL(tableSql2);
    }
}
