package Model;

import java.io.BufferedOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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
		byte[] in = fi.getFile(name);
		String newFile = System.getProperty("user.home");
		newFile += System.getProperty("file.separator");
		newFile += "files" + System.getProperty("file.separator");
		newFile += name;
		File outFile = new File(newFile);
                System.out.println("Out file is " + outFile.getAbsolutePath());
                if (!outFile.exists()) {
                    outFile.createNewFile();
                    System.out.println("File created");
                }
		BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(outFile));
                out.write(in, 0, in.length);
		out.close();
	}
        
        public long getServerTime() {
            long time = 0;
            try {
                time =  fi.getTime();
            } catch (RemoteException ex) {
                System.out.println("Error: rmiclient remote exception");
                ex.printStackTrace();
            }
            return time;
        }
        
        public void setServerDifference(long dif) {
            try {
                fi.setTimeDifference(dif);
            } catch (RemoteException ex) {
                System.out.println("Error: rmiclient remote exception");
                ex.printStackTrace();
            }
        }
}
