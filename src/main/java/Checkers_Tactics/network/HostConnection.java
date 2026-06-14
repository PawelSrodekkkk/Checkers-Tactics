package Checkers_Tactics.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HostConnection implements NetworkConnection {

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
        try {
            System.out.println("Host nasluchuje na porcie " + port);
            guestSocket = serverSocket.accept();
            output = new ObjectOutputStream(guestSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(guestSocket.getInputStream());
        } catch (IOException exception) {
            close();
            throw exception;
        } finally {
            closeServerSocket();
        }
    }

    @Override
    public synchronized void send(NetworkMessage message) throws IOException {
        if (output == null) {
            throw new IOException("Gosc nie jest polaczony");
        }
        output.writeObject(message);
        output.flush();
        output.reset();
    }

    @Override
    public NetworkMessage receive() throws IOException, ClassNotFoundException{
        if (input == null) {
            throw new IOException("Gosc nie jest polaczony");
        }
        Object receivedObject = input.readObject();

        if (receivedObject instanceof NetworkMessage message) {
            return message;
        }

        throw new IOException("Odebrano nieobslugiwany obiekt");
    }

    @Override
    public boolean isConnected() {
        return guestSocket != null
                && guestSocket.isConnected()
                && !guestSocket.isClosed();
    }

    @Override
    public void close() {
        closeQuietly(input);
        closeQuietly(output);
        closeQuietly(guestSocket);
        closeServerSocket();
    }

    private void closeServerSocket() {
        closeQuietly(serverSocket);
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }
}
