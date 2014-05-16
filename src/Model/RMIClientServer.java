/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcnabba
 */
public class RMIClientServer extends UnicastRemoteObject implements RMIInterface {
    
    private static String url = "localhost";
    private static final int REGISTRY_PORT = 1099;
    private static Registry remoteRegistry;
    private static Registry localRegistry;
    
    public RMIClientServer(String url) throws RemoteException {
        super();
        this.url = url;
    }

    private RMIClientServer() throws RemoteException {
        super();
    }
    
    public void startLocalServer() {
        try {
            RMIInterface stub = new RMIClientServer();
            remoteRegistry = LocateRegistry.getRegistry();
            remoteRegistry.bind(RMIInterface.class.getSimpleName(), stub);
        } catch (RemoteException | AlreadyBoundException ex) {
            Logger.getLogger(RMIClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void connectToRemote()   {
        try {
            localRegistry = LocateRegistry.getRegistry(url);
            RMIInterface server = (RMIClientServer) localRegistry.lookup(RMIInterface.class.getSimpleName());
            printRegistry();
        } catch (NotBoundException | RemoteException ex) {
            Logger.getLogger(RMIClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printRegistry() {
        try {
            System.out.println("stubs in registry");
            String[] bindings = Naming.list(url);
            for (String str : bindings )    {
                System.out.println(str);
            }
        } catch (RemoteException | MalformedURLException ex) {
            Logger.getLogger(RMIClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public ServerSocket createServerSocket()  {
//        RMIServerSocketFactory rf = new RMIServerSocketFactory();
//        
//    }
//    
//    public Socket createSocket(String str, int param)    {
//        RMIClientSocketFactory rs = RMIClientSocketFactory();
//    }

    @Override
    public void getFile(String f) throws RemoteException {
        try {
            RMIInterface stub = (RMIInterface) localRegistry.lookup(RMIInterface.class.getSimpleName());
            stub.getFile(f);    
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(RMIClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> getAvailableFiles() throws RemoteException {
            List<String> fileNames = new ArrayList<>();
            return fileNames;
    }
}