
import java.rmi.*;
import java.io.*;
import java.util.Scanner;

public class MyTubeClient {

    public static void main(String args[]) {
        MyTubeClient client = new MyTubeClient();
        File directory = new File("ClientMem");
        directory.mkdir();
        String IP = "192.168.1.33";

        try {
            String registryURL = "rmi://" + IP + ":" + 1234 + "/mytube";
            MyTubeInterface i = (MyTubeInterface) Naming.lookup(registryURL);
            CallbackInterface callbackObj = new CallbackImpl();
            i.addCallback(callbackObj);

            String input;
            Scanner reader = new Scanner(System.in);

            while (true) {
                System.out.println("What do you want to do?");
                System.out.println("d = download | u = upload | f = find | e = exit");
                input = reader.nextLine();

                if ("d".equals(input)) {
                    clientDownload(i);
                } else if ("u".equals(input)) {
                    clientUpload(i);
                } else if ("f".equals(input)) {
                    clientFind(i);
                } else if ("e".equals(input)) {
                    i.unregisterForCallback(callbackObj);
                    System.exit(0);
                } else {
                    System.out.println("Sorry, I couldn't understand that, please try again");
                }
            }
        } catch (Exception e) {
            System.out.println("Error! " + e);
        }
    }
//done

    public static void clientDownload(MyTubeInterface i) throws RemoteException {
        String name;
        Scanner reader = new Scanner(System.in);

        System.out.println("Please insert the name of the file you want to download:");
        name = reader.nextLine();
        String path = "ClientMem/" + name;
        byte[] file = i.download(name);

        if (file.length == 0) {
            System.out.println("There isn't any file named like that");
        } else {

            try {
                FileOutputStream FOS = new FileOutputStream(path);
                BufferedOutputStream Output = new BufferedOutputStream(FOS);
                Output.write(file, 0, file.length);
                Output.flush();
                Output.close();
            } catch (IOException e) {
                System.out.println("Error!" + e.getMessage());
            }
        }
    }
//done

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

        if (path != null) {
            userFile = new File(path);
            byte buffer[] = new byte[(int) userFile.length()];

            try {
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(path));
                input.read(buffer, 0, buffer.length);
                input.close();
                i.upload(buffer, name);
            } catch (IOException e) {
                System.out.println("Error!");
            }
        } else {
            System.out.println("There isn't any file named like that");
        }
    }

    public static void clientFind(MyTubeInterface i) throws RemoteException {
        String name;
        Scanner reader = new Scanner(System.in);

        System.out.println("Please insert the name of the file you want to find:");
        name = reader.nextLine();
        String results = i.find(name);

        System.out.println(results);
    }
}
