package Checkers_Tactics.network;

import java.io.IOException;

public interface NetworkConnection extends AutoCloseable {

    void send(NetworkMessage message) throws IOException;

    NetworkMessage receive() throws IOException, ClassNotFoundException;

    boolean isConnected();

    @Override
    void close();
}
