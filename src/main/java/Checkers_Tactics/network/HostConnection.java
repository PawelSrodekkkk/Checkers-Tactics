package Checkers_Tactics.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HostConnection {

    private final int port;

    private ServerSocket serverSocket;
    private Socket guestSocket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public HostConnection(int port) {
        this.port = port;
    }

    public void waitForGuest() throws IOException {

        serverSocket = new ServerSocket(port);

        System.out.println("Host nasluchuje na porcie " + port);

        guestSocket = serverSocket.accept();

        output = new ObjectOutputStream(guestSocket.getOutputStream());

        output.flush();

        input = new ObjectInputStream(guestSocket.getInputStream());
    }

    public void send(NetworkMessage message) throws IOException{
        output.writeObject(message);
        output.flush();
    }

    public NetworkMessage receive() throws IOException, ClassNotFoundException{

        Object receivedObject = input.readObject();

        if (receivedObject instanceof NetworkMessage message) {
            return message;
        }

        throw new IOException("Odebrano nieobslugiwany obiekt");
    }


}
