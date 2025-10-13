package pt.isec.pd.tcp.serializable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;


public class TcpTimeServer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int port = 6001;
    public static final int TIMEOUT = 10; //segundos


    public static void main(String[] args) throws IOException {
        Calendar time;
        if(args.length != 1) {
            System.out.println("Sintaxe: java TcpTimeServer serverAddress");
            return;
        }
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while(true) {
                try(
                        Socket socket = serverSocket.accept();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
                ){
                    socket.setSoTimeout(TIMEOUT*1000);
                    String receive = (String) in.readObject();
                    System.out.println("Client connected from " + socket.getInetAddress() + " on port " + socket.getPort());
                    if(receive.equals("TIME")){
                        time = Calendar.getInstance();
                        out.writeObject(time);
                        out.flush();
                    } else if (receive.equals("exit")) {
                        System.out.println("Server closed by client request.");
                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Err - " + e);
                }

                System.out.println("Connection closed.");
            }
        }
    }
}
