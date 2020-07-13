
package katanemimena;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Nima toy publisher pou exipiretei tous broker
public class PublisherThread extends Thread
{
    private BrokerInfo brokerData;
    private ArrayList<SongData> songlist = new ArrayList<>();       // Lista me ta tragoudia
    private ArrayList <BrokerInfo> brokersInfo;                     // Kathe nima exei tis plirofories gia tou broker

    public PublisherThread(Socket socket, ArrayList<SongData> songlist, ArrayList <BrokerInfo> brokersInfo)
    {
        this.brokerData = new BrokerInfo(socket);
        this.songlist = songlist;
        this.brokersInfo = brokersInfo;
    }
    
    
    // run einai panta h synartisi pou trexei to kathe nima
    // oti douleia kanei to nima tha tin grapsoume mesa stin run
    @Override
    public void run()
    {
        // Syndesi me broker
        brokerData.connect();
        
        // Paramenei syndedemeno me ton broker
        while(true)
        {
            
            try {
                
                // Diavazei to aitima apo ton broker
                char request = brokerData.in.readChar();
                
                
                if( request == 'S')             // Anazitisi tragoudiou
                {
                    String songname = (String) brokerData.in.readObject();
                    
                    
                    boolean found = false;      // Estw oti to tragoudi den yparxei
                    SongData sd=null;
                    
                    // Anazita to tragoudi
                    for (SongData x : songlist)
                    {
                        // Elegxei an einai to idio string
                        if( x.name.equals( songname ) )
                        {
                            found = true;
                            sd = x;
                            break;
                        }
                    }
                    
                    if( found == true )
                    {
                        brokerData.out.writeChar('F');      // Found
                        brokerData.out.flush();
                        
                        // Anoigei to arxeio
                        InputStream fileStream = new FileInputStream( sd.path );
                        
                        int partNo = 1;
                        byte[] allBytes = new byte[512];        // Chunck einai 512 byte
                        int length;

                        // Oso yparxoun dedomena sto arxeio
                        while( (length=fileStream.read(allBytes)) > 0 )
                        {
                            // Ta stelnei ston broker me tin push
                            MusicFileInfo mfi = new MusicFileInfo( allBytes, length, sd.name, sd.name);
                            
                            System.out.println("Stelnw tmima "+partNo+" tou tragoudiou "+sd.name);
                            partNo++;
                            
                            push(sd.name, mfi);
                        }
                        
                        // Ta stelnei ston broker me tin push to teleutaio tmima
                        MusicFileInfo mfi = new MusicFileInfo( null, 0, sd.name, sd.name);

                        push(sd.name, mfi);
                        
                        //Kleinei to arxeio
                        fileStream.close();
                    }
                    else
                    {
                        brokerData.out.writeChar('N');      // Not found
                        brokerData.out.flush();
                    }
                }
                else if(request == 'U')         // Update twn hash pou einai ypeuthinos
                {
                    int hash = brokerData.in.readInt();
                    
                    brokerData.hash = hash;
                    
                    // Steile pisw ti lista
                    brokerData.out.writeObject( brokersInfo );
                    brokerData.out.flush();
                }
                else if( request == 'T' )       // Termatismos  nimatos an lavei to T
                {
                    break;
                }

            }
            catch (IOException ex)
            {
                Logger.getLogger(PublisherThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(PublisherThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        
        
        // Aposyndesi me broker
        brokerData.disconnect();
    }
    
    
    void push(String topic, MusicFileInfo value)
    {
        try
        {
            // apostoli tou zeugous topic value
            brokerData.out.writeObject(topic);
            brokerData.out.writeObject(value);
            brokerData.out.flush();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
