
package katanemimena;

import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Publisher {
    
    private Socket socket;
    private ArrayList <BrokerInfo> brokersInfo;
    private boolean PublisherOn;
    private int port;
    private String folder;
    private ArrayList<SongData> songdataList;
    private int brokersNum;

    
    // Kataskeuastis, p einai port tou Publisher, folder = fakelos pou anazita gia tragoudia, kai to plithos twn broker
    public Publisher(int p, String folder, int brokersNum)
    {
        brokersInfo = new ArrayList<>();
        songdataList = new ArrayList<>();
        
        // Apothikeuei tous broker
        getBrokerList();
        
        this.folder = folder;
        this.brokersNum = brokersNum;
        port = p;
    }
    
    // Dimiourgei tin lista me tous broker
    void getBrokerList()
    {
        // Lista me tous brokers
        brokersInfo.add( new BrokerInfo(0, "192.168.2.10", 2475) );
        brokersInfo.add( new BrokerInfo(1, "192.168.2.10", 2476) );
        //brokersInfo.add( new BrokerInfo(1, "localhost", 1477) );
    }
    
    void init()
    {
        // Diavazoume ta arxeia
        readFiles(new File(folder));

        // Moirazei ta arxeia stous broker analoga tin hash synartisi
        for (SongData sd : songdataList)
        {
            int brokerid = hashTopic(sd.artist, brokersNum);
            
            // an den exei idi ton kallitexni
            if(brokersInfo.get( brokerid ).songs.contains(sd ) == false)
                brokersInfo.get( brokerid ).songs.add( sd );
        }
        
        
        // O publisher einai energos
        PublisherOn = true;
        
        try {
            
            // Dimiourgia listas gia apostoli
            ArrayList<BrokerData> brokerDatas = new ArrayList<>();
            
            for (BrokerInfo brokerInfo : brokersInfo)
            {
                BrokerData bd = new BrokerData(brokerInfo.ip, brokerInfo.port, brokerInfo.id);
                bd.artists = brokerInfo.songs;
                brokerDatas.add( bd );
            }
            
            // Syndesi me olous tous broker gia na steilei ta tragoudia pou einai ypeuthinos o kathenas
            for (BrokerInfo brokerInfo : brokersInfo)
            {
                brokerInfo.connect(0);
                brokerInfo.out.writeChar('U' );     // Stelnei U ston Broker wste na katalavei oti tou stelnei enimerwsh gia
                                                    // gia ta poia tragoudia einai ypeuthinos
                brokerInfo.out.flush();
                brokerInfo.out.writeObject( brokerDatas );  // Stelnei oli tin lista me ta tragoudia pou einai ypeuthinos
                brokerInfo.out.flush();
                brokerInfo.disconnect();
            }

            
            // anoigma twn socket gia na syndethoun oi broker
            ServerSocket serverSocket = new ServerSocket(port);
        
            System.out.println("Publisher xekinise");
            
            while (PublisherOn == true)
            {                
                // O server perimenei gia nees syndeseis
                Socket socket = serverSocket.accept();
                
                // Nima gia ton xeirismo twn broker pou syndeontai
                PublisherThread pThread = new PublisherThread(socket, songdataList, brokersInfo);

                // Trexoume to nima
                pThread.start();
            }
            
            // Kleinoume to server socket
            serverSocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    

    // Fernei se lista ola ta arxeia enos fakelou
    public void readFiles( File folder)
    {
        String artist_name = null;
        String song_name = null;

        
        for (File file : folder.listFiles())
        {
            if(file.isHidden()) continue;
            
            try {

                // Ftiaxnei antikeimeno MP3
                Mp3File mp3 = new Mp3File( file.getAbsolutePath() );

                // an exei to tag v1
                if (mp3.hasId3v1Tag())
                {
                    if(mp3.getId3v1Tag().getArtist()!= null && mp3.getId3v1Tag().getArtist().equals("") == false)
                    {
                        artist_name = mp3.getId3v1Tag().getArtist();
                    }
                }
                // an exei to tag v2
                if (mp3.hasId3v2Tag())
                {
                    if(mp3.getId3v2Tag().getArtist()!= null && mp3.getId3v2Tag().getArtist().equals("") == false)
                    {
                        artist_name = mp3.getId3v2Tag().getArtist();
                    }
                }

                SongData songData = new SongData(file.getName(), file.getAbsolutePath(), artist_name);
                songdataList.add( songData );

            } catch (Exception ex) {
                System.out.println("Apotyxia anagnwsis arxeiwn");
            }

        }
    }
    
    // Vasismeno sto https://www.geeksforgeeks.org/md5-hash-in-java/
    // Pairnei to topic kai to plithos twn broker
    public int hashTopic(String topic, int max) 
    { 
        try {

            // Epilogi algorithmou katakermatismou
            MessageDigest messageDigest = MessageDigest.getInstance("MD5"); 
  
            // Kanei digest to keimeno - diladi to teliko hash
            byte[] array = messageDigest.digest( topic.getBytes() ); 
  
            // Metatropi se int
            int hashed_no = new BigInteger(1, array).intValue();


            return Math.abs( hashed_no ) % max;
        }  
        catch (NoSuchAlgorithmException e)
        { 
            throw new RuntimeException(e); 
        } 
    } 
    
    
    public static void main(String[] args)
    {
        int brokerNum = 2;
        
        int port = 1234;
        
        // Dimiourgia tou Publisher
        Publisher pub = new Publisher(port, "dataset2/dataset2", brokerNum);
        
        
        // Ekkinisi tou Publisher
        pub.init();
      
        System.out.println("Publisher termatise");
        
        
    }
    
    
}
