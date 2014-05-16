/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.net.ServerSocket;

/**
 * Class used to pass local socket information to peers.  Goal is to allow a peer 
 * to store discovered peers in a data collection.
 * @author mcnabba
 */
public class PeerNode implements Serializable {
    
    private ServerSocket socket;
    
    public PeerNode(ServerSocket s)   {
        this.socket = s;
    }
    
    public ServerSocket getSocket()   {
        return this.socket;
    }
    
}
