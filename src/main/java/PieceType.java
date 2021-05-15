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

    /**
     * The ordinal value.
     */
    public int value;

    /**
     * To get the PieceType by ordinal value.
     */
    public final PieceType[] values = new PieceType[7];

    /**
     * Returns the bitboard index of a white or black piece type.
     *
     * @param pieceType The {@link PieceType}.
     * @param color {@link Board.Color}.
     *
     * @return An index.
     */
    public static int getBitboardNumber(PieceType pieceType, Board.Color color) {
        return (pieceType.value + color.value * 6) - 1;
    }

    /**
     * Constructs a new {@link PieceType} enum.
     *
     * @param value {@link #value}.
     */
    PieceType(int value) {
        this.value = value;
    }
}
