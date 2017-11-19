
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class MyTubeImpl extends UnicastRemoteObject implements MyTubeInterface {

    List<File> f = new ArrayList<>();
    File[] files = new File[50];
    private static Vector callbackObjects;

    public MyTubeImpl() throws RemoteException {
        super();
        callbackObjects = new Vector();
    }

    @Override
    public void addCallback(CallbackInterface CallbackObject) {
        System.out.println("Server got an 'addCallback' call.");
        callbackObjects.addElement(CallbackObject);
    }

    @Override
    public synchronized void unregisterForCallback(
            CallbackInterface callbackClientObject)
            throws java.rmi.RemoteException {
        if (callbackObjects.removeElement(callbackClientObject)) {
            System.out.println("Unregistered client ");
        } else {
            System.out.println("Unregister: client wasn't registered.");
        }
    }

    @Override
    public byte[] download(String name) throws RemoteException {
        File folder = new File("Database");
        String path = "Database";
        File[] directory = folder.listFiles();
        File userFile;
        path = search(directory, path, name);
        userFile = new File(path);
        byte buffer[] = new byte[(int) userFile.length()];

        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(path));
            input.read(buffer, 0, buffer.length);
            input.close();
            return (buffer);
        } catch (IOException e) {
            System.out.println("Error!");
            return new byte[0];
        }
    }

    public String search(File[] Files, String path, String name) {
        String found = null;
        for (File e : Files) {
            if (e.isDirectory()) {
                File folder = new File(path + "/" + e.getName());
                found = search(folder.listFiles(), path + "/" + folder.getName(), name);
                if (found != null) {
                    return found;
                }
            } else {
                if (e.getName().equalsIgnoreCase(name)) {
                    return path + "/" + name;
                }
            }
        }
        return found;
    }

    @Override
    public void upload(byte[] file, String name) {
        String ID = UUID.randomUUID().toString();
        File dir = new File("Database/" + ID);
        dir.mkdir();
        String path = "Database/" + ID + "/" + name;

        try {
            FileOutputStream FOS = new FileOutputStream(path);
            BufferedOutputStream Output = new BufferedOutputStream(FOS);
            Output.write(file, 0, file.length);
            Output.flush();
            Output.close();
            callback();
        } catch (IOException e) {
            System.out.println("Error!" + e.getMessage());
        }

    }

    @Override
    public String find(String name) {
        File folder = new File("Database");
        String path = "Database";
        File[] directory = folder.listFiles();
        String result = "";

        ArrayList<String> StringArray = serverFind(directory, path, name);

        for (int i = 0; i < StringArray.size(); i++) {
            if (i != StringArray.size() - 1) {
                result += StringArray.get(i) + " | ";
            } else {
                result += StringArray.get(i);
            }
        }

        return result;
    }

    public ArrayList<String> serverFind(File[] Files, String path, String name) {
        ArrayList<String> StringArray = new ArrayList<String>();

        for (File e : Files) {
            if (e.isDirectory()) {
                File folder = new File(path + "/" + e.getName());
                ArrayList<String> temp = serverFind(folder.listFiles(), path + "/" + folder.getName(), name);
                for (String s : temp) {
                    StringArray.add(s);
                }
            } else {
                if (e.getName().toLowerCase().contains(name.toLowerCase())) {
                    StringArray.add(e.getName());
                }
            }
        }

        return StringArray;
    }

    private static void callback() {
        for (int i = 0; i < callbackObjects.size(); i++) {
            System.out.println("Now performing the " + i + "th callback\n");
            CallbackInterface client
                    = (CallbackInterface) callbackObjects.elementAt(i);
            try {
                client.callMe("New content has been uploaded!");
            } catch (Exception e) {
                System.out.println("Error! " + e);
            }
        }
    }
}
