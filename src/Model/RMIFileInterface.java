package Model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;
import java.io.File;

public interface RMIFileInterface extends Remote {
	
	public String[] getFileList() throws RemoteException;
	public File getFile(String fileName) throws RemoteException, IOException;
}
