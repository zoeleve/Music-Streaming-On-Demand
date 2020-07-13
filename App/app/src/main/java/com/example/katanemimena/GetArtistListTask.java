package com.example.katanemimena;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import katanemimena.SongData;

public class GetArtistListTask extends AsyncTask {


    private String brokerip;
    private int brokerPort;
    private Context cntx;
    ArrayList<SongData> allArtists;
    ListView listView;

    private boolean listDownloaded;

    // Constructor
    public GetArtistListTask(String brokerip, int brokerPort, Context cntx, ListView lv) {
        this.brokerip = brokerip;
        this.brokerPort = brokerPort;
        this.cntx = cntx;
        this.listView = lv;
        this.listDownloaded = false;
    }


    @Override
    protected Object doInBackground(Object[] objects) {


        Socket socket = null;

        ObjectOutputStream out = null;
        ObjectInputStream in = null;


        try {

            // Anoigei tin syndesi me to socket
            socket = new Socket(brokerip, brokerPort);

            // Anoigei tis dyo roes
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Grafei ti typou einai gia na katalavei o broker poio eidos (Sub h Pub) syndethike
            out.writeInt( 1 );          // 1 einai o Subscriber
            out.flush();


            // Grafoume L gia na ferei tin lista
            out.writeChar('L');
            out.flush();

            // Pairnei tin lista me tous kallitexnes
            allArtists = (ArrayList<SongData>) in.readObject();


            listDownloaded = true;

        }
        catch (ClassNotFoundException ex)
        {
            System.out.println( ex );
        }
        catch (IOException ex)
        {
            System.out.println( ex );
        }
        finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }


    // Ekteleitai otan teleiwsei to Parallilo nima
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if( listDownloaded == true )
        {
            Toast toast = Toast.makeText(cntx, "List downloaded", Toast.LENGTH_LONG);
            toast.show();


            List<String> dataList = new ArrayList<String>();

            // Antigrafi twn kallitexnwn
            for (SongData sd: allArtists) {

                dataList.add( sd.artist );
            }


            // Perasma stin lista
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(cntx, android.R.layout.simple_list_item_1, dataList);
            listView.setAdapter(arrayAdapter);
        }
        else
        {
            Toast toast = Toast.makeText(cntx, "List hasn't downloaded", Toast.LENGTH_LONG);
            toast.show();
        }

    }

}
