/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author mcnabba
 */
public interface RMIInterface extends Remote {
    
    /**
     *
     * @param f
     * @throws RemoteException
     */
    public void getFile(String fileName) throws RemoteException;
    
    public List<File> getAvailableFiles() throws RemoteException;
    
    
}
