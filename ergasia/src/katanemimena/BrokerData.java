
package katanemimena;

import java.io.Serializable;
import java.util.ArrayList;

// BrokerData einai h klasi pou exei tis apaitoumenes plirofories
// gia na tis steilei o Publisher ston Broker
// Implements to Serializable gia na mporei olokliri h klasi na apostalei mesa apo to Socket
public class BrokerData implements Serializable
{
    public String ip;
    public int port;
    public int id;
    
    public ArrayList<SongData> artists;

    public BrokerData(String ip, int port, int id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.artists = new ArrayList<>();
    }

    // ToString gia na typwnei ta stoixeia twn tragoudiwn tou kathe broker
    @Override
    public String toString() {
        return "BrokerData{" + "ip=" + ip + ", port=" + port + ", id=" + id + ", artists=" + artists + '}';
    }

    
}
