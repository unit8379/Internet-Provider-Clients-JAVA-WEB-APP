package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InternetProvider extends Remote {
    String getMessage() throws RemoteException;

    byte[] getXmlFileOfAccounts() throws RemoteException;
}
