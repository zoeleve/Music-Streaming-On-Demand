
package katanemimena;

import java.io.Serializable;

// Klassi gia ta dedomena enos komamtiou tou arxeiou
public class MusicFileInfo implements Serializable
{
    byte filedata[];        // Pinakas me byte, pou einai ta chunks
    int length;             // Megethos tou pinaka dedomenwn
    String Artistname;
    String songname;

    public MusicFileInfo(byte[] filedata, int l, String Artistname, String songname)
    {
        // Antigrafei to mikos tou buffer
        this.length = l;
        
        if(filedata != null)
        {
            // Ftiaxnei ena neo pinaka kai tou antigrafei to filedata
            this.filedata = new byte[ length ];
            System.arraycopy(filedata, 0, this.filedata, 0, length);
        }
        
        this.Artistname = Artistname;
        this.songname = songname;
    }

    public byte[] getFiledata() {
        return filedata;
    }

    public int getLength() {
        return length;
    }

    public String getArtistname() {
        return Artistname;
    }

    public String getSongname() {
        return songname;
    }
}
