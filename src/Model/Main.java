/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import View.MainUI;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Adam
 */
public class Main {
    
    private static final int PORT = 33000;
    
    public static void main(String[] args)  {
        try {
            PeerNode p = new PeerNode(new Socket(InetAddress.getLocalHost(), PORT));
            MainUI ui = new MainUI(p);
            ui.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
