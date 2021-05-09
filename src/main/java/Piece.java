/**
 * Represents a Piece.
 */
public enum Piece {

    //-------------------------------------------------
    // Pieces
    //-------------------------------------------------

    WHITE_PAWN("♟ ", "P ", 'P', PieceType.PAWN, 0),
    WHITE_KNIGHT("♞ ", "N ", 'N', PieceType.KNIGHT, 1),
    WHITE_BISHOP("♝ ", "B ", 'B', PieceType.BISHOP, 2),
    WHITE_ROOK("♜ ", "R ", 'R', PieceType.ROOK, 3),
    WHITE_QUEEN("♛ ", "Q ", 'Q', PieceType.QUEEN, 4),
    WHITE_KING("♚ ", "K ", 'K', PieceType.KING, 5),

    BLACK_PAWN("♙ ", "p ", 'p', PieceType.PAWN, 6),
    BLACK_KNIGHT("♘ ", "n ", 'n', PieceType.KNIGHT, 7),
    BLACK_BISHOP("♗ ", "b ", 'b', PieceType.BISHOP, 8),
    BLACK_ROOK("♖ ", "r ", 'r', PieceType.ROOK, 9),
    BLACK_QUEEN("♕ ", "q ", 'q', PieceType.QUEEN, 10),
    BLACK_KING("♔ ", "k ", 'k', PieceType.KING, 11);

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * UTF8 Chess symbol.
     * Used for a pretty readable string representation of this piece in a chessboard.
     */
    public final String symbol;

    /**
     * Used for a readable string representation of this piece in a chessboard.
     */
    public final String letter;

    /**
     * Used for the chess notation.
     * White pieces are represented as upper case letters and black pieces are
     * represented as lower case letters.
     */
    public final char moveLetter;

    /**
     * The type of the piece.
     */
    public final PieceType pieceType;

    /**
     * The ordinal value.
     */
    public final int value;

    /**
     * To get the Piece by ordinal value.
     */
    public final Piece[] values = new Piece[12];

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Piece} enum.
     *
     * @param symbol {@link #symbol}.
     * @param letter {@link #letter}.
     * @param moveLetter {@link #moveLetter}.
     * @param pieceType {@link #pieceType}.
     * @param value {@link #value}.
     */
    Piece(String symbol, String letter, char moveLetter, PieceType pieceType, int value) {
        this.symbol = symbol;
        this.letter = letter;
        this.moveLetter = moveLetter;
        this.pieceType = pieceType;
        this.value = value;
        this.values[value] = this;
    }
}
