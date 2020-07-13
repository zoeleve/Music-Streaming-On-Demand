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

import katanemimena.Info;
import katanemimena.SongData;

public class GetSongListTask extends AsyncTask {


    private String brokerip;
    private String artistName;
    private int brokerPort;
    private Context cntx;
    ArrayList<SongData> allSongs;
    ListView listView;

    private boolean listDownloaded;

    // Constructor
    public GetSongListTask(String brokerip, int brokerPort, String artistname, Context cntx, ListView lv) {
        this.brokerip = brokerip;
        this.brokerPort = brokerPort;
        this.cntx = cntx;
        this.listView = lv;
        this.listDownloaded = false;
        this.artistName = artistname;
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


            // Grafoume to tragoudi pou zitame na mas steilei
            out.writeChar('S');
            out.writeObject( artistName );
            out.flush();

            // Parnei tin apantisi apo ton tyxaio broker (F/N)
            char result = in.readChar();

            // an einai N tote den exei autos ton kallitexni
            if( result == 'N' )
            {
                result = in.readChar();


                if( result == 'F' )     // Vrethike se allo broker
                {
                    // Lamvanoume tin lista me tous broker pou einai ypeuthinoi gia kathe tragoudi
                    Info broker = (Info) in.readObject();


                    // Aposyndesi apo ton palio broker
                    in.close();
                    out.close();
                    socket.close();

                    // Enimerwsi tou twnn stoixeiwn tou neou broker
                    this.brokerip = broker.brokerIp;
                    this.brokerPort = broker.brokerPort;


                    // Syndesi ston neo broker
                    socket = new Socket(brokerip, brokerPort);

                    // Anoigei tis dyo roes
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    // Grafei ti typou einai gia na katalavei o broker poio eidos (Sub h Pub) syndethike
                    out.writeInt( 1 );          // 1 einai o Subscriber
                    out.flush();

                    // Stelnei to gramma S pou tou zitaei na kanei anazitisi
                    out.writeChar('S');
                    out.writeObject(artistName);

                    // Anamenei tin apantisi
                    result = in.readChar();

                    if(result == 'N')
                    {
                        // Den vrethike to tragoudi
                        System.out.println("Den vrethike o kallitexnis");
                    }
                }
                else        // Den vrethike to tragoudi
                {
                    System.out.println("Den vrethike broker pou na exei ton kallitexni");
                }

            }


            // an telika vrethike, tote pairnei tin lista
            if(result == 'F')
            {
                listDownloaded = true;
                allSongs = (ArrayList<SongData>) in.readObject();
            }
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
            for (SongData sd: allSongs) {

                dataList.add( sd.name );
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

    public String getBrokerip() {
        return brokerip;
    }

    public int getBrokerPort() {
        return brokerPort;
    }
}
