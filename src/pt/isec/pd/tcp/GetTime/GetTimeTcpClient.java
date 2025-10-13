package pt.isec.pd.tcp.GetTime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;

public class GetTimeTcpClient {
    public static final String TIME_REQUEST = "TIME\n";
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Use: java GetTimeTcpClient <host> <port>");
            return;
        }
        try (
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader( socket.getInputStream()));
        ){
            System.out.println("Connected to " + socket.getInetAddress() + " on port " + socket.getPort());
            socket.setSoTimeout(3000); // 3 seconds
            pw.println(TIME_REQUEST);

            System.out.println("Time received from server: " + reader.readLine());
            System.out.println("Connection closed.");

        } catch (InterruptedIOException e){
            System.err.println("Timeout: " + e);
        } catch (Exception e){
            System.err.println("Err - " + e);
        }
    }
}
