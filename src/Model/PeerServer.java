/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcnabba
 */
public class PeerServer implements Runnable {
    
    private static boolean run = false;
    private List<PeerNode> peers;
    private ServerSocket serverSocket;
    
    public PeerServer() {
        peers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException ex) {
            Logger.getLogger(PeerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()   {
        Socket conn = null;
        while (run) {
            try {
                conn = serverSocket.accept();
                PeerNode n = new PeerNode(conn);
                System.out.println("peer socket " + n.getSocket());
                peers.add(n);
                System.out.println("number of peers " + peers.size());
            } catch (IOException ex) {
                Logger.getLogger(PeerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public List<PeerNode> getPeers()   {
        return this.peers;
    }
    
    
    public void stop()  {
        run = false;
    }
}
