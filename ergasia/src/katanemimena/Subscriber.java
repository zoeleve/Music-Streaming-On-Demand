package katanemimena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Subscriber
{
    
    private BrokerInfo brokerInfo;
    private String brokerIp;
    private int brokerPort;

    
    public Subscriber(String brokerIp, int brokerPort)
    {
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
    }

    
    public void init()
    {
        try {

            // Scanner gia na diavazei apo to pliktrologio to tragoudi
            Scanner scan = new Scanner(System.in);
            
            do{
                // Syndesi me tyxaio broker
                brokerInfo = new BrokerInfo(0, brokerIp, brokerPort);
                brokerInfo.connect(1);

                
                // Fernei oli tin lista me tous dimiourgous
                // Stelnei to gramma L pou tou zitaei tin lista
                brokerInfo.out.writeChar('L');
                brokerInfo.out.flush();
                
                ArrayList<SongData> allArtists = (ArrayList<SongData>) brokerInfo.in.readObject();
                brokerInfo.disconnect();
                brokerInfo.connect(1);
                
                
                for (SongData allArtist : allArtists) {
                    System.out.println(allArtist.artist);
                }
                System.out.print("Dwse ton tragoudisti pou thes (h exit) : ");
                
                // Diavazoume me nextLine to onoma tou tragoudiou
                String artistname = scan.nextLine();

                // An grapsei exit, termatizei
                if(artistname.equals("exit"))
                {
                    brokerInfo.out.writeChar('T');
                    brokerInfo.out.flush();
                    break;
                }

                // Stelnei to gramma S pou tou zitaei na kanei anazitisi
                brokerInfo.out.writeChar('S');
                brokerInfo.out.writeObject(artistname);
                brokerInfo.out.flush();


                // Parnei tin apantisi apo ton tyxaio broker (F/N)
                char result = brokerInfo.in.readChar();

                // an einai F tote to exei autos to tragoudi
                if( result == 'F' )
                {
                    ArrayList<SongData> allSongs = (ArrayList<SongData>) brokerInfo.in.readObject();
                    
                    System.out.println(allSongs);
                    
                    // Arxizei na diavazei to arxeio tou tragoudiou
                    System.out.print("Dwse to tragoudi pou thes (h exit) : ");
                
                    // Diavazoume me nextLine to onoma tou tragoudiou
                    String songname = scan.nextLine();

                    // An grapsei exit, termatizei
                    if(songname.equals("exit"))
                    {
                        brokerInfo.out.writeChar('T');
                        brokerInfo.out.flush();
                        break;
                    }
                    
                    brokerInfo.disconnect();
                    brokerInfo.connect(1);
                    
                    // Stelnei to gramma G pou tou zitaei na ferei to tragoudi
                    brokerInfo.out.writeChar('G');
                    brokerInfo.out.writeObject(songname);
                    brokerInfo.out.flush();
                    
                    // Parnei tin apantisi apo ton tyxaio broker (F/N)
                    result = brokerInfo.in.readChar();
                    if(result == 'F')
                        pull( artistname );
                    else
                        System.out.println("Dem vrethike");
                }
                
                // an einai N tote den exei autos to tragoudi
                else
                {
                    result = brokerInfo.in.readChar();
                    
                    
                    if( result == 'F' )     // Vrethike se allo broker
                    {
                        // Lamvanoume tin lista me tous broker pou einai ypeuthinoi gia kathe tragoudi
                        Info broker = (Info) brokerInfo.in.readObject();


                        // Aposyndesi apo ton palio broker
                        brokerInfo.disconnect();

                        // Enimerwsi tou twnn stoixeiwn tou neou broker
                        brokerInfo.ip = broker.brokerIp;
                        brokerInfo.port = broker.brokerPort;
                        brokerInfo.id = broker.BrokerId;

                        // Syndesi ston neo broker
                        brokerInfo.connect(1);

                        // Stelnei to gramma S pou tou zitaei na kanei anazitisi
                        brokerInfo.out.writeChar('S');
                        brokerInfo.out.writeObject(artistname);

                        // Anamenei tin apantisi
                        result = brokerInfo.in.readChar();

                        if(result == 'F')
                        {
                            // Diavazei to arxeio
                            pull( artistname );
                        }
                        else            // Den vrethike to tragoudi
                        {
                            System.out.println("Den vrethike to tragoudi");
                        }
                    }
                    else        // Den vrethike to tragoudi
                    {
                        System.out.println("Den vrethike to tragoudi");
                    }
                    
                }

                // Anaparagogi tou tragoudiou
                playData(artistname);
                
            
            }while(true);
            
            // Aposyndesi apo ton broker
            brokerInfo.disconnect();
            
        } catch (IOException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void pull(String topic)
    {
        try {
            
            MusicFileInfo mfi = (MusicFileInfo) brokerInfo.in.readObject();
            
            // Dimiourgia arxeiou ston disko
            File file = new File( mfi.songname );
            
            
            try 
            {
                // Anoigoume to stream tou arxeiou ston disko
                FileOutputStream fos = new FileOutputStream(file);
                
                int partNo = 1;
                
                // Oso yparxoun plirofories gia to arxeio
                do
                {
                    // Tis grafei sto disko
                    fos.write( mfi.filedata, 0, mfi.length );
                    
                    System.out.println("Elava tmima "+partNo+" tou "+topic);
                    partNo++;
                    
                    // Diavazei ta epomena dedomena
                    mfi = (MusicFileInfo) brokerInfo.in.readObject();
                    
                }while( mfi.filedata != null );
                
                // Kleinei to arxeio
                fos.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch (IOException ex) {
                Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void playData(String songname)
    {
        System.out.println("Playing .... "+songname);
    }
    
    public static void main(String[] args)
    {
        String randomBrokerIp = "localhost";
        int randomBrokerPort = 1475;
        
        
        Subscriber s = new Subscriber(randomBrokerIp, randomBrokerPort);
        
        s.init();
        
    }

    
}
