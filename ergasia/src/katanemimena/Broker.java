package katanemimena;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Broker {
    
    private boolean brokerOn;
    private ServerSocket serverSocket;
    private int port;
    private int myhash;
    public static int myid;
    private int brokersNum;
    
    // Pinakes me tis plirofories gia tous subscribers (ip/port)
    private String [] publisherIp;
    private int [] publisherPort;
    
    // Lista me tis plirofories twn allwn broker
    public static ArrayList<BrokerData> brokersData;
    
    
    public Broker(int p, int myid, int brokersNum, String []publisherIp, int []publisherPort)
    {
        port = p;
        this.brokersNum = brokersNum;
        this.myid = myid;
        
        this.publisherIp = publisherIp;
        this.publisherPort = publisherPort;
        
        brokersData = new ArrayList<>();
        
        try {
            // Ypologsimos gia to hash pou einai ypeuthinos
            myhash = calculateKeys( InetAddress.getLocalHost()+""+port, brokersNum);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Broker "+myid+" started");
    }
    
    public void init()
    {
        
        try {

            // O broker einai energos
            brokerOn = true;
        
        
            // anoigma twn socket gia na syndethoun oi consumer kai oi publisher
            serverSocket = new ServerSocket(port);
            
            while (brokerOn == true)
            {                
                // O server perimenei gia nees syndeseis apo tous consumer
                Socket socket = serverSocket.accept();
                
                // Nima gia ton xeirismo twn pelatwn pou syndeontai
                BrokerThread brokerThread = new BrokerThread(socket, publisherIp, publisherPort);

                // Trexoume to nima
                brokerThread.start();
            }
            
            // Kkleinoume to server socket
            serverSocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    // Vasismeno sto https://www.geeksforgeeks.org/md5-hash-in-java/
    // Text einai i ip tou kai max einai to plithos twn broker
    public int calculateKeys(String text, int max) 
    { 
        try {

            // Epilogi algorithmou katakermatismou
            MessageDigest md = MessageDigest.getInstance("MD5"); 
  
            // Kanei digest to keimeno - diladi to teliko hash
            byte[] bytesDIgested = md.digest(text.getBytes()); 
  
            // Metatropi se Biginteger
            BigInteger number = new BigInteger(1, bytesDIgested); 
  
            // Metatropi se int
            int hashed_no = number.intValue();


            return Math.abs( hashed_no ) % max;
        }  
        catch (NoSuchAlgorithmException e)
        { 
            throw new RuntimeException(e); 
        } 
    } 
    
    
    public static void main(String[] args) {
        
        int brokersNum = 2; 
        
        String [] subscribersIp = {"localhost"};
        int [] subscribersPort = {1234};
        
        //Broker bro = new Broker(1475, 0, brokersNum, subscribersIp, subscribersPort);
        Broker bro = new Broker(1476, 1, brokersNum, subscribersIp, subscribersPort);
        //Broker bro = new Broker(1477, 2, brokersNum, subscribersIp, subscribersPort);
        
        bro.init();
    }
}
