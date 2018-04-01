package com.example.android.improvedaudiorecorder;

import android.content.Context;
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

import com.example.android.improvedaudiorecorder.model.recording;

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
    protected String [] permissions = {android.Manifest.permission.RECORD_AUDIO};
    protected ArrayList<recording> recordings = new ArrayList<recording>();
    protected static recordingAdapter adapter = null;

    //3-28-18
    protected static File directory = null;
    protected static File Recordings_Contents[] = null;

    public String mFileName = null;
    public String userInput = null;
    boolean readyToRecord = false;
    boolean startPlaying = true;
    boolean startRecording = true;
    boolean paused = true;
    boolean file_name_entered = false;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(!permissionToRecordAccepted)
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions,REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_main);

        //3-28-18
        //switch to MODE_PRIVATE
        directory = this.getDir("Recordings", MODE_PRIVATE);
        Recordings_Contents = directory.listFiles();
        //Toast.makeText(this, directory.toString(), Toast.LENGTH_SHORT).show();

        final AppCompatButton playButton = (AppCompatButton)findViewById(R.id.xml_play_button);
        final AppCompatButton recordButton = (AppCompatButton) findViewById(R.id.xml_record_button);
        final AppCompatButton pauseButton = (AppCompatButton) findViewById(R.id.xml_pause_button);
        final Button enterButton = findViewById(R.id.enter_button);
        final ListView listView = findViewById(R.id.recording_container);

        //CREATE BLOCK THAT LOOKS FOR PRECREATED FILES AND SETS PLAY ENABLED IF THERE ARE SOME
        for(int i = 0; i < Recordings_Contents.length; i++){
            recordings.add(new recording(Recordings_Contents[i].toString()));
        }
       final EditText recordingNameField = (EditText)findViewById(R.id.recordingNameField);
        pauseButton.setEnabled(true);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                if(startPlaying) {
                    playButton.setText("Stop Playing");
                    recordButton.setEnabled(false);
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playButton.setText("Start Playing");
                            stopPlaying();
                            startPlaying = !startPlaying;
                            if(readyToRecord)
                                recordButton.setEnabled(true);
                        }
                    });
                }
                else {
                    playButton.setText("Start Playing");
                    if(readyToRecord)
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
                if(startRecording) {
                    recordButton.setText("Stop Recording");
                    playButton.setEnabled(false);
                    recordingNameField.setEnabled(false);
                }
                else {
                    recordButton.setText("Start Recording");
                    recordingNameField.setEnabled(true);
                    //SAVE FILE, ADD TO LIST
                    recordings.add(new recording(mFileName, userInput));
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

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onpause();
                if(paused)
                    pauseButton.setText("Paused");
                else
                    pauseButton.setText("Pause");
                paused = !paused;
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

        adapter = new recordingAdapter(this, recordings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recording listItem = (recording) listView.getItemAtPosition(position);
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (position == i) {
                        View temp = listView.getChildAt(i);
                        TextView txtView = ((TextView)temp.findViewById(R.id.item_name));
                        txtView.setTextColor(getResources().getColor(R.color.colorAccent));

                    }
                    else{
                        View temp = listView.getChildAt(i);
                        TextView txtView = ((TextView)temp.findViewById(R.id.item_name));
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

    private void onpause(){
        for(int i = 0; i < Recordings_Contents.length; i++){
            Toast.makeText(this, Recordings_Contents[i].toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private void onRecord(boolean start){
        if(start)
            startRecording();
        else
            stopRecording();
    }

    private void startRecording(){
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

    private void stopRecording(){
        refreshContents();
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    protected static void refreshContents(){
        Recordings_Contents = directory.listFiles();
        adapter.notifyDataSetChanged();
    }
    private void onPlay(boolean start){
        if(start)
            startPlaying();
        else
            stopPlaying();
    }

    //Connect a fresh MediaPlayer to our player and load it
    //with the correct file, then play it
    private void startPlaying(){
        player = new MediaPlayer();
        try{
            player.setDataSource(mFileName);
            player.prepare();
            player.start();
        }
        catch(IOException e){
            Log.e(LOG_TAG, "Couldn't load media file: " + mFileName);
        }
    }

    //TODO: add a pause button for playback

    //Release resources when finished to get memory back
    private void stopPlaying(){
        player.release();
        player = null;
    }



}
