package pt.isec.pd.tcp.GetFile;

import java.io.*;
import java.net.*;

public class GetFileTcpServer {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) {
        File localDirectory;

        if (args.length != 2) {
            System.out.println("Sintaxe: java GetFileUdpServer listeningPort localRootDirectory");
            return;
        }
        localDirectory = new File(args[1].trim());

        if (!localDirectory.exists()) {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }

        if (!localDirectory.canRead()) {
            System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
            return;
        }

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) { // 1

            while (true) {
                Socket socket = serverSocket.accept();
                new ProcessClientThread(socket, localDirectory).start(); // 3

            } //while

        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu uma excepcao ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        } //try 1
    } // main
}
