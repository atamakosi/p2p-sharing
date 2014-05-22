/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to pass local socket information to peers.  Goal is to allow a peer 
 * to store discovered peers in a data collection.
 * @author mcnabba
 */
public class PeerNode implements PeerListener {
       
    private final int PORT = 33000;
    private Map<String, PeerNode> peers;
    private boolean run = true;
    public String address;
    private Thread commsThread;
    private PeerComms pComms;
    private PeerDiscovery pDisc;
    private List<Observer> observers;
    private InetAddress leader = null;
    private RMIFileServer fileServer;
    private Leader leaderSelection;
    private ArrayList<FileServerList> servers; //contains list of servers and there respective files.

    public PeerNode()   {
        peers = new HashMap<>();
        pDisc = new PeerDiscovery(this);
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        pComms = new PeerComms();
        leaderSelection = new Leader(this);
        observers = new ArrayList<>();
        try {
            fileServer = new RMIFileServer(System.getProperty("user.home"));
            System.out.println("RMI server up.");
        } catch (RemoteException e) {
            System.err.println("Error making the rmi server: " );
            e.printStackTrace();
        }
    }

    /**
     * constructor to store incoming Peer connections.  
     * @param address
     */
    public PeerNode(String address)   {
        this.address = address;
    }

    public void stopSockets()  {
        pComms.requestStop();
        pDisc.requestStop();
        leaderSelection.requestStop();
        this.peers.clear();
        notifyListeners();
    }
    
    public void stopThreads()   {
        try {
            pComms.join();
            pDisc.join();
            leaderSelection.join();
        }   catch (InterruptedException e)  {
            
        }
    }
    
    public Map<String, PeerNode> getPeers()    {
        return peers;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public boolean addPeerNode(PeerNode n) {
        String nodeAddress = n.toString();
        if (!peers.containsKey(nodeAddress))  {
            peers.put(n.getAddress(), n);
            System.out.println("Peer added to Map");
            notifyListeners();
            return true;
        }   else    {
            System.out.println("Peer rejected : " + n.toString());
            return false;
        }
    }
    
    public void removePeerNode(PeerNode n)  {
        peers.remove(n);
        notifyListeners();
    }

    public ArrayList getFileList() {
        Iterator it = peers.values().iterator();
        servers = new ArrayList<>();
        String fcip = "";
        while (it.hasNext()) {
            //System.out.println("New file client: " + it.next().toString());
            
            try {
                fcip = it.next().toString();
                RMIFileClient fc = new RMIFileClient(fcip);
                String[] list = fc.searchForList();
                FileServerList fslist = new FileServerList(fcip);
                fslist.addAll(Arrays.asList(list));
                servers.add(fslist);
            } catch (RemoteException e) {
                System.out.println("Had RemoteException generating client " + fcip);
                e.printStackTrace();;
            } catch (NotBoundException e) {
                System.out.println("Had NotBoundException generating client " + fcip);
                e.printStackTrace();
            }
        }
        return servers;
    }
    
    public void getFileFromServer(String ip, String filename) {
        //needs to be implemented
        //System.out.println("hasn't been implemented yet");
        if (ip.equals(address)) {
            System.out.println("That is already your file stupid.");
            System.out.println("Retreival cancelled!");
        } else {
            try {
                RMIFileClient fc = new RMIFileClient(ip);
                fc.getRemoteFile(filename);
            } catch (Exception e) {
                System.out.println("Trouble getting file from client " + ip);
                e.printStackTrace();
            }
        }
    }
  
    
    @Override
    public String toString()    {
        return address;
    }
    
    /**
     * starts listening for peers, broadcasting to peers, and starts a leader
     * election.  Upon a local PeerNode starting, it should discover the leader
     * in the P2P network, which it might usurp.
     */
    public void start() {
        if (!pComms.isAlive() || 
                !pDisc.isAlive() || 
                    !leaderSelection.isAlive()) {
            pComms = new PeerComms();
            pDisc = new PeerDiscovery(this);
            leaderSelection = new Leader(this);
        }
        pComms.start();
        pDisc.start();
        leaderSelection.start();
    }
    
    public void setLeader(InetAddress leader)   {
        this.leader = leader;
    }
    
    @Override
    public void register(Observer obs) {
        if (!observers.contains(obs))   {
            observers.add(obs);
        }
    }

    @Override
    public void unregister(Observer obs) {
        if (observers.contains(obs))    {
            observers.remove(obs);
        }
    }

    @Override
    public void notifyListeners() {
        for (Observer obs : observers ) {
            obs.update(peers);
        }
    }
}
