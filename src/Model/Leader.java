/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import com.google.common.primitives.Longs;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for running a modified Chang-Roberts algorithm for leader election in a
 * peer to peer network.  
 * @author Adam
 */
public class Leader implements Runnable {
    
    private final int SLEEP = 1000;
    private final String GROUP = "224.0.0.2";
    private InetAddress group;
    private final int DEST_PORT = 33001;
    //when election started flag set to true
    private boolean participant = false;
    //unique numerical identifier, used to determine leader.  greatest UID = leader
    private long ownID;
    private MulticastSocket serverSocket;
    private InetAddress leader;
    private PeerNode node;
    private boolean run = true;
    
    public Leader(PeerNode node) {
        this.ownID = Runtime.getRuntime().freeMemory();
        System.out.println("Free Memory = " + ownID);
        this.node = node;
        try {
            serverSocket = new MulticastSocket(DEST_PORT);
            group = InetAddress.getByName(GROUP);
            serverSocket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void receiveMessage()   {
       try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(packet);
            buffer = packet.getData();
            long id = Longs.fromByteArray(buffer);
            System.out.println("received long = " + id);
            if (id <= ownID ) {
                startElection();
            }   else    {
                System.out.println("Leader is " + packet.getAddress());
                node.setLeader(packet.getAddress());
            }   
            
        } catch (IOException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startElection() {
        System.out.println("Starting election algorithm.");
        participant = true;
        byte[] msg = Longs.toByteArray(ownID);
        sendPacket(msg);
    }

    /**
     * send broadcast messages
     * @param msg 
     */
    private void sendPacket(byte[] buffer)   {
        try {

            group = InetAddress.getByName(GROUP);
            DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, group, DEST_PORT);
            System.out.println("Sending Leader packet");
            DatagramSocket dSocket = new DatagramSocket();
            dSocket.send(dPacket);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    @Override
    public void run() {
        if (this.leader == null) {
            startElection();
        }
        while (run) {
            receiveMessage();
        }
    }
    
}
