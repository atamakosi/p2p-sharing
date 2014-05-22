package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class RMIFileClient {
	
	private RMIFileInterface fi;

	public RMIFileClient(String ip) throws RemoteException, NotBoundException {
		Registry reg = LocateRegistry.getRegistry(ip);
		fi = (RMIFileInterface) reg.lookup("FServer");
	}

	public String[] searchForList() throws RemoteException {
		return fi.getFileList();
	}

	public void getRemoteFile(String name) throws IOException {
		File inFile = fi.getFile(name);
		String newFile = System.getProperty("user.dir");
		newFile += System.getProperty("file.separator");
		newFile += "files" + System.getProperty("file.separator");
		newFile += inFile.getName();
		File outFile = new File(newFile);
		InputStream inStream = new FileInputStream(inFile);
		OutputStream outStream = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int bytRead;
		while ((bytRead = inStream.read(buf)) > 0) {
			outStream.write(buf, 0, bytRead);
		}
		inStream.close();
		outStream.close();

	}
}
