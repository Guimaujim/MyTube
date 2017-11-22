
import java.rmi.*;
import java.rmi.registry.*;
import java.io.*;

public class MyTubeServer {

    public static void main(String args[]) {
        File directory = new File("Database"); //Directory where the server will save its files
        directory.mkdir();
        String IP = "192.168.1.33";
        System.setProperty("java.rmi.server.hostname", IP); //Set so the clients can connect properly

        //Same coding as the one done in class
        try {
            MyTubeImpl exportedObj = new MyTubeImpl();
            startRegistry(1234);
            String registryURL = "rmi://" + IP + ":" + 1234 + "/mytube";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("MyTube Server ready.");
        } catch (Exception ex) {
            System.out.println("Error!");
        }
    }

    //Same coding as the one done in class
    private static void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        } catch (RemoteException ex) {
            System.out.println(
                    "RMI registry cannot be located at port " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println(
                    "RMI registry created at port " + RMIPortNum);
        }
    }
}
