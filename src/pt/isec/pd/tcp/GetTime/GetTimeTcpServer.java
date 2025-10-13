package pt.isec.pd.tcp.GetTime;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GetTimeTcpServer {
    public static void main(String[] args) {
        int port;

        if(args.length != 1){
            System.out.println("Use: java GetTimeTcpServer <port>");
            return;
        }

        port = Integer.parseInt(args[0]);

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server listening on port " + port);

            while(true){
                try{
                    Socket socket = serverSocket.accept();
                    new Thread(new ProcessClientThread(socket)).start();

                } catch (IOException e) {
                    System.err.println("Err - " + e);
                }
            }

        } catch (BindException e) {
            System.err.println("Port " + port + " already in use. " + e);
        } catch (IOException e) {
            System.err.println("Err - " + e);
        }
    }
}
