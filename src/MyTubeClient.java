
import java.rmi.*;
import java.io.*;
import java.util.Scanner;

public class MyTubeClient {

    public static void main(String args[]) {
        MyTubeClient client = new MyTubeClient();
        File directory = new File("ClientMem"); //Directory where clients will save their files
        directory.mkdir();
        String IP = "192.168.1.33";
        String clientIP = "192.168.1.33";
        System.setProperty("java.rmi.server.hostname", clientIP); //Set so callbacks can work properly

        try {
            String registryURL = "rmi://" + IP + ":" + 1234 + "/mytube";
            MyTubeInterface i = (MyTubeInterface) Naming.lookup(registryURL);
            CallbackInterface callbackObj = new CallbackImpl();
            i.addCallback(callbackObj); //Client adds to callback list

            String input;
            Scanner reader = new Scanner(System.in);

            while (true) {
                System.out.println("What do you want to do?");
                System.out.println("d = download | u = upload | f = find | e = exit");
                input = reader.nextLine();
                //Client waits for user's to decide what to do

                if ("d".equals(input)) {
                    clientDownload(i);
                    System.out.println("Download completed!");
                } else if ("u".equals(input)) {
                    clientUpload(i);
                    System.out.println("Upload completed!");
                } else if ("f".equals(input)) {
                    clientFind(i);
                } else if ("e".equals(input)) {
                    i.unregisterForCallback(callbackObj); //Clients is removed from callback list
                    System.exit(0);
                } else {
                    System.out.println("Sorry, I couldn't understand that, please try again");
                }
            }
        } catch (Exception e) {
            System.out.println("Error! " + e);
        }
    }

    //Downloads file from server to client
    public static void clientDownload(MyTubeInterface i) throws RemoteException {
        String name;
        Scanner reader = new Scanner(System.in);

        System.out.println("Please insert the name of the file you want to download:");
        name = reader.nextLine();
        String path = "ClientMem/" + name;
        byte[] file = i.download(name); //Client calls server to execute implementation's method to download the file

        if (file.length == 0) {
            System.out.println("There isn't any file named like that");
        } else {

            try {
                FileOutputStream FOS = new FileOutputStream(path);
                BufferedOutputStream Output = new BufferedOutputStream(FOS);
                Output.write(file, 0, file.length);
                Output.flush();
                Output.close();
                //Client flushes the byte array received into a file
            } catch (IOException e) {
                System.out.println("Error!" + e.getMessage());
            }
        }
    }

    //Auxiliary method so the client can find the file to upload
    public static String search(File[] Files, String path, String name) {
        String found = null;
        for (File e : Files) {
            if (e.isDirectory()) {
                File folder = new File(path + "/" + e.getName());
                found = search(folder.listFiles(), path + "/" + folder.getName(), name);
                if (found != null) {
                    return found;
                }
            } else if (e.isFile()) {
                if (e.getName().equalsIgnoreCase(name)) {
                    return path + "/" + name;
                }
            }
        }
        return found;
    }

    //Uploads file from client to server
    public static void clientUpload(MyTubeInterface i) throws RemoteException {
        String name;
        Scanner reader = new Scanner(System.in);

        System.out.println("Please insert the name of the file you want to upload:");
        name = reader.nextLine();

        File folder = new File("ClientMem");
        String path = "ClientMem";
        File[] directory = folder.listFiles();
        File userFile;
        path = search(directory, path, name);
        //Client searches for the file

        if (path != null) {
            userFile = new File(path);
            byte buffer[] = new byte[(int) userFile.length()]; //Client converts file into an array of bytes to be sent

            try {
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(path));
                input.read(buffer, 0, buffer.length);
                input.close();
                i.upload(buffer, name); //Client calls server to execute implementation's method to upload the file
            } catch (IOException e) {
                System.out.println("Error!");
            }
        } else {
            System.out.println("There isn't any file named like that");
        }
    }

    //Searches files on server that have relation with the name provided
    public static void clientFind(MyTubeInterface i) throws RemoteException {
        String name;
        Scanner reader = new Scanner(System.in);

        System.out.println("Please insert the name of the file you want to find:");
        name = reader.nextLine();
        String results = i.find(name); //Client calls server to execute implementation's method to find the file

        System.out.println(results);
    }
}
