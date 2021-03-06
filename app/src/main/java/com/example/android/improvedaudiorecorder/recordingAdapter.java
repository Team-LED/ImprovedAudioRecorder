package com.example.android.improvedaudiorecorder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.improvedaudiorecorder.model.recording;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lizzi on 3/24/2018.
 */

public class recordingAdapter extends ArrayAdapter<recording> {

    protected ListView listView;

    public recordingAdapter(Activity context, ArrayList<recording> recordingList){
        super(context, 0, recordingList);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

        }

        final recording currentRecording = getItem(position);

        TextView fileNameTextView = listItemView.findViewById(R.id.item_name);
        fileNameTextView.setText(currentRecording.getRecordingTitle());

        TextView deleteTextView = listItemView.findViewById(R.id.delete_view);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileToDelete = currentRecording.getFullFileName();
                File file = new File(fileToDelete);
                boolean deleted = file.delete();
                MainActivity.refreshContents();
                remove(currentRecording);
            }
        });

        return listItemView;
    }

}
