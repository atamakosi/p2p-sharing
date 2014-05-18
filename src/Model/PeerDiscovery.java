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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class PeerDiscovery extends Thread {

    private PeerNode localPeerNode;
    private boolean run = true;
    private final int PORT = 33000;
    private final String GROUP = "224.0.0.2";
    private MulticastSocket serverSocket;
    private InetAddress group;
    private PeerNode p;
    
    public PeerDiscovery(PeerNode localPeerNode)  {
        this.localPeerNode = localPeerNode;
        try {
            serverSocket = new MulticastSocket(PORT);
            System.out.println("Listening on port " + serverSocket.getLocalPort());
            group = InetAddress.getByName(GROUP);
            serverSocket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("begin receive...");
            while (run) {
                serverSocket.receive(packet);
                System.out.println("Receiving...");
                System.out.println("received " + packet.getAddress());
                p = new PeerNode(packet.getAddress());
                localPeerNode.addPeerNode(p);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void stopRun()  {
        run = false;
        try {
            serverSocket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverSocket.close();
    }
}
