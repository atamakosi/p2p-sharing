package Model;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;

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
        
        /*
	public static void main(String[] args) {
		try {
			RMIFileClient fc = new RMIFileClient("localhost");
			String[] list = fc.searchForList();
			for (String file : list) {
				System.out.println("F: " + file);
			}
			fc.getRemoteFile("joel");
		} catch (RemoteException e) {
			System.err.println("Unable to use regsitry: " + e);
		} catch (NotBoundException e) {
			System.err.println("Name FServer not currently bound: " + e);
		} catch (IOException e) {
			System.err.println("Problem getting file: " + e);
		}
	}*/
}
