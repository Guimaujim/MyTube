
import java.rmi.*;
import java.io.*;
import java.util.List;

public interface MyTubeInterface extends Remote {

    public byte[] download(String name)
            throws java.rmi.RemoteException;

    public void upload(byte[] file, String title)
            throws java.rmi.RemoteException;

    public String find(String name)
            throws java.rmi.RemoteException;

    public void addCallback(
            CallbackInterface CallbackObject)
            throws java.rmi.RemoteException;

    public void unregisterForCallback(
            CallbackInterface callbackClientObject)
            throws java.rmi.RemoteException;
}
