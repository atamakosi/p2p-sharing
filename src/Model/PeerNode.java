/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.common.primitives.Longs;
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
 * primary class used to start threads for networking and holding references to
 * other peers.  
 * @author mcnabba
 */
public class PeerNode implements PeerListener {
       
    private final int PORT = 33000;
    private Map<String, PeerNode> peers;
    public String address;
    private Thread commsThread;
    private PeerComms pComms;
    private PeerDiscovery pDisc;
    private List<Observer> observers;
    private InetAddress leader = null;
    private RMIFileServer fileServer;
    private Leader leaderSelection;
    private Map<PeerNode, Long> vectorStamps;
    private long time = 0;
    private ClockSet clock;
    private ArrayList<FileServerList> servers; //contains list of servers and there respective files.

    public PeerNode()   {
        peers = new HashMap<>();
        //initialize the vector collection
        vectorStamps = new HashMap<>();
        //begins this vector at zero
        vectorStamps.put(this, time);
        pDisc = new PeerDiscovery(this);
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PeerNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        pComms = new PeerComms(this);
        leaderSelection = new Leader(this);
        observers = new ArrayList<>();
        clock = new ClockSet(this);
        try {
            fileServer = new RMIFileServer(System.getProperty("user.home"), clock);
            System.out.println("RMI server up.");
        } catch (RemoteException e) {
            System.err.println("Error making the rmi server: " );
        }
        
        
    }

    public synchronized long getVectorTimeStamp()    {
        return this.vectorStamps.get(this);
    }
    
    public synchronized void setVectorTimeStamp()    {
        System.out.println("updating timestamp in node");
        this.vectorStamps.put(this, time++);
    }
    
    public synchronized void updateVectorForPeer(String address, long timestamp) {
        System.out.println("received timestamp at peer node = " + timestamp);
        this.vectorStamps.put(peers.get(address), timestamp);
    }
    
    /**
     * constructor to store incoming Peer connections.  
     * @param address
     */
    public PeerNode(String address)   {
        this.address = address;
    }

    /**
     * stops threads infinite loops
     */
    public void stopSockets()  {
        pComms.requestStop();
        pDisc.requestStop();
        leaderSelection.requestStop();
        this.peers.clear();
        notifyListeners();
    }
    
    /**
     * kills threads if not dead already
     */
    public void stopThreads()   {
        try {
            pComms.join();
            pDisc.join();
            leaderSelection.join();
            clock.join();
        }   catch (InterruptedException e)  {
            
        }
    }
    
    public Map<String, PeerNode> getPeers()    {
        return peers;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public boolean addPeerNode(PeerNode n, byte[] byteArray) {
        String nodeAddress = n.toString();
        long timestamp = Longs.fromBytes(byteArray[9], byteArray[10], byteArray[11], 
                byteArray[12], byteArray[13], byteArray[14], byteArray[15], byteArray[16]);
        System.out.println("Timestamp of received message when adding peer node = " + timestamp);
        System.out.println("local timestamp = " + time);
        if (!peers.containsKey(nodeAddress))  {
            peers.put(n.getAddress(), n);
            vectorStamps.put(peers.get(n.getAddress()), timestamp);
            System.out.println("Peer added to Map");
            notifyListeners();
            return true;
        }   else    {
            vectorStamps.put(peers.get(n.getAddress()), timestamp);
            return false;
        }
    }
    
    public void removePeerNode(PeerNode n)  {
        peers.remove(n.toString());
        notifyListeners();
    }

    public ArrayList getFileList() {
        Iterator it = peers.values().iterator();
        servers = new ArrayList<>();
        String fcip = "";
        while (it.hasNext()) {
            try {
                fcip = it.next().toString();
                RMIFileClient fc = new RMIFileClient(fcip);
                String[] list = fc.searchForList();
                FileServerList fslist = new FileServerList(fcip);
                fslist.addAll(Arrays.asList(list));
                servers.add(fslist);
            } catch (RemoteException e) {
                System.out.println("Had RemoteException generating client " + fcip);
            } catch (NotBoundException e) {
                System.out.println("Had NotBoundException generating client " + fcip);
            }
        }
        return servers;
    }
    
    public InetAddress getLeader() {
        return this.leader;
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
    
    public boolean isLeader() {
        boolean isLeader = false;
        try {
            isLeader = (this.leader == InetAddress.getLocalHost());
        } catch (Exception e) {
            System.out.println("Trying to check leader");
        }
        return isLeader;
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
            pComms = new PeerComms(this);
            pDisc = new PeerDiscovery(this);
            leaderSelection = new Leader(this);
        }
        pComms.start();
        pDisc.start();
        leaderSelection.start();
        clock.start();
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
