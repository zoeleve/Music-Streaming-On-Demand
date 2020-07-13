package com.example.katanemimena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistActivity extends AppCompatActivity {

    private String brokerIp;
    private int brokerPort;
    private ListView artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);


        // Pairnoume tis plirofories apo to proigoumeno activity
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        String port = intent.getStringExtra("port");

        this.brokerIp = ip;
        this.brokerPort = Integer.parseInt( port );         // Metatrepei to string se int


        // Apothikeuoume tin lista
        artistList = findViewById( R.id.artist_list);



        // Rythmizoume ti tha ginei otan patisei ena stoixeio tis listas
        artistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // Mathainoume poion kallitexni exei patisei o xristis
                String artist = ((TextView) view).getText().toString();

                // Xekiname ena neo activity pou tha katevazei to tragoudi
                Intent myIntent = new Intent( ArtistActivity.this, SongActivity.class);

                // Symplirwnoume ta stoixeia gia na ta steilei sto epomeno akticity
                myIntent.putExtra("ip", brokerIp);
                myIntent.putExtra("port", ""+brokerPort);
                myIntent.putExtra("artistName", artist);

                startActivity(myIntent);
            }
        });


        // Parallilo nima gia na ferei tin lista me ta tragoudia
        GetArtistListTask gat = new GetArtistListTask(brokerIp, brokerPort, this, (ListView) findViewById(R.id.artist_list));
        gat.execute();
    }
}
