package com.example.android.improvedaudiorecorder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.improvedaudiorecorder.data.RecordingContract.RecordingEntry;

/**
 * Created by lizzi on 4/4/2018.
 */

public class RecordingDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "listOfRecordings.db";
    private static final int DATABASE_VERSION = 1;

    public RecordingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_RECORDINGS_TABLE = "CREATE TABLE " + RecordingEntry.TABLE_NAME
                + " (" + RecordingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecordingEntry.COLUMN_RECORDING_FILE_NAME + " TEXT NOT NULL, "
                + RecordingEntry.COLUMN_RECORDING_TITLE + " TEXT DEFAULT title, "
                + RecordingEntry.COLUMN_RECORDING_INTERVIEWEE + "TEXT DEFAULT interviewee, "
                + RecordingEntry.COLUMN_RECORDING_DATE + "TEXT DEFAULT 0.0.0);";

        db.execSQL(SQL_CREATE_RECORDINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
