package Model;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIFileServer implements RMIFileInterface {

	private String fileDirectory;

	public RMIFileServer(String fileDir) throws RemoteException {
		this.fileDirectory = fileDir;
		RMIFileInterface stub = (RMIFileInterface) 
			UnicastRemoteObject.exportObject(this,1099);
                Registry reg = LocateRegistry.createRegistry(1099);
		reg.rebind("FServer", stub);
		System.out.println("Names bound in the registry");
	}

	@Override
	public String[] getFileList() {
		File dir = new File(this.fileDirectory);
		String[] fList = dir.list();
		return fList;
	}

        @Override
	public File getFile(String fileName) {
		File f = null;
		String file = this.fileDirectory + System.getProperty("file.separator")
			+ fileName;
		File temp = new File(file);
		if (temp.exists() && !temp.isDirectory()) {
			f = temp;
		}
		return f;
	}
}

