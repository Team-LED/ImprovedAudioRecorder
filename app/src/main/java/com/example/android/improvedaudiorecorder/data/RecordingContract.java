package com.example.android.improvedaudiorecorder.data;

import android.provider.BaseColumns;

/**
 * Created by lizzi on 4/4/2018.
 */

public final class RecordingContract {

    private RecordingContract(){}

    public static final class RecordingEntry implements BaseColumns {

        public final static String TABLE_NAME = "recording_table";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RECORDING_TITLE = "title";
        public final static String COLUMN_RECORDING_FILE_NAME = "filename";

        /*
        public final static String COLUMN_RECORDING_INTERVIEWEE = "interviewee";
        public final static String COLUMN_RECORDING_DATE = "date";
        public final static String COLUMN_RECORDING_TAGS = "tags";
        public final static String COLUMN_RECORDING_PHOTO = "photo";
        public final static String COLUMN_RECORDING_LENGTH = "length";
         */

    }
}
