
package katanemimena;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Nima tou broker pou exypiretei tous publisher kai subscriber
public class BrokerThread extends Thread
{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String [] publisherIp;
    private int [] publisherPort;

    public BrokerThread(Socket socket, String [] publisherIp, int [] publisherPort) {
        this.socket = socket;
        this.publisherIp = publisherIp;
        this.publisherPort = publisherPort;
    }
    
    
    // Run pou ektelei tin douleia tou kathe nimatos
    @Override
    public void run()
    {
        
        Publisher p = null;
        Subscriber s = null;
        
        try {
            // Apofasizoume an einai publisher h subscriber
            // 0 gia tous publisher kai 1 gia subscriber
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            int typos = in.readInt();
            
            if(typos == 0)
            {
                // einai publisher
                acceptConnection(p);
            }
            else if(typos == 1)
            {
                // einai subscriber
                acceptConnection(s);
            }
            else
            {
                // Agnoooume ton agnwsto
                System.out.println("Agnwstos typos pelati");
            }
            
            in.close();
            out.close();
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void acceptConnection(Publisher p)
    {
        try {
            System.out.println("Syndethike publisher");
            
            
            char c = in.readChar();
            
            // Xreiazetai enimerwsi gia ta tragoudia pou einai ypeuthinos
            if(c == 'U')
            {
                // Diavazei tin lista me ta tragoudia pou einai ypeuthinos
                ArrayList<BrokerData> list = (ArrayList<BrokerData>) in.readObject();
                
                // Tin antigrafei stin diki tou lista
                for (BrokerData bd : list)
                {
                    Broker.brokersData.add( bd );
                }
                
                // Typwnei gia poia stoixei einai ypeuthinos o kathe broker
                for (BrokerData brokerData : Broker.brokersData) {
                    
                    System.out.println(brokerData);
                }
                
                System.out.println("Updated");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void acceptConnection(Subscriber s)
    {
        System.out.println("Syndethike subscriber");
        
        String artistname = "";
        
        try {

            char c = in.readChar();
            
            // O subscriber anazita tin lista me tous kallitexnes
            if(c == 'L')
            {
                ArrayList<SongData> allArtists = new ArrayList<>();
                
                // Anazita kai tin diki tou lista me ta tragoudia
                for (BrokerData brokerInfo : Broker.brokersData) {
                    
                    // Psaxnei tin lista 
                    for (SongData artis : brokerInfo.artists)
                    {
                        boolean found = false;
                        // An to vrei synexizei
                        for (SongData allArtist : allArtists) {

                            if(allArtist.artist.equals(artis.artist)){

                                found = true;
                                break;
                            }
                        }

                        // an oxi to vazei ston lista
                        if(found == false){
                            allArtists.add(artis);
                        }
                    }
                }   
            
                out.writeObject( allArtists );
                out.flush();
            }
            // O subscriber fernei ola ta tragoudia tou tragoudisti
            else if(c == 'S')
            {
                // Diavazei to onoma tou tragoudisti
                artistname = (String) in.readObject();
                boolean found = false;
                ArrayList<SongData> allSongs = new ArrayList<>();
                
                // Anazita kai tin diki tou lista me ta tragoudia
                for (BrokerData brokerInfo : Broker.brokersData) {
                    
                    // An vrei to diko tou id
                    if( brokerInfo.id == Broker.myid )
                    {
                        // Psaxnei tin lista 
                        for (SongData artis : brokerInfo.artists)
                        {
                            // An to vrei synexizei
                            if( artis.artist.equals(artistname) )
                            {
                                found = true;
                                allSongs.add(artis);
                                //break;
                            }
                        }
                    }
                }
                
                // An to vrike proxwraei stin syndesh me ton Publisher
                if( found == true )
                {
                    // Stelnei F (found) ston subscriber
                    out.writeChar('F');
                    out.flush();
                    
                    // Stelnei pisw tin lista
                    out.writeObject( allSongs );
                    out.flush();
                }
                else
                {
                    // Stelnei N (not found) ston Subscriber
                    out.writeChar('N');
                    out.flush();
                    
                    Info info = null;
                    
                    // Anazita stin lista me tous allous broker an exoun to tragoudi
                    for (BrokerData brokerInfo : Broker.brokersData) {

                        // Psaxnei tin lista tou kathe broker
                        for (SongData song : brokerInfo.artists)
                        {
                            // An to vrei synexizei
                            if( song.artist.equals(artistname) )
                            {
                                found = true;
                                
                                info = new Info(brokerInfo.ip, brokerInfo.port, brokerInfo.id, artistname);
                                
                                break;
                            }
                        }
                    }
                    
                    
                    if(found == true)
                    {
                        // Vrethike o broker pou to exei
                        out.writeChar('F');
                        out.flush();
                        
                        // Stelnei pisw ta stoixeia tou broker pou exei ton kallitexni
                        out.writeObject(info);
                        out.flush();
                    }
                    else
                    {
                        // Telos, o kallitexnis den yparxei
                        out.writeChar('N');
                        out.flush();
                    }
                    
                }
            }
            // O subscriber anazita kapoio tragoudi
            else if(c == 'G')
            {
                // Diavazei to onoma tou tragoudiou
                String songname = (String) in.readObject();
                boolean found = false;
                
                // Anazita kai tin diki tou lista me ta tragoudia
                for (BrokerData brokerInfo : Broker.brokersData) {
                    
                    // An vrei to diko tou id
                    if( brokerInfo.id == Broker.myid )
                    {
                        // Psaxnei tin lista 
                        for (SongData artis : brokerInfo.artists)
                        {
                            // An to vrei synexizei
                            if( artis.name.equals(songname) )
                            {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                
                // An to vrike proxwraei stin syndesh me ton Publisher
                if( found == true )
                {
                    // Stelnei F (found) ston subscriber
                    out.writeChar('F');
                    out.flush();
                    
                    notifyPublisher(songname);
                }
                else
                {
                    // Stelnei N (not found) ston Subscriber
                    out.writeChar('N');
                    out.flush();
                }
            }
            // An grapsei T simainei oti diakoptei tin syndesi
            else if( c == 'T' )
            {
                return;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }

    private void notifyPublisher(String songname)
    {
        try {
            
            char c;
            
            // Syndeetai se olous tous publisher gia na zitisei to tragoudi
            for (int i = 0; i < publisherIp.length; i++)
            {

                // Syndesi me ton publisher gia tin metafora
                Socket pubSocket = new Socket(publisherIp[i], publisherPort[i]);

                ObjectOutputStream pubOut = new ObjectOutputStream( pubSocket.getOutputStream() );
                ObjectInputStream pubIn = new ObjectInputStream( pubSocket.getInputStream() );

                // Ton enimerwnei gia to poio tragoudi zitaei
                pubOut.writeChar('S');
                pubOut.writeObject( songname );
                pubOut.flush();

                // Anamenei to apotelesma F/N
                c = pubIn.readChar();


                // An to vrike
                if( c == 'F' )
                {
                    MusicFileInfo mfi = null;

                    do
                    {
                        // Diavazei to topic/value apo ton publisher
                        // Antistoixei sto diavasma ths push tou Publisher
                        String topic = (String) pubIn.readObject();
                        mfi = (MusicFileInfo) pubIn.readObject();

                        // To stelnei ston subscriber
                        out.writeObject( mfi );
                        out.flush();

                    }while( mfi.filedata != null );
                }

                // Ton enimerwnei gia termatismo tis syndesis
                pubOut.writeChar('T');
                pubOut.flush();

                pubIn.close();
                pubOut.close();

                pubSocket.close();
                
                // An vrethike tote telos h anazitisi kai stous allous publisher
                if( c== 'F')
                {
                    break;
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BrokerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
