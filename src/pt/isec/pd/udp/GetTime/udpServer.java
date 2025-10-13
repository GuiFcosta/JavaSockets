package pt.isec.pd.udp.GetTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpServer {
    public static void main(String[] args) {
        boolean run = true;
        try (DatagramSocket socket = new DatagramSocket(6002)) {
            while (run) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                    socket.receive(packet);
                    InetAddress remoteAddr = packet.getAddress();
                    int remotePort = packet.getPort();
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received: " + msg);
                    if (msg.equals("TIME")) {
                        LocalDateTime time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String timeStr = time.format(formatter);
                        byte[] data = timeStr.getBytes();
                        DatagramPacket response = new DatagramPacket(data, data.length, remoteAddr, remotePort);
                        socket.send(response);
                    } else if (msg.equals("exit")) {
                        run = false;
                        System.out.println("Server closed");
                    }
                } catch (IOException e) {
                    System.err.println("Erro: " + e);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar o socket: " + e);
        }
    }
}
