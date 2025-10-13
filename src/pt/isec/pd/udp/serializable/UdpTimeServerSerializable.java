package pt.isec.pd.udp.serializable;

import java.io.*;
import java.net.*;

import java.util.Calendar;

public class UdpTimeServerSerializable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int MAX_SIZE = 10000;
    public static final String TIME_REQUEST = "TIME\n";

    public static void main(String[] args) {
        int serverPort = 6001; // Porto do servidor

        if (args.length != 1) {
            System.out.println("Sintaxe: java UdpTimeServerSerializable serverAddress");
            return;
        }

        while (true) {
            try (DatagramSocket socket = new DatagramSocket(serverPort, InetAddress.getByName(args[0]))) {
                System.out.println("Servidor UDP a aguardar pedidos no porto " + serverPort + "...");


                DatagramPacket packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                String request;
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()))) {
                    request = (String) in.readObject();
                }

                if (request.equals(TIME_REQUEST)) {
                    Calendar now = Calendar.getInstance();

                    try (
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bout)
                    ) {
                        out.writeObject(now);
                        out.flush();

                        packet = new DatagramPacket(bout.toByteArray(), bout.size(), packet.getAddress(), packet.getPort());
                    }
                    socket.send(packet);
                }

            } catch (BindException e) {
                System.out.println("Endereco/porto ja em uso. A tentar novamente noutro porto...");
                serverPort++;
            } catch (IOException e) {
                System.out.println("Erro de E/S:\n\t" + e);
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("Erro no casting do objecto recebido:\n\t" + e);
                return;
            }
        }

    }
}
