/**
 * Represents a Piece.
 */
public enum Piece {

    //-------------------------------------------------
    // Pieces
    //-------------------------------------------------

    WHITE_PAWN("♟ ", "P ", 'P'),
    WHITE_KNIGHT("♞ ", "N ", 'N'),
    WHITE_BISHOP("♝ ", "B ", 'B'),
    WHITE_ROOK("♜ ", "R ", 'R'),
    WHITE_QUEEN("♛ ", "Q ", 'Q'),
    WHITE_KING("♚ ", "K ", 'K'),

    BLACK_PAWN("♙ ", "p ", 'p'),
    BLACK_KNIGHT("♘ ", "n ", 'n'),
    BLACK_BISHOP("♗ ", "b ", 'b'),
    BLACK_ROOK("♖ ", "r ", 'r'),
    BLACK_QUEEN("♕ ", "q ", 'q'),
    BLACK_KING("♔ ", "k ", 'k');

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * UTF8 Chess symbol.
     * Used for a simple gui representation of the board.
     */
    public final String symbol;

    /**
     * Used for a simple gui representation of the board.
     */
    public final String letter;

    /**
     * Used for the chess notation.
     */
    public final char moveLetter;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Piece} enum.
     *
     * @param symbol {@link #symbol}
     * @param letter {@link #letter}
     * @param moveLetter {@link #moveLetter}
     */
    Piece(String symbol, String letter, char moveLetter) {
        this.symbol = symbol;
        this.letter = letter;
        this.moveLetter = moveLetter;
    }
}
