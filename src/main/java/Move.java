/**
 * Represents a Move object.
 */
public class Move {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The piece.
     */
    public Piece piece;

    /**
     * The start index.
     */
    public int from;

    /**
     * The target of the move.
     */
    public int to;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece {@link #piece}
     * @param from {@link #from}
     * @param to {@link #to}
     */
    public Move(Piece piece, int from, int to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {
        return "Piece: " + piece.moveLetter + " from: " + Bitboard.SQUARE_STRINGS[from] + " to: " + Bitboard.SQUARE_STRINGS[to];
    }
}
