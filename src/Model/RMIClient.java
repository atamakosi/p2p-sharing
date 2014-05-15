/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcnabba
 */
public class RMIClient {
    
    public static final String REGISTRY_URL = "localhost";
    
    public RMIClient()  {
        
    }
    
    public Registry getRegistry()   {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(REGISTRY_URL);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return registry;
    }
}
