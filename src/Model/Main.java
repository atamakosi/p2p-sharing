/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import View.MainUI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class Main {
    
    
    public static void main(String[] args)  {
        try {
            PeerNode node = new PeerNode(InetAddress.getLocalHost());
            MainUI ui = new MainUI();
            ui.setVisible(true);
            PeerDiscovery disco = new PeerDiscovery();
            disco.run();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
