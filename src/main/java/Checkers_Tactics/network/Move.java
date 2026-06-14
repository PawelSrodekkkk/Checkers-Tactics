package Checkers_Tactics.network;

import java.io.Serializable;
import java.util.Objects;

public class Move implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int fromRow;
    private final int fromColumn;
    private final int toRow;
    private final int toColumn;

    public Move(int fromRow, int fromColumn, int toRow, int toColumn) {
        this.fromRow = fromRow;
        this.fromColumn = fromColumn;
        this.toRow = toRow;
        this.toColumn = toColumn;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToColumn() {
        return toColumn;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Move move)) return false;
        return fromRow == move.fromRow
                && fromColumn == move.fromColumn
                && toRow == move.toRow
                && toColumn == move.toColumn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromColumn, toRow, toColumn);
    }

    @Override
    public String toString() {
        return "Move{" + "fromRow=" + fromRow + ", fromColumn=" + fromColumn + ", toRow=" + toRow + ", toColumn=" + toColumn + '}';
    }
}
