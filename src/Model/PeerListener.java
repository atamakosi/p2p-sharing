/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

/**
 *
 * @author Adam
 */
public interface PeerListener {
     
    public void register(Observer ob);
    
    public void unregister(Observer ob);
    
    public void notifyListeners();
    
}
