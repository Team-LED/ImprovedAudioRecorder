package com.example.android.improvedaudiorecorder;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by lizzi on 3/24/2018.
 */

public class CustomButtons {

    public class PlayButton extends AppCompatButton {
        boolean startPlaying = true;
        OnClickListener playButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startPlaying)
                    setText("Clicked");
                else
                    setText("Unclicked");
                startPlaying = !startPlaying;
            }
        };

        public PlayButton(Context context) {
            super(context);
            setOnClickListener(playButtonListener);
        }
    }

    public class MyButton extends
            AppCompatButton {
        public MyButton(Context context) {
            super(context, null);
        }

        public MyButton(Context context, AttributeSet attrs) {
            super(context, attrs, 0);
        }

        public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }

}
