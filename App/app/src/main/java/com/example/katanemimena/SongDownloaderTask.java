package com.example.katanemimena;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import katanemimena.MusicFileInfo;

public class SongDownloaderTask extends AsyncTask
{

    private String brokerip;
    private int brokerPort;
    private String songName;
    private Context cntx;
    private String path ;

    private boolean songDownloaded;

    // Constructor
    public SongDownloaderTask(String brokerip, int brokerPort, String songName, Context cntx)
    {
        this.brokerip = brokerip;
        this.brokerPort = brokerPort;
        this.songName = songName;
        this.cntx = cntx;

        this.songDownloaded = false;

        this.path = cntx.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/"+ songName;
    }


    // Synartisi pou trexei parallila me to kyriws nima UI
    @Override
    protected Object doInBackground(Object[] objects) {

        Socket socket = null;

        ObjectOutputStream out= null;
        ObjectInputStream in= null;


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
            out.writeChar('G');
            out.writeObject(songName);
            out.flush();

            // Parnei tin apantisi apo ton tyxaio broker (F/N)
            char result = in.readChar();

            // an einai F tote to exei autos to tragoudi
            if( result == 'F' )
            {
                // Arxizei na diavazei to arxeio tou tragoudiou
                try {

                    MusicFileInfo mfi = (MusicFileInfo) in.readObject();

                    // Dimiourgia arxeiou stin eswteriki mnimi tou kinitou
                    File file = new File( path  );


                    try
                    {
                        // Anoigoume to stream tou arxeiou ston disko
                        FileOutputStream fos = new FileOutputStream(file);

                        int partNo = 1;



                        // Oso yparxoun plirofories gia to arxeio
                        do
                        {
                            // Tis grafei sto arxeio mesa sto kinito
                            fos.write( mfi.getFiledata(), 0, mfi.getLength() );


                            System.out.println("Elava tmima "+partNo+" tou "+ songName);
                            partNo++;

                            // Diavazei ta epomena dedomena
                            mfi = (MusicFileInfo) in.readObject();

                        }while( mfi.getFiledata() != null );


                        // Kleinei to arxeio
                        fos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    songDownloaded = true;
                }
                catch (IOException ex)
                {
                    songDownloaded = false;
                }
                catch (ClassNotFoundException ex)
                {
                    songDownloaded = false;
                }


            }

            // an einai N tote den exei autos to tragoudi
            else
            {
                songDownloaded = false;
            }

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

        if( songDownloaded == true )
        {
            Toast toast = Toast.makeText(cntx, "Song downloaded", Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(cntx, "Song hasn't downloaded", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public boolean isSongDownloaded() {
        return songDownloaded;
    }

    public String getPath() {
        return path;
    }
}
