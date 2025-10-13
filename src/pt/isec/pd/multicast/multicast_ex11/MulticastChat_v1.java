package pt.isec.pd.multicast.multicast_ex11;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class MulticastChat_v1 extends Thread {
    public static final String LIST = "LIST";
    public static final String EXIT = "EXIT";
    public static final int MAX_SIZE = 1000;

    protected String username;
    protected MulticastSocket s = null;
    protected boolean running = false;

    public MulticastChat_v1(String username, MulticastSocket s) {
        this.username = username;
        this.s = s;
        running = true;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        DatagramPacket pkt, response;
        String msg;

        if (s == null || !running)
            return;

        try {
            while (running) {
                /* Aguarda pela receção de um datagram no socket s */
                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);

                msg = new String(pkt.getData(), 0, pkt.getLength());

                if (msg.toUpperCase().contains(LIST.toUpperCase())) {
                    /* Reenvia o username 'a origem do datagrama recebido */
                    response = new DatagramPacket(username.getBytes(), username.length(), pkt.getAddress(), pkt.getPort());

                    s.send(response);
                    //continue;
                }
                System.out.println("\n(" + pkt.getAddress().getHostAddress() + ":" +
                        pkt.getPort() + ") " + msg + "\n");
                System.out.print("> ");
            }
        } catch (IOException e) {
            if (running)
                System.err.println("Erro na thread de receção: " + e.getMessage());
        } finally {
            if (!s.isClosed())
                s.close();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        if (args.length != 4) {
            System.out.println("Sintaxe: java MulticastChat <nickname> <groupo multicast> <porto> <NIC multicast>");
            return;
        }

        InetAddress group = InetAddress.getByName(args[1]);
        int port = Integer.parseInt(args[2]);
        String msg, msgToSend;

        MulticastSocket socket = null;
        DatagramPacket dgram;
        MulticastChat_v1 t = null;
        NetworkInterface nif;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            //showNetworkInterfaces();
            try {
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[3])); //e.g., 127.0.0.1, 192.168.10.1, ...
            } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
                nif = NetworkInterface.getByName(args[3]); //e.g., lo, eth0, wlan0, en0, ...
            }

            socket = new MulticastSocket(port);

            /* Associa o socket ao grupo de multicast/IP de classe D pretendido (args[1]) */
            socket.joinGroup(new InetSocketAddress(group, port), nif);

            /* Inicia a thread que vai ficar continuamente 'a espera de datagrams no socket */
            t = new MulticastChat_v1(args[0], socket);
            t.start();

            System.out.print("> ");

            /* A thread principal entre num ciclo em que vai a aguardar por mensagens em System.in
               e envia-as para o grupo de multicast*/

            while ((msg = in.readLine()) != null) {
                if (msg.equalsIgnoreCase(EXIT))
                    break;
                msgToSend = args[0] + ": " + msg;

                /* Envia a mensagem para o grupo de multicast */
                dgram = new DatagramPacket(msgToSend.getBytes(), msgToSend.length(), group, port);
                socket.send(dgram);
            }

        } finally {
            if (t != null)
                t.terminate();

            if (socket != null)
                socket.close();

            //t.join(); //Para esperar que a thread termine caso esteja em modo daemon
        }
    }
}
