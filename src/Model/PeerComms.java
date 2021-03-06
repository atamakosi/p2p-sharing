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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class PeerComms extends Thread {

    private final String GROUP = "224.0.0.2";
    private final int DEST_PORT = 33000;
    private final int SLEEP = 10000;
    private volatile boolean run = true;
    private InetAddress group;
    private DatagramSocket dSocket;
    private static PeerNode localPeerNode;
    
    public PeerComms(PeerNode localPeerNode)  {
        this.localPeerNode = localPeerNode;
        try {
            group = InetAddress.getByName(GROUP);
            dSocket = new DatagramSocket();
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connect()   {
        if (dSocket == null || dSocket.isClosed() ) {
            try {
                dSocket = new DatagramSocket();
            } catch (SocketException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void run()   {
        broadcast();
        disconnect();
    }
    /**
     * Due to the need to pass timestamps, disconnect notifications, etc. packet format
     * is set as follows:
     * bytes 0 - 7 reserved for disconnect notification as long 99999
     * byte 8 reserved for snapshot notification
     * bytes 9 - 16 reserved for vector timestamp long
     */
    public void broadcast() {
        byte[] buffer = new byte[256];
        DatagramPacket dPacket;
        byte[] timeStamp = new byte[8];
        //for loop to merge byte arrays
        for (int i = 0; i < timeStamp.length; i++)    {
            buffer[9 + i] = timeStamp[i];
        }
        while (run) {
            connect();
            try {
                //broadcast empty data packet every 10 seconds
                dPacket = new DatagramPacket(buffer, buffer.length, group, DEST_PORT);
                //sending an empty packet still allows remote peer to get the IP address of sender
                dSocket.send(dPacket);
                localPeerNode.setVectorTimeStamp();
                System.out.println("datapacket sent");
                try {
                    PeerComms.sleep(SLEEP);
                    System.out.println("Sleeping comms");
                } catch (InterruptedException ex) {
                    Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
                }
                dPacket = null;

            } catch (SocketException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        leavingP2PNetwork();
    }
    
    public void requestStop()  {
        run = false;
    }
    
    public void disconnect()    {
            dSocket.close();
            System.out.println("socket closed");
    }
    
    public void leavingP2PNetwork() {
        byte[] buffer = new byte[256];
        long msg = 999999;
        buffer = Longs.toByteArray(msg);
        DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, group, DEST_PORT);
        try {
            dSocket.send(dPacket);
            localPeerNode.setVectorTimeStamp();
            System.out.println("send leaving notification to p2p network");
            System.out.println("packet sent = " + Longs.fromByteArray(dPacket.getData()));
        } catch (IOException ex) {
            Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
