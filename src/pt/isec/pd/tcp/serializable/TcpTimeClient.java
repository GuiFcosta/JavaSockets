package pt.isec.pd.tcp.serializable;

//import pt.isec.pd.time.Time;
import pt.isec.pd.aula4.Time;

import java.net.*;
import java.io.*;
import java.util.*;

public class TcpTimeClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String TIME_REQUEST = "time";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) throws IOException {

        InetAddress serverAddr = null;
        int serverPort = -1;

        Time response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java TcpTimeClient serverAddress serverUdpPort");
            return;
        }

        try {
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            try (Socket socket = new Socket(serverAddr, serverPort);
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                socket.setSoTimeout(TIMEOUT * 1000);

                out.writeObject(TIME_REQUEST);
                out.flush();

                response = (Time) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (response == null) {
                System.out.println("O servidor nao enviou qualquer resposta antes de"
                        + " fechar a ligacao TCP!");
            } else {
                System.out.println("Hora recebida do servidor: " + response);
                System.out.println();
            }

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Não foi recebida qualquer resposta:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
    }

}
