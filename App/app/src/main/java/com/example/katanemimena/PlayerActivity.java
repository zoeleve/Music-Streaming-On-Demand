package com.example.katanemimena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayerActivity extends AppCompatActivity {

    private String brokerip;
    private int brokerPort;
    private String songName;
    private Button playButton;
    private SongDownloaderTask task;
    private MediaPlayer mp;
    private boolean media_player_created = false;            // Mas deixnei an to MediaPlayer exei ftiaxtei i oxi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Pairnoume tis plirofories apo to proigoumeno activity
        Intent intent = getIntent();
        brokerip = intent.getStringExtra("ip");
        brokerPort = Integer.parseInt( intent.getStringExtra("port") );
        songName = intent.getStringExtra("songName");

        // Pairnoume to koumpi
        playButton = findViewById( R.id.button_play );
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // An exei katevei to tragoudi
                if(task.isSongDownloaded())
                {
                    // An to MediaPlayer den exei ftiaxtei, tote to ftiaxnei
                    if(media_player_created == false)
                    {
                        // To monopati tou treagoudiou einai apo to task pou to katevase (mesw tou getter)
                        mp = MediaPlayer.create(PlayerActivity.this, Uri.parse(task.getPath()));
                        media_player_created = true;
                    }

                    // An den exei xeinisei xekinaei
                    if(mp.isPlaying() == true)
                    {
                        mp.pause();
                        playButton.setText("Play");
                    }
                    else
                    {
                        mp.start();
                        playButton.setText("Pause");
                    }
                }
            }
        });

        // Xekiname to parallilo nima
        task = new SongDownloaderTask(brokerip, brokerPort, songName, getApplicationContext());
        task.execute();
    }
}
