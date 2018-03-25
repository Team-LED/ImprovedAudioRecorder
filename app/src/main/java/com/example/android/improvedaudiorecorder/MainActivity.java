package com.example.android.improvedaudiorecorder;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ImprovedAudioRecorder";
    protected static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    protected boolean permissionToRecordAccepted = false;
    protected String [] permissions = {android.Manifest.permission.RECORD_AUDIO};


    public String mFileName = null;
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
        final AppCompatButton playButton = (AppCompatButton)findViewById(R.id.xml_play_button);
        final AppCompatButton recordButton = (AppCompatButton) findViewById(R.id.xml_record_button);
        final AppCompatButton pauseButton = (AppCompatButton) findViewById(R.id.xml_pause_button);
        final Button enterButton = findViewById(R.id.enter_button);

        //CREATE BLOCK THAT LOOKS FOR PRECREATED FILES AND SETS PLAY ENABLED IF THERE ARE SOME

       final EditText recordingNameField = (EditText)findViewById(R.id.recordingNameField);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                if(startPlaying) {
                    playButton.setText("Stop Playing");
                    recordButton.setEnabled(false);
                }
                else {
                    playButton.setText("Start Playing");
                    recordButton.setEnabled(true);
                }
                startPlaying = !startPlaying;
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(startRecording);
                if(startRecording) {
                    recordButton.setText("Stop Recording");
                    playButton.setEnabled(false);
                }
                else {
                    recordButton.setText("Start Recording");
                    playButton.setEnabled(true);
                }
                startRecording = !startRecording;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String userInput = recordingNameField.getText().toString();
                mFileName = getExternalCacheDir().getAbsolutePath() + '/' + userInput + ".3gp";
                Toast.makeText(MainActivity.this, mFileName, Toast.LENGTH_SHORT).show();
                file_name_entered = true;
                recordButton.setEnabled(true);
            }
        });


    }
    private void onRecord(boolean start){
        if(start)
            startRecording();
        else
            stopRecording();
    }

    private void startRecording(){
        recorder = new MediaRecorder();
        //Sets up the recorder for audio as opposed to video,
        //and a few other things related to file format and encoding.
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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
        recorder.stop();
        recorder.release();
        recorder = null;
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
