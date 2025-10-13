package pt.isec.pd.tcp.GetFile;

import java.io.*;
import java.net.Socket;

public class GetFileTcpClient {
    public static void main(String[] args) {
        String fileToGet, localFilePath = null;
        File localDirectory;
        if(args.length != 4){
            System.out.println("Use: java GetFileTcpClient serverAddress serverUdpPort fileToGet localDirectory");
            return;
        }
        fileToGet = args[2];
        localDirectory = new File(args[3]);
        int nbytes;
        int nLoops = 0;
        int receivedBytes = 0;
        byte[] fileChunck = new byte[4096];

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
            localFilePath = localDirectory.getCanonicalPath()+File.separator+fileToGet;
        } catch(IOException e) {
            System.out.println("Ocorreu a excepcao {" + e +"} ao obter o caminho canonico para o ficheiro local!");
            return;
        }

        try (
            FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath);
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
        ){
            System.out.println("Connected to " + socket.getInetAddress() + " on port " + socket.getPort());
            socket.setSoTimeout(30000); // 3 seconds
            InputStream is = socket.getInputStream();
            pw.println(fileToGet);

            while((nbytes = is.read(fileChunck)) > 0){
                nLoops++;
                receivedBytes += nbytes;
                localFileOutputStream.write(fileChunck, 0, nbytes);
            }

            System.out.println("Connection closed.");

        } catch (InterruptedIOException e){
            System.err.println("Timeout: " + e);
        } catch (Exception e){
            System.err.println("Err - " + e);
        }
    }
}
