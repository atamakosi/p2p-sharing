/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcnabba
 */
public class RMIClient implements RMIInterface {
    
    public static final String REGISTRY_URL = "localhost";
    
    public RMIClient()  {
        
    }
    
    public void start() {
        try {
        RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.getRegistry(REGISTRY_URL);
        registry.rebind("stub", stub);
        System.out.println("stubs in registry");
        try {
            String[] bindings = Naming.list(REGISTRY_URL);
            for (String str : bindings )    {
                System.out.println(str);
            }
        }   catch (MalformedURLException e)     {
            System.out.println("error retrieving names " + e);
        }
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public File getFile() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}