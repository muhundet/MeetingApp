package com.example.meetingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MeetingsOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MeetingsDB.db";
    public static final int DATABASE_VERSION = 1;

    public MeetingsOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MeetingsDatabaseContract.MeetingsEntry.SQL_CREATE_TABLE);
        db.execSQL(MeetingsDatabaseContract.EmployeesEntry.SQL_CREATE_TABLE);
        db.execSQL(MeetingsDatabaseContract.RegisterEntry.SQL_CREATE_TABLE);
        db.execSQL(MeetingsDatabaseContract.AttendanceEntry.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
