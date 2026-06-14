package Checkers_Tactics.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GuestConnection implements NetworkConnection {

    private final String hostAddress;
    private final int port;
    private final int connectTimeoutMillis;

    private Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public GuestConnection(String hostAddress, int port) {
        this(hostAddress, port, 10_000);
    }

    public GuestConnection(String hostAddress, int port, int connectTimeoutMillis) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void connect() throws IOException {
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(hostAddress, port), connectTimeoutMillis);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException exception) {
            close();
            throw exception;
        }
    }

    @Override
    public synchronized void send(NetworkMessage message) throws IOException {
        if (output == null) {
            throw new IOException("Brak polaczenia z hostem");
        }
        output.writeObject(message);
        output.flush();
        output.reset();
    }

    @Override
    public NetworkMessage receive() throws IOException, ClassNotFoundException {
        if (input == null) {
            throw new IOException("Brak polaczenia z hostem");
        }
        Object receivedObject = input.readObject();

        if (receivedObject instanceof NetworkMessage message) {
            return message;
        }

        throw new IOException("Odebrano nieobslugiwany obiekt");
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void close() {
        closeQuietly(input);
        closeQuietly(output);
        closeQuietly(socket);
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
