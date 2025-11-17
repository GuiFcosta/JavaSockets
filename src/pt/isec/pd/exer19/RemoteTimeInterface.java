package pt.isec.pd.exer19;

import pt.isec.pd.Time.Time;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteTimeInterface extends Remote {
    public Time getTime() throws RemoteException;
}
