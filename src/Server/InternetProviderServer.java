package Server;

import java.rmi.registry.*;
import java.rmi.server.*;

public class InternetProviderServer {
    public static void main(String[] args) {
        System.setProperty("java.security.policy", "src\\Server\\client.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            InternetProviderImpl product = new InternetProviderImpl("Me");
            InternetProvider stub = (InternetProvider)UnicastRemoteObject.exportObject(product, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Binding server implementations to registry...");
            registry.bind("actions", stub);
            System.out.println("Waiting for invocations from clients...");
        }
        catch (Exception e) {
            System.err.println("Server exception: ");
            e.printStackTrace();
        }
    }
}
