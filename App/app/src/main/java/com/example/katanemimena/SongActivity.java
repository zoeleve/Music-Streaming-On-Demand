package com.example.katanemimena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SongActivity extends AppCompatActivity {

    private String brokerIp, artistName;
    private int brokerPort;
    private ListView artistList;
    private GetSongListTask gslt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        // Pairnoume tis plirofories apo to proigoumeno activity
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        String port = intent.getStringExtra("port");
        String artist = intent.getStringExtra("artistName");

        this.brokerIp = ip;
        this.brokerPort = Integer.parseInt( port );         // Metatrepei to string se int
        this.artistName = artist;



        // Apothikeuoume tin lista
        artistList = findViewById( R.id.song_list);



        // Rythmizoume ti tha ginei otan patisei ena stoixeio tis listas
        artistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // Mathainoume poio tragoudi exei patisei o xristis
                String song = ((TextView) view).getText().toString();

                // Xekiname ena neo activity pou tha katevazei to tragoudi
                Intent myIntent = new Intent( SongActivity.this, PlayerActivity.class);

                // Symplirwnoume ta stoixeia gia na ta steilei sto epomeno akticity
                myIntent.putExtra("ip", gslt.getBrokerip());
                myIntent.putExtra("port", ""+gslt.getBrokerPort());
                myIntent.putExtra("songName", song);

                startActivity(myIntent);
            }
        });


        // Parallilo nima gia na ferei tin lista me ta tragoudia
        gslt = new GetSongListTask(brokerIp, brokerPort,  artistName,this, artistList);
        gslt.execute();
    }
}
