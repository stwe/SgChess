/**
 * Represents a PieceType.
 */
public enum PieceType {
    NO_PIECE(0),
    PAWN(1),
    KNIGHT(2),
    BISHOP(3),
    ROOK(4),
    QUEEN(5),
    KING(6);

    public int value;

    PieceType(int value) {
        this.value = value;
    }
}
