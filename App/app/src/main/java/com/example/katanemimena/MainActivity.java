package com.example.katanemimena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Vriskoume to koumpi connect
        Button button = findViewById( R.id.button_connect );

        // Thetoume to event gia na ektelestei otan o xristis patisei to koumpi
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ti tha ginei otan patithei to koumpi

                // Vriskoume ta view pou tha exei symplirwsei tin ip kai port
                TextView textViewIp = findViewById( R.id.textViewIP );
                TextView textViewPort = findViewById( R.id.textViewPort );

                // Exagoume ta stoixeia IP/PORT
                String ip = textViewIp.getText().toString();
                String port = textViewPort.getText().toString();

                // Xekiname ena neo activity pou tha exei tin lista me ta tragoudia
                Intent myIntent = new Intent( MainActivity.this, ArtistActivity.class);

                // Symplirwnoume ta stoixeia gia na ta steilei sto epomeno akticity
                myIntent.putExtra("ip", ip);
                myIntent.putExtra("port", port);

                startActivity(myIntent);
            }
        });
    }
}
