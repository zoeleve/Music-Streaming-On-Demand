
package katanemimena;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Klasi me ta stoixeia tou kathe broker kai tin lista me ta tragoudia tou
public class BrokerInfo implements Serializable{
    
    public String ip;
    public int port;
    public int id;
    public int hash;            // hash tou broker, opoio tragoudi exei mikrotero hash tou anikei
    public Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    public ArrayList<SongData> songs;     // Lista me tragoudia pou tou antistoixoun
    
    
    // Kataskeuastis pou exei mono to socket
    public BrokerInfo(Socket so)
    {
        this.socket = so;
    }
    
    // Kataskeuastis me id ip port
    public BrokerInfo(int id, String ip, int port)
    {
        this.ip = ip;
        this.id = id;
        this.port = port;
        songs = new ArrayList<>();
    }
    
    // Syndesi me ton sygkekrimeno broker
    public void connect()
    {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Syndesi ston broker analoga me ton typo
    // 0 gia tous Publihser
    // 1 gia tous Subscriber
    public void connect(int typosPelati)
    {
        try {
            
            // Anoigei tin syndesi me to socket
            socket = new Socket(ip, port);
            
            // Anoigei tis dyo roes
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            // Grafei ti typou einai gia na katalavei o broker poio eidos (Sub h Pub) syndethike
            out.writeInt( typosPelati );
            out.flush();
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // Kleinei tis syndeseis
    public void disconnect()
    {
        try {
            
            in.close();
            out.close();
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
}
