
import java.rmi.*;
import java.rmi.registry.*;
import java.io.*;
//done
public class MyTubeServer {

    public static void main(String args[]) {
        File directory = new File("Database");
        directory.mkdir();
        try {
            MyTubeImpl exportedObj = new MyTubeImpl();
            startRegistry(1234);
            String registryURL = "rmi://localhost:" + 1234 + "/mytube";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("MyTube Server ready.");
        } catch (Exception ex) {
            System.out.println("Error!");
        }
    }

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
