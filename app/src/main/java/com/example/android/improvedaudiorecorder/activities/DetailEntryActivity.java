package com.example.android.improvedaudiorecorder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.example.android.improvedaudiorecorder.R;

public class DetailEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_entry);

        final AppCompatButton enterButton = findViewById(R.id.xml_enter_details_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailEntryActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
