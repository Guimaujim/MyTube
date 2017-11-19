import java.rmi.*;

public interface CallbackInterface extends Remote{

    public String callMe(String message) throws
            java.rmi.RemoteException;	
}
