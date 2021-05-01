/**
 * Represents a Piece.
 */
public enum Piece {

    //-------------------------------------------------
    // Elements
    //-------------------------------------------------

    WHITE_PAWNS("♟ "),
    WHITE_KNIGHTS("♞ "),
    WHITE_BISHOPS("♝ "),
    WHITE_ROOKS("♜ "),
    WHITE_QUEENS("♛ "),
    WHITE_KING("♚ "),

    BLACK_PAWNS("♙ "),
    BLACK_KNIGHTS("♘ "),
    BLACK_BISHOPS("♗ "),
    BLACK_ROOKS("♖ "),
    BLACK_QUEENS("♕ "),
    BLACK_KING("♔ ");

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * UTF8 Chess symbol.
     */
    public final String symbol;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Piece} enum.
     *
     * @param symbol {@link #symbol}
     */
    Piece(String symbol) {
        this.symbol = symbol;
    }
}
