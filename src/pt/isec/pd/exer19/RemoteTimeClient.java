package pt.isec.pd.exer19;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RemoteTimeClient {
    public static void main(String[] args) throws RemoteException {
        String registry;
        if (args.length != 1) {
            registry = "localhost";
        } else {
            registry = args[0];
        }

        try {
            String registration = "rmi://" + registry + "/RemoteTimeInterface";
            Remote remoteService = Naming.lookup(registration);

            RemoteTimeInterface timeService = (RemoteTimeService) remoteService;

            System.out.println(timeService.getTime());

        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            System.out.println("Client exception: " + e.getMessage());
            System.exit(1);
        }
    }
}
