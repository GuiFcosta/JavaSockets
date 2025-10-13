package pt.isec.pd.udp.GetTime;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class udpClient {
    public static void main(String[] args) {
        String hostName = "127.0.0.1";
        boolean run = true;
        Scanner sc = new Scanner(System.in);
        try (DatagramSocket socket = new DatagramSocket()) {
            while (run) {
                String input = sc.nextLine();
                if (input.equals("TIME")) {
                    try {
                        byte[] buffer = "TIME".getBytes();
                        InetAddress addr = InetAddress.getByName(hostName);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 6002);
                        socket.send(packet);
                        DatagramPacket response = new DatagramPacket(new byte[256], 256);
                        socket.receive(response);
                        String msg = new String(response.getData(), 0, response.getLength());
                        System.out.println("Received: " + msg);
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                } else {
                    byte[] buffer = "exit".getBytes();
                    InetAddress addr = InetAddress.getByName(hostName);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 6002);
                    socket.send(packet);
                    run = false;
                    System.out.println("Client closed");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar o socket: " + e.getMessage());
        }
        sc.close();
    }
}
