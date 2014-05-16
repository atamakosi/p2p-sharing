/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    
    private static boolean run = true;
    private List<PeerNode> peers;
    private ServerSocket serverSocket;
    private final static int PORT = 33000;
    
    public PeerServer() {
        peers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            Logger.getLogger(PeerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()   {
        Socket conn = null;
        while (run) {
            try {
                byte[] recvBuf = new byte[5000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                DatagramSocket dSock = new DatagramSocket(PORT);
                dSock.receive(packet);
                int byteCount = packet.getLength();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                PeerNode n = null;
                try {
                    n = (PeerNode)is.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PeerServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                is.close();
                dSock.close();
//                conn = serverSocket.accept();
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
