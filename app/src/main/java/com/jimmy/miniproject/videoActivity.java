package com.jimmy.miniproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.VideoView;

public class videoActivity extends AppCompatActivity {
    Button next,play;
    CheckBox cb;
    static Boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        next = (Button)findViewById(R.id.btnStart);
        play = (Button)findViewById(R.id.btnPlay);
        cb =(CheckBox)findViewById(R.id.checkBox);

        final VideoView videoView = (VideoView) findViewById(R.id.video_view);
        String videoPath = "android.resource://" + getPackageName() + "/raw/video";
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        //MediaController mediaController = new MediaController(this);
        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end = true;
                Intent intent = new Intent();
                intent.putExtra("checked",cb.isChecked());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
}
