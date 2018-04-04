package com.example.android.improvedaudiorecorder.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.android.improvedaudiorecorder.data.RecordingDbHelper;
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

    //For accessing and storing audio files
    public static File directory = null;
    public static File Recordings_Contents[] = null;

    //For accessing the database
    private RecordingDbHelper dbHelper;

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
        final AppCompatButton play_button = (AppCompatButton) findViewById(R.id.xml_play_button);
        final AppCompatButton record_button = (AppCompatButton) findViewById(R.id.xml_record_button);
        final AppCompatButton new_button = (AppCompatButton) findViewById(R.id.xml_new_button);
        final Button enter_button = findViewById(R.id.xml_enter_button);
        final ListView listView = findViewById(R.id.recording_container);

        //PRE-DATABASE IMPLEMENTATION
        //From audio files, create recording objects and load into arraylist, which will
        //then be shown in the listview.
        for (int i = 0; i < Recordings_Contents.length; i++) {
            listOfRecordings.add(new recording(Recordings_Contents[i].toString()));
        }

        final EditText recording_name_field = (EditText) findViewById(R.id.recordingNameField);

        new_button.setEnabled(true);

        //Set listeners for buttons.
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                if (startPlaying) {
                    play_button.setText("Stop Playing");
                    record_button.setEnabled(false);
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            play_button.setText("Start Playing");
                            stopPlaying();
                            startPlaying = !startPlaying;
                            if (readyToRecord)
                                record_button.setEnabled(true);
                        }
                    });
                } else {
                    play_button.setText("Start Playing");
                    if (readyToRecord)
                        record_button.setEnabled(true);
                }
                startPlaying = !startPlaying;
            }
        });

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRecord(startRecording);
                if (startRecording) {
                    record_button.setText("Stop Recording");
                    play_button.setEnabled(false);
                    recording_name_field.setEnabled(false);
                } else {
                    record_button.setText("Start Recording");
                    recording_name_field.setEnabled(true);
                    //SAVE FILE, ADD TO LIST
                    listOfRecordings.add(new recording(mFileName, userInput));
                    //adapter.notifyDataSetChanged();
                    refreshContents();
                    recording_name_field.getText().clear();
                    recording_name_field.setEnabled(true);
                    readyToRecord = false;
                    record_button.setEnabled(false);
                    mFileName = null;
                }
                startRecording = !startRecording;
            }
        });

        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DetailEntryActivity.class);
                startActivity(i);
            }
        });

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput = recording_name_field.getText().toString();
                mFileName = directory.toString() + '/' + userInput + ".3gp";
                file_name_entered = true;
                readyToRecord = true;
                record_button.setEnabled(true);
            }
        });

        //Database access
        /*
        dbHelper = new RecordingDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        */

        //PREDATABASE IMPLAMENTATION
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
                play_button.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
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
