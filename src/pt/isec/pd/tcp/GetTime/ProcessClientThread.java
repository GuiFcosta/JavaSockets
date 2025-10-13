package pt.isec.pd.tcp.GetTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

public record ProcessClientThread(Socket socket) implements Runnable {
    public static final String TIME_REQUEST = "TIME";

    @Override
    public void run() {
        try (
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            socket.setSoTimeout(1000);
            String receive = in.readLine();
            System.out.println("Client connected from " + socket.getInetAddress() + " on port " + socket.getPort());
            if (receive.equalsIgnoreCase(TIME_REQUEST)) {
                Calendar calendar = GregorianCalendar.getInstance();
                String timeStr = calendar.get(GregorianCalendar.HOUR_OF_DAY) + ":" +
                        calendar.get(GregorianCalendar.MINUTE) + ":" +
                        calendar.get(GregorianCalendar.SECOND);
                pw.println(timeStr);
                pw.flush();
            }
        } catch (IOException e) {
            System.err.println("Err - " + e);
        } finally {
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                System.err.println("Err - " + e);
            }
        }
    }
}
