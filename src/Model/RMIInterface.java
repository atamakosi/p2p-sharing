/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.File;
import java.rmi.RemoteException;

/**
 *
 * @author mcnabba
 */
public interface RMIInterface {
    
    public File getFile() throws RemoteException;
}
