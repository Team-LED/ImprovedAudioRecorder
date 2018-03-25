package com.example.android.improvedaudiorecorder;

/**
 * Created by lizzi on 3/24/2018.
 */

public class recording {
    private String full_file_name;
    private String short_file_name;

    public recording(String f, String s){
        full_file_name = f;
        short_file_name = s;
    }

    public String getFullFileName(){return full_file_name;}
    public String getShortFileName(){ return short_file_name;}
}
