package com.example.meetingapp.database;

import android.content.Context;
import android.database.Cursor;
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

    public Cursor getUnsyncedMeetingData() {
        SQLiteDatabase db = this.getReadableDatabase();
        final String [] meetingColumns = {
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_AGENDA,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_VENUE,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_DATE,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_START_TIME,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_ID,
                MeetingsDatabaseContract.MeetingsEntry.COLUMN_ZOOM_MEETING_PASSWORD};

        String selection = MeetingsDatabaseContract.MeetingsEntry.COLUMN_MEETING_ID + " = ?";
        String[] selectionArgs = {String.valueOf(0)};


        final Cursor meetingsEntryCursor = db.query(MeetingsDatabaseContract.MeetingsEntry.TABLE_NAME,
                meetingColumns,
                selection, selectionArgs, null, null, null);

        return meetingsEntryCursor;
    }

    public Cursor getUnsyncedRegister(){
        return
    }
}
