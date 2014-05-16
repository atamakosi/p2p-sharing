/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcnabba
 */
public class PeerClient implements Runnable {
    
    private static final int PORT = 33001;
    private boolean run = true;
    private MulticastSocket mSocket;
    private final long SLEEP = 5000;
    private static PeerNode peerNode;
    private static final String SERVER_IP = "Server_IP";
    private int outputStream = 15000;
    
    public PeerClient(PeerNode peerNode) {
        try {
            this.mSocket = new MulticastSocket(PORT);
            this.peerNode = peerNode;
        } catch (IOException ex) {
            Logger.getLogger(PeerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()   {
        ObjectOutputStream os = null;
        while (run) {
            try {
                InetAddress address = InetAddress.getByName(SERVER_IP);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(outputStream);
                os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                os.flush();
                os.writeObject((PeerNode) peerNode);
                os.flush();
                byte[] sendBuf = byteStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, 4445);
                int byteCount = packet.getLength();
                try (DatagramSocket dSock = new DatagramSocket()) {
                    dSock.send(packet);
                    os.close();
                } 
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PeerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch ( IOException ex) {
                Logger.getLogger(PeerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public void stop()  {
        this.run = false;
    }
}
