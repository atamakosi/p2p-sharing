/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import com.google.common.primitives.Longs;
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
    private volatile boolean run = true;
    private final int PORT = 33000;
    private final String GROUP = "224.0.0.2";
    private MulticastSocket serverSocket;
    private InetAddress group;
    private PeerNode p;
    
    public PeerDiscovery(PeerNode localPeerNode)  {
        this.localPeerNode = localPeerNode;
        try {
            serverSocket = new MulticastSocket(PORT);
            group = InetAddress.getByName(GROUP);
            serverSocket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        listen();
        disconnect();
    }
    
    private void connect()  {
        if (serverSocket == null || serverSocket.isClosed())    {
            try {
                System.out.println("rebind server socket!");
                serverSocket = new MulticastSocket(PORT);
                serverSocket.joinGroup(group);
            } catch (IOException ex) {
                Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void listen()   {
        byte[] buffer;
        boolean exists = false;
        while (run) {
            connect();
            try {
                buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (!serverSocket.isClosed() )    {
                    serverSocket.receive(packet);
                    p = new PeerNode(packet.getAddress().getHostAddress());
                    exists = localPeerNode.addPeerNode(p);
                    if ( exists && (Longs.fromByteArray(packet.getData()) == 999999))   {
                        System.out.println("Received P2P removal request");
                        localPeerNode.removePeerNode(p);
                    }
                    buffer = null;
                }
            } catch (IOException ex) {
                Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
    }
    
    public void requestStop()  {
        run = false;
        System.out.println("Peer Discover stop = " + run);
    }
    
    public void disconnect()    {
        try {
            serverSocket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Peer Discovery closed");
        serverSocket.close();
    }
}
