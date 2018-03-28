package com.example.android.improvedaudiorecorder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.improvedaudiorecorder.model.recording;

import java.util.ArrayList;

/**
 * Created by lizzi on 3/24/2018.
 */

public class recordingAdapter extends ArrayAdapter<recording> {

    public recordingAdapter(Activity context, ArrayList<recording> recordingList){
        super(context, 0, recordingList);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

        }

        recording currentRecording = getItem(position);

        TextView fileNameTextView = listItemView.findViewById(R.id.item_name);
        fileNameTextView.setText(currentRecording.getRecordingTitle());

        return listItemView;
    }
}
