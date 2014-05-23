/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import View.MainUI;
/**
 *
 * @author Adam
 */
public class Main {
    
    private static final int PORT = 33000;
        
    public static void main(String[] args)  {        
        PeerNode p = new PeerNode();       
        MainUI ui = new MainUI();
        ui.setVisible(true);
        ui.setNode(p);
        p.start();
    }
}
