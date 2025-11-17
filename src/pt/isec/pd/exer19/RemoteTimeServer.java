package pt.isec.pd.exer19;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteTimeServer {
    public static void main(String[] args) {
        String registry;

        if (args.length != 1) {
            registry = "localhost";
        } else {
            registry = args[0];
        }

        try {
            try {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("RMI registry created.");
            } catch (RemoteException e) {
                System.out.println("RMI registry already exists.");
            }

            RemoteTimeService timeService = new RemoteTimeService();
            System.out.println("Remote Time Service is running...");

            String registration = "rmi//" + registry + "/RemoteTimeInterface";

            Naming.bind(registration, timeService);

            System.out.println("Remote Time Service bound in RMI registry.");
            System.out.println("Local Time: " + timeService.getTime());

        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            System.out.println("Server exception: ");
            System.exit(1);
        }
    }
}
