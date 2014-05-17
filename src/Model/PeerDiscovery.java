/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class PeerDiscovery implements Runnable {

    private List<PeerNode> peers;
    private boolean run = true;
    private final int PORT = 33000;
    private final String GROUP = "224.0.0.2";
    
    public PeerDiscovery(List<PeerNode> peers)  {
        this.peers = peers;
    }
    
    @Override
    public void run() {
        try {
            MulticastSocket serverSocket = new MulticastSocket(PORT);
            System.out.println("Listening on port " + serverSocket.getLocalPort());
            InetAddress group = InetAddress.getByName(GROUP);
            serverSocket.joinGroup(group);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);
                System.out.println("Receiving...");
//                PeerNode p = new PeerNode(connection);
//                if (!peers.contains(p)) {
//                    synchronized (peers)    {
//                        peers.add(p);
//                    }
//                }
                String str = new String(packet.getData());
                System.out.println("Received = " + str);
            
            serverSocket.leaveGroup(group);
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void stopRun()  {
        run = false;
    }
}
