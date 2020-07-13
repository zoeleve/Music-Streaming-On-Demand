
package katanemimena;

import java.io.Serializable;



// To info prepei na kanei implements to Serializable
// waste na mporei na stalei mesa apo to socket
public class Info implements Serializable
{

    public String brokerIp;
    public int brokerPort;
    public int BrokerId;
    public String ArtistName;

    public Info(String brokerIp, int brokerPort, int BrokerId, String ArtistName)
    {
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
        this.BrokerId = BrokerId;
        this.ArtistName = ArtistName;
    }

}
