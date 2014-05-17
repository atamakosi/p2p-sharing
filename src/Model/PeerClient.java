/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles discovery of peers and publishes local data to peers.  This
 * sets up awareness of the p2p network.
 * @author mcnabba
 */
public class PeerClient implements Runnable {
    
    private static final int PORT = 33001;
    private boolean run = true;
    private MulticastSocket mSocket;
    private final long SLEEP = 5000;
    private static PeerNode peerNode;
    private static final String SERVER_IP = "Server_IP";
    private final int outputStream = 15000;
    
    public PeerClient(PeerNode peerNode) {
        try {
            this.mSocket = new MulticastSocket(PORT);
            this.peerNode = peerNode;
        } catch (IOException ex) {
            Logger.getLogger(PeerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()   {
        ObjectInputStream is = null;
        
        try {
            Thread.sleep(SLEEP);
        } catch (InterruptedException ex) {
            Logger.getLogger(PeerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
                
       
    }
    
    public void stop()  {
        this.run = false;
    }
}
