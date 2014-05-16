/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface for RMI 
 * @author mcnabba
 */
public interface RMIInterface extends Remote {
    
    /**
     * retrieves specified file from peer
     * @param f
     * @throws RemoteException
     */
    public void getFile(String fileName) throws RemoteException;
    
    /**
     * retrieves available files from peers
     * @return
     * @throws RemoteException 
     */
    public List<String> getAvailableFiles() throws RemoteException;
    
    
}
