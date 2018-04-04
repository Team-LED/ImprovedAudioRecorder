package com.example.android.improvedaudiorecorder.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.improvedaudiorecorder.R;
import com.example.android.improvedaudiorecorder.model.recording;
import com.example.android.improvedaudiorecorder.recordingAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//TODO: 1. Check user input before file creation to see if name is already in use.
//      2. Deload resources when playback ends
//      4. Decide how to handle multiple files with the same name(Overwrite? Not Allow?)

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ImprovedAudioRecorder";
    protected static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    protected boolean permissionToRecordAccepted = false;
    protected String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    protected ArrayList<recording> listOfRecordings = new ArrayList<recording>();
    protected static recordingAdapter adapter = null;


    public static File directory = null;
    public static File Recordings_Contents[] = null;

    public String mFileName = null;
    public String userInput = null;

    //States to keep track of which buttons should be enabled, to prevent
    //stuff like playing and recording at the same time.
    boolean readyToRecord = false;
    boolean startPlaying = true;
    boolean startRecording = true;
    boolean paused = true;
    boolean file_name_entered = false;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    //Gets permission to use microphone
    //Will add in camera and writing to external memory after integration
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted)
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_main);

        //Get the name of our directory and load all the files currently stored in it
        directory = this.getDir("Recordings", MODE_PRIVATE);
        Recordings_Contents = directory.listFiles();

        //Link up buttons from activity_main.xml
        final AppCompatButton playButton = (AppCompatButton) findViewById(R.id.xml_play_button);
        final AppCompatButton recordButton = (AppCompatButton) findViewById(R.id.xml_record_button);
        final AppCompatButton newButton = (AppCompatButton) findViewById(R.id.xml_new_button);
        final Button enterButton = findViewById(R.id.xml_enter_button);
        final ListView listView = findViewById(R.id.recording_container);

        //PRE-DATABASE IMPLEMENTATION
        //From audio files, create recording objects and load into arraylist, which will
        //then be shown in the listview.
        for (int i = 0; i < Recordings_Contents.length; i++) {
            listOfRecordings.add(new recording(Recordings_Contents[i].toString()));
        }

        final EditText recordingNameField = (EditText) findViewById(R.id.recordingNameField);
        newButton.setEnabled(true);

        //Set listeners for buttons.
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                if (startPlaying) {
                    playButton.setText("Stop Playing");
                    recordButton.setEnabled(false);
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playButton.setText("Start Playing");
                            stopPlaying();
                            startPlaying = !startPlaying;
                            if (readyToRecord)
                                recordButton.setEnabled(true);
                        }
                    });
                } else {
                    playButton.setText("Start Playing");
                    if (readyToRecord)
                        recordButton.setEnabled(true);
                }
                startPlaying = !startPlaying;
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordingNameField.setEnabled(false);
                onRecord(startRecording);
                if (startRecording) {
                    recordButton.setText("Stop Recording");
                    playButton.setEnabled(false);
                    recordingNameField.setEnabled(false);
                } else {
                    recordButton.setText("Start Recording");
                    recordingNameField.setEnabled(true);
                    //SAVE FILE, ADD TO LIST
                    listOfRecordings.add(new recording(mFileName, userInput));
                    //adapter.notifyDataSetChanged();
                    refreshContents();
                    recordingNameField.getText().clear();
                    readyToRecord = false;
                    recordButton.setEnabled(false);
                    mFileName = null;
                }
                recordingNameField.setEnabled(true);
                startRecording = !startRecording;
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetailEntryActivity.class);
                startActivity(i);
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput = recordingNameField.getText().toString();
                mFileName = directory.toString() + '/' + userInput + ".3gp";
                //Toast.makeText(MainActivity.this, mFileName, Toast.LENGTH_SHORT).show();
                file_name_entered = true;
                readyToRecord = true;
                recordButton.setEnabled(true);
            }
        });

        adapter = new recordingAdapter(this, listOfRecordings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recording listItem = (recording) listView.getItemAtPosition(position);
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (position == i) {
                        View temp = listView.getChildAt(i);
                        TextView txtView = ((TextView) temp.findViewById(R.id.item_name));
                        txtView.setTextColor(getResources().getColor(R.color.colorAccent));

                    } else {
                        View temp = listView.getChildAt(i);
                        TextView txtView = ((TextView) temp.findViewById(R.id.item_name));
                        txtView.setTextColor(Color.BLACK);
                    }

                }
                mFileName = listItem.getFullFileName();
                playButton.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
    }

    private void onNew() {
        //Switch intent to activity_detail_entry
        //get details
        //come back
    }

    private void onpause() {
        for (int i = 0; i < Recordings_Contents.length; i++) {
            Toast.makeText(this, Recordings_Contents[i].toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onRecord(boolean start) {
        if (start)
            startRecording();
        else
            stopRecording();
    }

    private void startRecording() {
        recorder = new MediaRecorder();

        //It might be best to put this whole thing inside a try/catch
        //block? I'm not sure.

        //Sets up the recorder for audio as opposed to video,
        //and a few other things related to file format and encoding.
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        //I think here is where I need to add the internal storage
        //directory as an argument
        recorder.setOutputFile(mFileName);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //Does some final magic to get the recorder ready.
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        refreshContents();
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public static void refreshContents() {
        Recordings_Contents = directory.listFiles();
        adapter.notifyDataSetChanged();
    }

    private void onPlay(boolean start) {
        if (start)
            startPlaying();
        else
            stopPlaying();
    }

    //Connect a fresh MediaPlayer to our player and load it
    //with the correct file, then play it
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(mFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Couldn't load media file: " + mFileName);
        }
    }

    //TODO: add a pause button for playback

    //Release resources when finished to get memory back
    private void stopPlaying() {
        player.release();
        player = null;
    }


}
