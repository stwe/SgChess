import java.util.Objects;

/**
 * Represents a Board object.
 */
public class Board {

    //-------------------------------------------------
    // Types
    //-------------------------------------------------

    public enum Color {
        WHITE, BLACK
    }

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    // ANSI escape sequences

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_BLACK = "\u001B[30;1m";
    private static final String ANSI_RED = "\u001B[31;1m";
    private static final String ANSI_GREEN = "\u001B[32;1m";
    private static final String ANSI_YELLOW = "\u001B[33;1m";
    private static final String ANSI_BLUE = "\u001B[34;1m";
    private static final String ANSI_PURPLE = "\u001B[35;1m";
    private static final String ANSI_CYAN = "\u001B[36;1m";
    private static final String ANSI_WHITE = "\u001B[37;1m";

    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40;1m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41;1m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42;1m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43;1m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44;1m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45;1m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46;1m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47;1m";

    // board and pieces colors

    private static final String BOARD_WHITE = ANSI_BLACK;
    private static final String BOARD_BLACK = ANSI_BLACK;

    private static final String PIECE_WHITE = "\u001B[38;5;15m";
    private static final String PIECE_BLACK = ANSI_RED;

    /**
     * The FEN for the starting position.
     */
    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The toString method uses the UTF8 chess pieces.
     */
    private boolean showUnicodeSymbols = false;

    /**
     * The board is output in color.
     */
    private boolean colored = true;

    /**
     * The bitboards to represent the positions of each kind and color of piece on a chessboard.
     */
    private final long[] bitboards = new long[Bitboard.NUMBER_OF_BITBOARDS];

    /**
     * A bitboard with all the pieces.
     */
    private long allPiecesBitboard = 0L;

    /**
     * Denoting who is on the move.
     */
    private Color colorToMove = Color.WHITE;

    /**
     * An integer containing castling rights.
     */
    private int castlingRights;

    /**
     * The bit index of the En Passant target square.
     */
    private int epIndex = 0;

    /**
     * A {@link MoveGenerator} object.
     */
    private final MoveGenerator moveGenerator;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Board} object.
     */
    public Board() {
        this(FEN_START);
    }

    /**
     * Constructs a new {@link Board} object.
     *
     * @param fen A particular FEN board position.
     */
    public Board(String fen) {
        initWithFen(Objects.requireNonNull(fen, "fen must not be null"));
        updateCommonBitboards();

        moveGenerator = new MoveGenerator(this);
    }

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    public long getWhitePawns() {
        return bitboards[Bitboard.WHITE_PAWNS_BITBOARD];
    }

    public long getWhiteKnights() {
        return bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD];
    }

    public long getWhiteBishops() {
        return bitboards[Bitboard.WHITE_BISHOPS_BITBOARD];
    }

    public long getWhiteRooks() {
        return bitboards[Bitboard.WHITE_ROOKS_BITBOARD];
    }

    public long getWhiteQueens() {
        return bitboards[Bitboard.WHITE_QUEENS_BITBOARD];
    }

    public long getWhiteKing() {
        return bitboards[Bitboard.WHITE_KING_BITBOARD];
    }

    public long getBlackPawns() {
        return bitboards[Bitboard.BLACK_PAWNS_BITBOARD];
    }

    public long getBlackKnights() {
        return bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD];
    }

    public long getBlackBishops() {
        return bitboards[Bitboard.BLACK_BISHOPS_BITBOARD];
    }

    public long getBlackRooks() {
        return bitboards[Bitboard.BLACK_ROOKS_BITBOARD];
    }

    public long getBlackQueens() {
        return bitboards[Bitboard.BLACK_QUEENS_BITBOARD];
    }

    public long getBlackKing() {
        return bitboards[Bitboard.BLACK_KING_BITBOARD];
    }

    public long getWhitePieces() {
        return bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD];
    }

    public long getBlackPieces() {
        return bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD];
    }

    public long getAllPawns() {
        return bitboards[Bitboard.ALL_PAWNS_BITBOARD];
    }

    public long getAllKnights() {
        return bitboards[Bitboard.ALL_KNIGHTS_BITBOARD];
    }

    public long getAllBishops() {
        return bitboards[Bitboard.ALL_BISHOPS_BITBOARD];
    }

    public long getAllRooks() {
        return bitboards[Bitboard.ALL_ROOKS_BITBOARD];
    }

    public long getAllQueens() {
        return bitboards[Bitboard.ALL_QUEENS_BITBOARD];
    }

    public long getAllKings() {
        return bitboards[Bitboard.ALL_KINGS_BITBOARD];
    }

    public long getAllPieces() {
        return allPiecesBitboard;
    }

    /**
     * Get {@link #showUnicodeSymbols}.
     *
     * @return boolean
     */
    public boolean isShowUnicodeSymbols() {
        return showUnicodeSymbols;
    }

    /**
     * Get {@link #colored}.
     *
     * @return boolean
     */
    public boolean isColored() {
        return colored;
    }

    /**
     * Get {@link #colorToMove}.
     *
     * @return {@link #colorToMove}.
     */
    public Color getColorToMove() {
        return colorToMove;
    }

    /**
     * Get {@link #castlingRights}.
     *
     * @return {@link #castlingRights}.
     */
    public int getCastlingRights() {
        return castlingRights;
    }

    /**
     * Get {@link #epIndex}.
     *
     * @return {@link #epIndex}.
     */
    public int getEpIndex() {
        return epIndex;
    }

    /**
     * Get {@link #moveGenerator}.
     *
     * @return {@link #moveGenerator}.
     */
    public MoveGenerator getMoveGenerator() {
        return moveGenerator;
    }

    //-------------------------------------------------
    // Setter
    //-------------------------------------------------

    /**
     * Set {@link #showUnicodeSymbols}.
     *
     * @param showUnicodeSymbols {@link #showUnicodeSymbols}.
     */
    public void setShowUnicodeSymbols(boolean showUnicodeSymbols) {
        this.showUnicodeSymbols = showUnicodeSymbols;
    }

    /**
     * Set {@link #colored}.
     *
     * @param colored {@link #colored}
     */
    public void setColored(boolean colored) {
        this.colored = colored;
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(" +---+---+---+---+---+---+---+---+\n");

        for (var rank = 8; rank >= 1; rank--) {
            s.append(rank).append("|");

            for (var file = 1; file <= 8; file++) {
                var piece = getPieceString(file - 1, rank - 1);

                // set square background
                if (colored) {
                    if (file % 2 == 1 && rank % 2 == 0) {
                        s.append(BOARD_WHITE);
                    }
                    if (file % 2 == 0 && rank % 2 == 0) {
                        s.append(BOARD_BLACK);
                    }

                    if (file % 2 == 1 && rank % 2 == 1) {
                        s.append(BOARD_BLACK);
                    }
                    if (file % 2 == 0 && rank % 2 == 1) {
                        s.append(BOARD_WHITE);
                    }
                }

                // append piece
                if (piece.equals("")) {
                    s.append("   ");
                    if (colored) {
                        s.append(ANSI_RESET);
                    }
                } else {
                    s.append(" ");
                    s.append(piece);
                }

                s.append("|");
            }

            if (rank == 8) {
                s.append("    on the move: ").append(colorToMove);
            }

            if (rank == 7) {
                s.append("    total possible moves: ").append(moveGenerator.getMoves().size());
            }

            if (rank == 6) {
                s.append("    castling rights: ");
            }

            s.append("\n");
            s.append(" +---+---+---+---+---+---+---+---+\n");
        }

        s.append("   a   b   c   d   e   f   g   h");

        return s.toString();
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    /**
     * The main init method. Splits the FEN and calls further methods to set the bitboards.
     *
     * @param fen A particular FEN board position.
     */
    private void initWithFen(String fen) {
        Objects.requireNonNull(fen, "fen must not be null");

        var fenFields = fen.split(" ");
        if (fenFields.length != 6) {
            throw new RuntimeException("Invalid FEN record given.");
        }

        // pieces
        var piecePlacement = fenFields[0].split("/");
        if (piecePlacement.length != 8) {
            throw new RuntimeException("Invalid piece placement.");
        }

        var currentRank = 7;
        for (var pieces : piecePlacement) {
            setBitboards(pieces, currentRank);
            currentRank--;
        }

        // color to move
        colorToMove = fenFields[1].equals("w") ? Color.WHITE : Color.BLACK;

        // castling
        // todo

        // en passant
        if (!fenFields[3].equals("-")) {
            var file = (104 - fenFields[3].charAt(0)) + 1;
            var rank = Integer.parseInt(fenFields[3].substring(1)) - 1;
            epIndex = Bitboard.getSquareByFileAndRank(file, rank);
        }
    }

    /**
     * Adds all pieces of a rank to the {@link #bitboards}.
     *
     * @param pieces All pieces of a rank.
     * @param currentRank The number of the rank.
     */
    private void setBitboards(String pieces, int currentRank) {
        Objects.requireNonNull(pieces, "pieces must not be null");

        var x = -1;
        for (var ch : pieces.toCharArray()) {
            switch (ch) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    x += Character.digit(ch, 10);
                    break;

                case 'P' :
                    bitboards[Bitboard.WHITE_PAWNS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'N' :
                    bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'B' :
                    bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'R' :
                    bitboards[Bitboard.WHITE_ROOKS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'Q' :
                    bitboards[Bitboard.WHITE_QUEENS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'K' :
                    bitboards[Bitboard.WHITE_KING_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;

                case 'p' :
                    bitboards[Bitboard.BLACK_PAWNS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'n' :
                    bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'b' :
                    bitboards[Bitboard.BLACK_BISHOPS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'r' :
                    bitboards[Bitboard.BLACK_ROOKS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'q' :
                    bitboards[Bitboard.BLACK_QUEENS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                case 'k' :
                    bitboards[Bitboard.BLACK_KING_BITBOARD] |= Bitboard.SQUARES[Bitboard.getSquareByFileAndRank(++x, currentRank)];
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ch);
            }
        }
    }

    //-------------------------------------------------
    // Update
    //-------------------------------------------------

    /**
     * Updates all bitboards that consist of several other bitboards.
     */
    private void updateCommonBitboards() {
        bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD] =
                bitboards[Bitboard.WHITE_PAWNS_BITBOARD] |
                bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] |
                bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] |
                bitboards[Bitboard.WHITE_ROOKS_BITBOARD] |
                bitboards[Bitboard.WHITE_QUEENS_BITBOARD] |
                bitboards[Bitboard.WHITE_KING_BITBOARD];

        bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD] =
                bitboards[Bitboard.BLACK_PAWNS_BITBOARD] |
                bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD] |
                bitboards[Bitboard.BLACK_BISHOPS_BITBOARD] |
                bitboards[Bitboard.BLACK_ROOKS_BITBOARD] |
                bitboards[Bitboard.BLACK_QUEENS_BITBOARD] |
                bitboards[Bitboard.BLACK_KING_BITBOARD];

        bitboards[Bitboard.ALL_PAWNS_BITBOARD] = bitboards[Bitboard.WHITE_PAWNS_BITBOARD] | bitboards[Bitboard.BLACK_PAWNS_BITBOARD];
        bitboards[Bitboard.ALL_KNIGHTS_BITBOARD] = bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] | bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD];
        bitboards[Bitboard.ALL_BISHOPS_BITBOARD] = bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] | bitboards[Bitboard.BLACK_BISHOPS_BITBOARD];
        bitboards[Bitboard.ALL_ROOKS_BITBOARD] = bitboards[Bitboard.WHITE_ROOKS_BITBOARD] | bitboards[Bitboard.BLACK_ROOKS_BITBOARD];
        bitboards[Bitboard.ALL_QUEENS_BITBOARD] = bitboards[Bitboard.WHITE_QUEENS_BITBOARD] | bitboards[Bitboard.BLACK_QUEENS_BITBOARD];
        bitboards[Bitboard.ALL_KINGS_BITBOARD] = bitboards[Bitboard.WHITE_KING_BITBOARD] | bitboards[Bitboard.BLACK_KING_BITBOARD];

        allPiecesBitboard = bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD] | bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD];
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    /**
     * Returns a String for the specified file/rank.
     *
     * @param file A vertical line on the chessboard.
     * @param rank A horizontal line on the chessboard.
     *
     * @return The String for the square.
     */
    private String getPieceString(int file, int rank) {
        if (Bitboard.isBitSet(getWhitePawns(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_PAWN.symbol + ANSI_RESET : Piece.WHITE_PAWN.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_PAWN.letter + ANSI_RESET : Piece.WHITE_PAWN.letter;
        }

        if (Bitboard.isBitSet(getWhiteKnights(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_KNIGHT.symbol + ANSI_RESET : Piece.WHITE_KNIGHT.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_KNIGHT.letter + ANSI_RESET : Piece.WHITE_KNIGHT.letter;
        }

        if (Bitboard.isBitSet(getWhiteBishops(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_BISHOP.symbol + ANSI_RESET : Piece.WHITE_BISHOP.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_BISHOP.letter + ANSI_RESET : Piece.WHITE_BISHOP.letter;
        }

        if (Bitboard.isBitSet(getWhiteRooks(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_ROOK.symbol + ANSI_RESET : Piece.WHITE_ROOK.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_ROOK.letter + ANSI_RESET : Piece.WHITE_ROOK.letter;
        }

        if (Bitboard.isBitSet(getWhiteQueens(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_QUEEN.symbol + ANSI_RESET : Piece.WHITE_QUEEN.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_QUEEN.letter + ANSI_RESET : Piece.WHITE_QUEEN.letter;
        }

        if (Bitboard.isBitSet(getWhiteKing(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_WHITE + Piece.WHITE_KING.symbol + ANSI_RESET : Piece.WHITE_KING.symbol
                    : colored ? PIECE_WHITE + Piece.WHITE_KING.letter + ANSI_RESET : Piece.WHITE_KING.letter;
        }

        if (Bitboard.isBitSet(getBlackPawns(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_PAWN.symbol + ANSI_RESET : Piece.BLACK_PAWN.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_PAWN.letter + ANSI_RESET : Piece.BLACK_PAWN.letter;
        }

        if (Bitboard.isBitSet(getBlackKnights(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_KNIGHT.symbol + ANSI_RESET : Piece.BLACK_KNIGHT.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_KNIGHT.letter + ANSI_RESET : Piece.BLACK_KNIGHT.letter;
        }

        if (Bitboard.isBitSet(getBlackBishops(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_BISHOP.symbol + ANSI_RESET : Piece.BLACK_BISHOP.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_BISHOP.letter + ANSI_RESET : Piece.BLACK_BISHOP.letter;
        }

        if (Bitboard.isBitSet(getBlackRooks(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_ROOK.symbol + ANSI_RESET : Piece.BLACK_ROOK.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_ROOK.letter + ANSI_RESET : Piece.BLACK_ROOK.letter;
        }

        if (Bitboard.isBitSet(getBlackQueens(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_QUEEN.symbol + ANSI_RESET : Piece.BLACK_QUEEN.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_QUEEN.letter + ANSI_RESET : Piece.BLACK_QUEEN.letter;
        }

        if (Bitboard.isBitSet(getBlackKing(), file, rank)) {
            return showUnicodeSymbols ?
                      colored ? PIECE_BLACK + Piece.BLACK_KING.symbol  + ANSI_RESET : Piece.BLACK_KING.symbol
                    : colored ? PIECE_BLACK + Piece.BLACK_KING.letter + ANSI_RESET : Piece.BLACK_KING.letter;
        }

        return "";
    }
}
