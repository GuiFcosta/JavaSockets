package pt.isec.pd.udp.GetFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

public class GetFileUdpClient {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5; //segundos

    public static void main(String[] args) {
        File localDirectory;
        String fileName, localFilePath = null;
        InetAddress serverAddr;
        int serverPort;
        DatagramPacket packet;
        int nChunks = 0;
        int receivedBytes = 0;

        if(args.length != 4){
            System.out.println("Sintaxe: java GetFileUdpClient serverAddress serverUdpPort fileToGet localDirectory");
            return;
        }

        fileName = args[2];
        localDirectory = new File(args[3]);

        if(!localDirectory.exists()){
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if(!localDirectory.isDirectory()){
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }

        if(!localDirectory.canWrite()){
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }

        try {
            localFilePath = localDirectory.getCanonicalPath()+File.separator+fileName;
        } catch(IOException e) {
            System.out.println("Ocorreu a excepcao {" + e +"} ao obter o caminho canonico para o ficheiro local!");
            return;
        }

        try(
            FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath);
            DatagramSocket socket = new DatagramSocket()
        ){
            System.out.println("Ficheiro " + localFilePath + " criado.");

            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            socket.setSoTimeout(TIMEOUT*1000);

            packet = new DatagramPacket(fileName.getBytes(), fileName.length(), serverAddr, serverPort);
            socket.send(packet);

            System.out.println("Socket receive buffer size: " + socket.getReceiveBufferSize() + " bytes");

            do {
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                if(packet.getPort() == serverPort && packet.getAddress().equals(serverAddr)) {
                    receivedBytes += packet.getLength();
                    nChunks++;
                    localFileOutputStream.write(packet.getData(), 0, packet.getLength());
                }

            } while(packet.getLength() > 0);

            System.out.println("Transferencia concluida.");

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t"+e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t"+e);
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout de recepcao");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket ou ao ficheiro local " + localFilePath +":\n\t"+e);
        }

        System.out.format(
                "Foram recebidos %d blocos, incluindo o final vazio, " +
                "num total de %d bytes\r\n", nChunks, receivedBytes
        );
    }
}

