/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to pass local socket information to peers.  Goal is to allow a peer 
 * to store discovered peers in a data collection.
 * @author mcnabba
 */
public class PeerNode  {
       
    private final int PORT = 33000;
    private static List<PeerNode> peers;
    private boolean run = true;
    private Socket s;
    private Thread commsThread;
    private Thread discThread;
    private PeerComms pComms;
    private PeerDiscovery pDisc;
    
    public PeerNode()   {
        peers = new ArrayList<>();
        pDisc = new PeerDiscovery(peers);
        pComms = new PeerComms();
        commsThread = new Thread(pComms);
        discThread = new Thread(pDisc);
        
    }

    public PeerNode(Socket s)   {
        this.s = s;
    }
 
    public void sendFileName(String fileName, Socket s)  {
        
    }
    
    public void stop()  {
        pComms.stopRun();
        pDisc.stopRun();
        try {
            
            commsThread.join();
            discThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<PeerNode> getPeers()    {
        return peers;
    }
    
    @Override
    public String toString()    {
        String str = null;
        try {
            str = InetAddress.getLocalHost().getAddress().toString();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }
    
    public void start() {
        System.out.println("Threads starting...");
        commsThread.start();
        discThread.start(); 
    }
}
