package pt.isec.pd.udp.serializable;

import java.io.*;
import java.net.*;
import java.util.Calendar;

public class UdpTimeClientSerializable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int MAX_SIZE = 10000;
    public static final String TIME_REQUEST = "TIME\n";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args)
    {
        InetAddress serverAddr = null; // Endereco IP do servidor
        DatagramPacket packet = null; // Pacote UDP
        int serverPort; // Porto do servidor
        Calendar returnedObject; // Objeto recebido do servidor

        if(args.length != 2){
            System.out.println("Sintaxe: java UdpTimeClientSerializable serverAddress serverUdpPort");
            return;
        }

        try(DatagramSocket socket = new DatagramSocket()){
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            socket.setSoTimeout(TIMEOUT*1000);

            /*
                ByteArrayOutputStream -> This class implements an output stream in which the data is
                written into a byte array. The buffer automatically grows as data is written to it.
                The data can be retrieved using toByteArray() and toString().

                ObjectOutputStream -> An ObjectOutputStream writes primitive data types and graphs of Java objects to
                an OutputStream. The objects can be read (reconstituted) using an ObjectInputStream.
            */
            try(
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout)
            ){
                out.writeObject(TIME_REQUEST);
                out.flush();

                packet = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);
            }
            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            try(ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()))){
                returnedObject = (Calendar) in.readObject();
            }

            System.out.println("Hora recebida do servidor: " + returnedObject.getTime());
            System.out.println();

        }catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        } catch (ClassNotFoundException e) {
            System.out.println("Classe não encontrada ao desserializar objeto:\n\t"+e);
            throw new RuntimeException(e);
        }
    }
}