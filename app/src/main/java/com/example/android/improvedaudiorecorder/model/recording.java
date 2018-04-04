package com.example.android.improvedaudiorecorder.model;

/**
 * Created by lizzi on 3/24/2018.
 */

//There will be two/three sources of data for creating recording objects:
//    1. The directory of audio files
//    2. The data base with all the attributes that are associated with
//       that specific file
//    3. A directory of photo files for the pictures attached to each interveiw
//       or maybe we draw from the gallery? I haven't looked into the details of
//       the photo stuff at all

public class recording {
    private String full_file_name;
    private String recording_title;
    private String interviewee_attribute;
    private String date_attribute;
    /*
    Attributes to add
    -interveiwee
    -date
    -length
    -tags
    -Photo, probably as a file path?
     */


    public recording(String f){
        full_file_name = f;
        this.retreiveData();
    }

    public recording(String f, String s){
        full_file_name = f;
        recording_title = s;
    }

    public recording(String f, String s, String i, String d){
        full_file_name = f;
        recording_title = s;
        interviewee_attribute = i;
        date_attribute = d;

    }

    public String getFullFileName(){return full_file_name;}
    public String getRecordingTitle(){ return recording_title;}
    public String getInterviewee() {return interviewee_attribute;}
    public String getDate() {return date_attribute;}

    public void retreiveData(){
        recording_title = full_file_name.substring(full_file_name.lastIndexOf( '/' )+1, full_file_name.lastIndexOf('.'));
    }


}
