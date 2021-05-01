/**
 * Represents a Move object.
 */
public class Move {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * For print out.
     */
    public char piece;

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
    public Move(char piece, int from, int to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {
        return "Piece: " + piece + " from: " + Bitboard.SQUARE_STRINGS[from] + " to: " + Bitboard.SQUARE_STRINGS[to];
    }
}
