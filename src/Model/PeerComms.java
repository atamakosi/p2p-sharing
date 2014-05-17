/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class PeerComms implements Runnable {

    private final String GROUP = "224.0.0.2";
    private final int DEST_PORT = 33000;
    private final int SLEEP = 10000;
    private boolean run = true;
    
    public PeerComms()  {
    
    }
    
    @Override
    public void run() {
        try {
            while (run) {
                //broadcast empty data packet every 10 seconds
                //sleep 10 seconds
                DatagramSocket dSocket = new DatagramSocket();
                byte[] buffer = new byte[256];
                buffer = InetAddress.getLocalHost().toString().getBytes();
                InetAddress group = InetAddress.getByName(GROUP);
                DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, group, DEST_PORT);
                System.out.println("Sending address to peers...");
                dSocket.send(dPacket);
                dSocket.close();
                try {
                    Thread.sleep(SLEEP);
                    System.out.println("Sleeping 10s...");
                } catch (InterruptedException ex) {
                    Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            } catch (SocketException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PeerComms.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void sendFile(String fileName)  {

//            PrintWriter out = new PrintWriter( socket.getOutputStream(), true );
//            System.out.println("Sending file " + fileName);
//            out.println(fileName);
//            out.close();
     
    }
    
    public void stopRun()  {
        run = false;
    }
}
