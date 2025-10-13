package pt.isec.pd.tcp.GetFile;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProcessClientThread extends Thread {
    private Socket socket;
    private final File localDirectory;

    public ProcessClientThread(Socket socket, File localDirectory) {
        this.socket = socket;
        this.localDirectory = localDirectory;
    }

    @Override
    public void run() {
        try (BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()))) { //2 (atendimento cliente)

            socket.setSoTimeout(GetFileTcpServer.TIMEOUT * 1000);
            OutputStream out = socket.getOutputStream();

            String requestedFileName = bin.readLine();

            if (requestedFileName == null)
                return;

            System.out.println("Recebido pedido para \"" + requestedFileName + "\" de " + socket.getInetAddress().getHostName() + ":" + socket.getPort());

            String requestedCanonicalFilePath = new File(localDirectory + File.separator + requestedFileName).getCanonicalPath();

            if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");
                return;
            }

            try (InputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {
                System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

                int totalBytes = 0;
                int nChunks = 0;
                int nbytes;
                byte[] fileChunk = new byte[GetFileTcpServer.MAX_SIZE];


                do {
                    nbytes = requestedFileInputStream.read(fileChunk);

                    if (nbytes > -1) {//Not EOF
                        out.write(fileChunk, 0, nbytes);
                        out.flush();

                        totalBytes += nbytes;
                        nChunks++;
                    }

                } while (nbytes > 0);

                System.out.format("Transferência concluída em %d blocos com um total de %d bytes\r\n", nChunks, totalBytes);
            }

        } catch (SocketTimeoutException ex) { //Subclasse de IOException
            System.out.println("O cliente atual nao enviou qualquer nome de ficheiro (timeout)");
        } catch (FileNotFoundException e) {   //Subclasse de IOException
            System.out.println("Ocorreu a exceção {" + e + "} ao tentar abrir o ficheiro !");
        } catch (IOException ex) {
            System.out.println("Problem de I/O no atendimento ao cliente atual: " + ex);
        } //try 2 (atendimento cliente)
    }
}
