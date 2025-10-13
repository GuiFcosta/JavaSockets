package pt.isec.pd.multicast.multicast_ex12;

import java.io.*;
import java.net.*;

public class MulticastChat_v2 extends Thread {
    public static final String LIST = "LIST";
    public static String EXIT = "EXIT";
    public static int MAX_SIZE = 1000;

    protected String username;
    protected MulticastSocket s;
    protected boolean running;

    public MulticastChat_v2(String username, MulticastSocket s) {
        this.username = username;
        this.s = s;
        running = true;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        DatagramPacket pkt;
        Msg msg;
        String strMsg;
        ObjectInputStream in;
        ByteArrayInputStream bin;
        Object obj;

        if (s == null || !running)
            return;

        try {
            while (running) {
                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);

                try {
                    // "Deserialize" o objeto transportado no datagram acabado de ser recebido
                    bin = new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength());
                    in = new ObjectInputStream(bin);
                    obj = in.readObject();

                    System.out.print("\n(" + pkt.getAddress().getHostAddress() + ":" + pkt.getPort() + ") ");

                    //Caso o objeto recebido seja uma instância de Msg...
                    if (obj instanceof Msg) {
                        msg = (Msg) obj;

                        if (msg.getMsg().toUpperCase().contains(LIST)) {
                            //Envia o username 'a origem sob a forma de um objeto serialize do tipo ‘String’

                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bout);

                            out.writeObject(username);
                            out.flush();
                            byte[] data = bout.toByteArray();
                            DatagramPacket response = new DatagramPacket(data, data.length, pkt.getAddress(), pkt.getPort());

                            s.send(response);
                            continue;
                        }

                        //Mostra a mensagem recebida bem como a identificacao do emissor
                        System.out.println("Recebido \"" + msg.getMsg() +"\" de " + msg.getNickname());

                        //Caso o objeto recebido seja uma instância de ‘String’...
                    } else if (obj instanceof String) {
                        strMsg = (String) obj;
                        //Mostra a String
                        System.out.println(strMsg);
                        System.out.print("\n> ");
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado!");
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
                }
            }
        } catch (IOException e) {
            if (running)
                System.err.println("Erro de rede: " + e.getMessage());
        } finally {
            if (!s.isClosed())
                s.close();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        if (args.length != 4) {
            System.out.println("Sintaxe: java MulticastChat <nickname> <groupo multicast> <porto> <interface de rede usada para multicast>");
            return;
        }
        InetAddress group = InetAddress.getByName(args[1]);
        int port = Integer.parseInt(args[2]);
        String msg;

        MulticastSocket socket = null;
        NetworkInterface nif;
        MulticastChat_v2 t = null;

        DatagramPacket dgram;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            try {
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[3])); //e.g., 127.0.0.1, 192.168.10.1, ...
            } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
                nif = NetworkInterface.getByName(args[3]); //e.g., lo0, eth0, wlan0, en0, ...
            }

            socket = new MulticastSocket(port);
            socket.joinGroup(new InetSocketAddress(group, port), nif);

            //Lanca a thread adicional dedicada a aguardar por datagramas no socket e a processá-los
            t = new MulticastChat_v2(args[0], socket);
            t.start();

            System.out.print("> ");

            while ((msg = in.readLine()) != null) {
                if (msg.equalsIgnoreCase(EXIT))
                    break;

                Msg m = new Msg(args[0], msg);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(m);
                oout.flush();

                //Envia para o grupo de multicast e porto escolhidos uma instância de Msg
                byte[] data = bout.toByteArray();
                dgram = new DatagramPacket(data, data.length, group, port);
                socket.send(dgram);
            }
        } finally {
            if (t != null)
                t.terminate();
            if (socket != null)
                socket.close();
        }
    }
}
