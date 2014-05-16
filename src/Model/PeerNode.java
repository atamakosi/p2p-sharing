/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author mcnabba
 */
public class PeerNode implements Serializable {
    
    private Socket socket;
    
    public PeerNode(Socket s)   {
        this.socket = s;
    }
    
    public Socket getSocket()   {
        return this.socket;
    }
    
}
