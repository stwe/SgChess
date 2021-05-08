/**
 * Represents a Move object.
 */
public class Move {

    // from, to (6)
    // captured (4)
    // ep, pawn start (1)
    // promoted piece (4)
    // castle (1)

    private int move;

    //-------------------------------------------------
    // From
    //-------------------------------------------------

    public int getFrom() {
        // 0x3F = 000000111111
        return move & 0x3F;
    }

    public void setFrom(int bitIndex) {
        // clear first 6 bits
        move &= ~0x3F;
        // mask on the first 6 bits an OR with bitIndex
        move |= (bitIndex & 0x3F);
    }

    //-------------------------------------------------
    // To
    //-------------------------------------------------

    public int getTo() {
        // 0xFC0 = 111111000000
        return (move & 0xFC0) >>> 6;
    }

    public void setTo(int bitIndex) {
        move &= ~0xFC0;
        move |= (bitIndex & 0x3F) << 6;
    }

    //-------------------------------------------------
    // Captured
    //-------------------------------------------------

    public int getCaptured() {
        return 0;
    }

    public void setCaptured() {

    }

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

    public Move() {}

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
