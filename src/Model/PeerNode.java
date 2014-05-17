/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Class used to pass local socket information to peers.  Goal is to allow a peer 
 * to store discovered peers in a data collection.
 * @author mcnabba
 */
public class PeerNode implements Serializable {
    
    private InetAddress address;
    private int data;
    
    public PeerNode(InetAddress address, int data)   {
        this.address = address;
        this.data = data;
    }
    
    public PeerNode(InetAddress address)    {
        this.address = address;
    }
    
    @Override
    public String toString()    {
        return this.address.toString();
    }
}
