package Checkers_Tactics.network;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkGameSession implements AutoCloseable {

    public enum Role {
        HOST(1),
        GUEST(2);

        private final int playerNumber;

        Role(int playerNumber) {
            this.playerNumber = playerNumber;
        }

        public int getPlayerNumber() {
            return playerNumber;
        }
    }

    public interface Listener {
        void onMessage(NetworkMessage message);

        void onDisconnected(String reason);
    }

    private final NetworkConnection connection;
    private final Role role;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean disconnectReported = new AtomicBoolean(false);

    private Listener listener;
    private Thread receiverThread;

    public NetworkGameSession(NetworkConnection connection, Role role) {
        this.connection = connection;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public int getLocalPlayer() {
        return role.getPlayerNumber();
    }

    public void start(Listener listener) throws IOException {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Sesja sieciowa zostala juz uruchomiona");
        }

        this.listener = listener;

        if (role == Role.HOST) {
            send(new NetworkMessage(
                    MessageType.START_GAME,
                    null,
                    "Host gra bialymi, gosc gra czarnymi"
            ));
        }

        receiverThread = new Thread(this::receiveMessages, "checkers-network-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void send(NetworkMessage message) throws IOException {
        if (!connection.isConnected()) {
            throw new IOException("Brak aktywnego polaczenia");
        }

        try {
            connection.send(message);
        } catch (IOException exception) {
            reportDisconnect(exception);
            close();
            throw exception;
        }
    }

    private void receiveMessages() {
        try {
            while (running.get()) {
                NetworkMessage message = connection.receive();
                Listener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onMessage(message);
                }
            }
        } catch (EOFException | SocketException exception) {
            if (running.get()) {
                reportDisconnect(exception);
            }
        } catch (IOException | ClassNotFoundException exception) {
            if (running.get()) {
                reportDisconnect(exception);
            }
        } finally {
            running.set(false);
            connection.close();
        }
    }

    private void reportDisconnect(Exception exception) {
        if (!disconnectReported.compareAndSet(false, true)) {
            return;
        }

        Listener currentListener = listener;
        if (currentListener != null) {
            String reason = exception.getMessage();
            currentListener.onDisconnected(reason == null ? "Polaczenie zostalo zamkniete" : reason);
        }
    }

    @Override
    public void close() {
        running.set(false);
        connection.close();
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
    }
}
