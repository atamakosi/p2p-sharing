/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class PeerDiscovery implements Runnable {
    
    private final int GROUP = 8888;
    private int peerData;
    private DatagramSocket bcastSocket = null;
    private InetSocketAddress bcastAddress = null;
    private InetAddress responseDestination = null;
    private List<PeerNode> peers = null;
    private final int PORT = 33000;
    private boolean run = true;
    private static final byte QUERY = (byte) 33001;
    private static final byte REPLY = (byte) 33000;
    
    public PeerDiscovery()   {
        try {
            bcastSocket = new DatagramSocket(PORT);
            bcastAddress = new InetSocketAddress("255.255.255.255", PORT);
            peers = new ArrayList<>();
        } catch (SocketException ex) {
            Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        byte[] b = new byte[5];
        DatagramPacket dataIn = new DatagramPacket(b, b.length);
        System.out.println("Starting Peer Discovery...");
        while (run) {
            b[0] = 0;
            try {
                bcastSocket.receive(dataIn);
                System.out.println("Data In");
            } catch (IOException ex) {
                Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
            }
            int recvData = decode(b, 1);
            if ( b[0] == QUERY && recvData == GROUP)    {
                byte[] reply = new byte[5];
                reply[0] = REPLY;
                encode(peerData, reply, 1);
                DatagramPacket dataOut = new DatagramPacket(reply, reply.length);
                responseDestination = dataIn.getAddress();
                try {
                    bcastSocket.send(dataOut);
                    System.out.println("Data Out");
                } catch (IOException ex) {
                    Logger.getLogger(PeerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   else if (b[0] == REPLY) {
                if (peers != null && dataIn.getAddress().equals(responseDestination))   {
                    PeerNode tmp = new PeerNode(dataIn.getAddress(), recvData);
                    
                    if (!peers.contains(tmp))   {
                        synchronized (peers) {
                            peers.add(tmp);
                            System.out.println("peerNode added " + tmp.toString());
                        }
                    }
                }
            }
        }
        bcastSocket.disconnect();
        bcastSocket.close();
    }
    
//    public List<PeerNode> getPeerNodes(int timeout, byte peerType)    {
//        peers = new ArrayList<PeerNode>();
//        byte[] b = new byte[5];
//        b[0] = QUERY;
//        encode(group, b, 1);
//        
//        DatagramPacket dataOut = new DatagramPacket(b, b.length, bcastAddress);
//        bcastSocket.send(dataOut);
//        
//        try {
//            Thread.sleep(timeout);
//        }
//        
//    }
    private static int decode( byte[] b, int index )
    {
      int i = 0;

      i |= b[ index ] << 24;
      i |= b[ index + 1 ] << 16;
      i |= b[ index + 2 ] << 8;
      i |= b[ index + 3 ];

      return i;
    }

    private static void encode( int i, byte[] b, int index )
    {
      b[ index ] = ( byte ) ( i >> 24 & 0xff );
      b[ index + 1 ] = ( byte ) ( i >> 16 & 0xff );
      b[ index + 2 ] = ( byte ) ( i >> 8 & 0xff );
      b[ index + 3 ] = ( byte ) ( i & 0xff );
    }
    
    public void connect()   {
        System.out.println("Connecting...");
        run = true;
        run();
    }
  
    public void disconnect()    {
        System.out.println("Disconnecting..");
        run = false;
        bcastSocket.disconnect();
        bcastSocket.close();
    }
}
