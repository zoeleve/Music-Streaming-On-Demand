
package katanemimena;

import java.io.Serializable;


// Klasi me ta stoixeia tou tragoudiou
public class SongData implements Serializable
{
    public String name;     // Onoma tragoudiou gia tin anazitisi
    public String path;     // Monopati tragoudiou gia tin apostoli tou arxeiou
    public String artist;   // Onoma kallitexni

    public SongData(String name, String path, String artist) {
        this.name = name;
        this.path = path;
        this.artist = artist;
    }

    @Override
    public boolean equals(Object obj) {
        return name.equals(((SongData) obj).name) && artist.equals(((SongData) obj).artist);
    }

    @Override
    public String toString() {
        return name+" ("+artist+")";
    }


}
