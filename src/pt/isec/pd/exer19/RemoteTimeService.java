package pt.isec.pd.exer19;

import pt.isec.pd.Time.Time;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;

public class RemoteTimeService extends UnicastRemoteObject implements RemoteTimeInterface {
    public RemoteTimeService() throws RemoteException { }

    @Override
    public Time getTime() throws RemoteException {
        Calendar c = Calendar.getInstance();

        int hours = c.get(Calendar.HOUR_OF_DAY);
        int mins = c.get(Calendar.MINUTE);
        int secs = c.get(Calendar.SECOND);

        return new Time(hours, mins, secs);
    }
}
