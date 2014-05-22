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
public class Leader extends Thread {
    
    //class D network group to join
    private final String GROUP = "224.0.0.2";
    //variable to hold the network group
    private InetAddress group;
    //port to rx/tx data packets regarding election on 
    private final int DEST_PORT = 33001;
    //when election started flag set to true
    private boolean participant = false;
    //unique numerical identifier, used to determine leader.  greatest UID = leader
    private long ownID;
    private MulticastSocket serverSocket;
    //reference to the peer who is leader.  can be set to localhost.
    private InetAddress leader;
    //reference to local client to pass leader data when a new leader is found.
    private static PeerNode localPeerNode;
    
    private boolean run = true;
    
    /**
     * 
     * @param node 
     */
    public Leader(PeerNode localPeerNode) {
        this.ownID = Runtime.getRuntime().freeMemory();
        this.localPeerNode = localPeerNode;
        try {
            serverSocket = new MulticastSocket(DEST_PORT);
            group = InetAddress.getByName(GROUP);
            serverSocket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Listens for messages from group requesting an election.  The packets being
     * received will contain the amount of free memory in the sender.  The local 
     * client will compare against its own memory at startup and determine if it
     * should rebroadcast using <code> startElection</code>
     * claiming it has more free memory or accept the new leader
     * because they have more available resources.  
     * 
     * This may result in a flood of broadcasts temporarily as the leader is 
     * sorted in theory.  E.g. if this is the lowest id peer, then all other 
     * peers might respond and end up with a Big O(n2) transmissions.  
     * 
     * Each peer with greater id will respond, until only one peer responds and 
     * other peers set it as the leader.
     */
    public void receiveMessage()   {
        byte[] buffer;
        long id;
        while (run)  {
            try {
                buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (serverSocket != null)    {
                    serverSocket.receive(packet);
                    buffer = packet.getData();
                    id = Longs.fromByteArray(buffer);
                    if (id < ownID ) {
                        startElection();
                    }   else    {
                        localPeerNode.setLeader(packet.getAddress());
                        long timestamp = Longs.fromBytes(buffer[9], buffer[10], buffer[11],
                                buffer[12], buffer[13], buffer[14], buffer[15], buffer[16]);
                        System.out.println("received update for leader " + timestamp);
                        localPeerNode.updateVectorForPeer(packet.getAddress().getHostAddress(), timestamp);
                    }   
                }
                buffer = null;
            } catch (IOException ex) {
                Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        try {
            serverSocket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverSocket.close();
    }
    
    /**
     * Called when this thread receives an id less than its own local id value. 
     * transmits its id to other peers in group.
     */
    public void startElection() {
        participant = true;
        byte[] msg = new byte[256];
        byte[] longByte = Longs.toByteArray(ownID);
        for (int i =0; i < longByte.length; i++)    {
            msg[i] = longByte[i];
        }
        System.out.println("free memory msg length = " + msg.length);
        byte[] timestamp = Longs.toByteArray(localPeerNode.getVectorTimeStamp());
        for (int i = 0; i < timestamp.length; i++)  {
            msg[i + 9] = timestamp[i];
        }
        localPeerNode.setVectorTimeStamp();
        sendPacket(msg);
    }

    /**
     * send broadcast messages
     * @param msg 
     */
    private void sendPacket(byte[] buffer)   {
        try {
            DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, group, DEST_PORT);
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
    
    /**
     * stops thread actions
     */
    public void requestStop()  {
        run = false;
    }
}
