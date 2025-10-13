package pt.isec.pd.udp.GetFile;

import java.io.*;
import java.net.*;

public class GetFileUdpServer {
    public static final int MAX_DATA = 4000;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Sintaxe: java GetFileUdpServer <port> <directory>");
            return;
        }

        int port;
        File baseDir;
        try {
            port = Integer.parseInt(args[0]);
            baseDir = new File(args[1]).getCanonicalFile();
            if (!baseDir.isDirectory()) {
                System.out.println("A diretoria indicada não existe!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Erro nos argumentos: " + e);
            return;
        }

        // Cria socket UDP
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor a escutar no porto " + port);

            while (true) {
                /*
                    new DatagramPacket(buffer, length)
                    buffer: array de bytes onde os dados recebidos serão armazenados
                    length: tamanho do array de bytes
                    request: pacote recebido
                    receive() -> aguarda a chegada de um pacote
                */
                DatagramPacket request = new DatagramPacket(new byte[MAX_DATA], MAX_DATA);
                socket.receive(request);

                String fileName = new String(request.getData(), 0, request.getLength()).trim();
                File requestedFile = new File(baseDir, fileName).getCanonicalFile();

                /*
                    Verifica se o ficheiro está dentro da diretoria base
                    getPath(): caminho absoluto
                    startsWith(): verifica se começa com
                    isFile(): verifica se é um ficheiro
                */
                if (!requestedFile.getPath().startsWith(baseDir.getPath()) || !requestedFile.isFile()) {
                    System.out.println("Pedido inválido: " + requestedFile);
                    continue;
                }

                /*
                    Envia o ficheiro em blocos
                    FileInputStream: lê bytes de um ficheiro
                    new FileInputStream(file): cria um fluxo de entrada para o ficheiro
                */
                try (FileInputStream fis = new FileInputStream(requestedFile)) {
                    /*
                        Lê e envia blocos de dados
                        buffer: array de bytes para armazenar os dados lidos
                    */
                    byte[] buffer = new byte[MAX_DATA];
                    int bytesRead;
                    /*
                        while ((bytesRead = fis.read(buffer)) != -1)
                        fis.read(buffer): lê até buffer.length bytes do ficheiro
                        bytesRead: número de bytes lidos
                        -1: fim do ficheiro
                        DatagramPacket: pacote de dados a enviar
                        new DatagramPacket(buffer, bytesRead, address, port)
                        buffer: array de bytes com os dados a enviar
                        bytesRead: número de bytes a enviar
                        address: endereço IP do destinatário
                        port: porto do destinatário
                        socket.send(packet): envia o pacote através do socket
                    */
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        DatagramPacket dataPacket = new DatagramPacket(
                                buffer, bytesRead,
                                request.getAddress(),
                                request.getPort()
                        );
                        socket.send(dataPacket);
                    }
                    /*
                        Envia um pacote vazio para indicar o fim da transferência
                        new DatagramPacket(new byte[0], 0, address, port)
                        new byte[0]: array de bytes vazio
                        0: tamanho do array
                    */
                    DatagramPacket endPacket = new DatagramPacket(
                            new byte[0],
                            0,
                            request.getAddress(),
                            request.getPort()
                    );
                    socket.send(endPacket);
                    System.out.println("Transferência concluída: " + fileName);
                } catch (IOException e) {
                    System.out.println("Erro ao enviar ficheiro: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e);
        }
    }
}
