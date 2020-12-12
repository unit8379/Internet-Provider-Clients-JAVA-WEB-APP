package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InternetProvider extends Remote {
    String getMessage() throws RemoteException;

    void insertNewAccountToDatabase(String firstName, String secondName, int tariffId) throws RemoteException;

    void deleteAccountFromDatabase(int accountId) throws RemoteException;

    byte[] getXmlFileOfAccounts() throws RemoteException;
}
