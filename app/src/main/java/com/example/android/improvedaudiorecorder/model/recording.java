package com.example.android.improvedaudiorecorder.model;

/**
 * Created by lizzi on 3/24/2018.
 */

public class recording {
    private String full_file_name;
    private String recording_title;
    /*
    Attributes to add
    -interveiwee
    -date
    -length
    -tags
     */


    public recording(String f){
        full_file_name = f;
        this.retreiveData();
    }

    public recording(String f, String s){
        full_file_name = f;
        recording_title = s;
    }

    public String getFullFileName(){return full_file_name;}
    public String getRecordingTitle(){ return recording_title;}
    public void retreiveData(){
        recording_title = full_file_name.substring(full_file_name.lastIndexOf( '/' )+1, full_file_name.lastIndexOf('.'));
    }


}
