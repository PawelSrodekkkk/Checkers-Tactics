package Checkers_Tactics.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GuestConnection {

    private final String hostAddress;
    private final int port;

    private Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public GuestConnection(String hostAddress, int port) {
        this.hostAddress = hostAddress;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(hostAddress, port);

        output = new ObjectOutputStream(socket.getOutputStream());

        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
    }

    public void send(NetworkMessage message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    public NetworkMessage receive() throws IOException, ClassNotFoundException {

        Object receivedObject = input.readObject();

        if (receivedObject instanceof NetworkMessage message) {
            return message;
        }

        throw new IOException("Odebrano nieobslugiwany obiekt");
    }
}
