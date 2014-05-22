package Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIFileInterface extends Remote {
	
	public String[] getFileList() throws RemoteException;
	public byte[] getFile(String fileName) throws RemoteException, IOException;
        public long getTime() throws RemoteException;
        public void setTimeDifference(long time) throws RemoteException;}
