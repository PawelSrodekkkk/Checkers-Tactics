package Checkers_Tactics.network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private final Move move;
    private final String text;

    public NetworkMessage(MessageType type, Move move, String text)
    {
        this.type = type;
        this.move = move;
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public Move getMove() {
        return move;
    }

    public String getText() {
        return text;
    }
}
