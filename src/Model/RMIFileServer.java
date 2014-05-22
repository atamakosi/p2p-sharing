package Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
	public byte[] getFile(String fileName) {
		String file = this.fileDirectory + System.getProperty("file.separator")
			+ fileName;
		File temp = new File(file);
		BufferedInputStream f = null;
                byte[] buffer = new byte[(int) temp.length()];
                System.out.println("Server: file is " + temp.getAbsolutePath());
		if (temp.exists() && !temp.isDirectory()) {
                    try {
			f = new BufferedInputStream(new FileInputStream(temp));
                        f.read(buffer, 0, buffer.length);
                    } catch (Exception e) {
                        System.out.println("Problem creating input stream");
                        e.printStackTrace();
                    }
		}
		return buffer;
	}
}

