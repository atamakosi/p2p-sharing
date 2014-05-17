/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to pass local socket information to peers.  Goal is to allow a peer 
 * to store discovered peers in a data collection.
 * @author mcnabba
 */
public class PeerNode {
       
    private int port;
    private List<PeerNode> peers;
    private boolean run = true;
    
    public PeerNode(Socket s)   {
        this.port = s.getPort();
        peers = new ArrayList<>();
    }
 
    public void sendFileName(String fileName, Socket s)  {
        try (Socket socket = s) 
         {
            PrintWriter out = new PrintWriter( socket.getOutputStream(), true );
            out.println(fileName);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listen() throws IOException    {
        ServerSocket serverSocket = new ServerSocket(0);
        System.out.println("Listening on port " + serverSocket.getLocalPort());
        while (run) {
            Socket connection = serverSocket.accept();
            PeerNode p = new PeerNode(connection);
            if (!peers.contains(p)) {
                synchronized (peers)    {
                    peers.add(p);
                }
            }
        }
    }
    
    
    public void stop()  {
        run = false;
    }
    
    
    @Override
    public String toString()    {
        String str = null;
        try {
            str = InetAddress.getLocalHost().getAddress().toString();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }
}
