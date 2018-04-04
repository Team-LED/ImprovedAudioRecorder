package com.example.android.improvedaudiorecorder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.example.android.improvedaudiorecorder.R;

public class DetailEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_entry);

        final AppCompatButton enterButton = findViewById(R.id.xml_enter_details_button);
        final AppCompatButton skipButton = findViewById(R.id.xml_skip_details_button);

        final EditText title_field = findViewById(R.id.edit_title);
        final EditText interviewee_field = findViewById(R.id.edit_interviewee);
        final EditText date_field = findViewById(R.id.edit_date);


        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                1. grab data from editTexts. Use title to gen filename
                   ie, mFileName = directory.toString() + '/' + [input from titlefield] + ".3gp";

                    String titleInput = title_field.getText().toString();
                    String intervieweeInput =
                    String fileNameInput = directory.toString() + '/' + titleInput + ".3gp";

                2. go to recording screen. Use filename to get recorder ready.
                3. If user precedes to make recording, then save audio file and create a database
                   object with these attributes.
                   Else, forget all temp data and go back to main list.

                 */

                //Dummy Code, right now enter just switches us back to the main activity
                Intent i = new Intent(DetailEntryActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take user straight to record screen
                //Have some default data as a placeholder until user creates details
                //We will use the _ID field to seperate multiple "default" recordings
                //until the user does so. They can do this on the detail Edit screen.
            }
        });
    }
}
