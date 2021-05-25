package com.example.bigwork.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DairySave extends SQLiteOpenHelper {
    public DairySave(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static String CREATE_Dairy="create table Dairy("
            +"id integer primary key autoincrement,"
            +"address text,"
            +"weather text,"
            +"date text,"
            +"word text,"
            +"img text)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Dairy);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Dairy");
        db.execSQL("drop table if exists Profile");
        onCreate(db);
    }
}
