/**
 * Represents a Move object.
 */
public class Move {

    //-------------------------------------------------
    // Flags
    //-------------------------------------------------

    /**
     * Flags that indicate special moves.
     */
    public enum SpecialMoveFlag {
        NORMAL(0),
        PROMOTION(1),
        ENPASSANT(2),
        CASTLING(3),
        PAWN_START(4);

        public int value;

        SpecialMoveFlag(int value) {
            this.value = value;
        }
    }

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * A packed integer containing all of the move data.
     *
     * Currently 25 bits are used in total to store move information.
     * The format is as follows:
     *
     * <p>bit  0 -  5: <b>from</b> square (0 - 63)</p>
     * <p>bit  6 - 11: <b>to</b> square (0 - 63)</p>
     * <p>bit 12 - 14: <b>captured piece</b> type (0 - 6)</p>
     * <p>bit 15 - 17: <b>promoted piece</b> type (0 - 6)</p>
     * <p>bit 18 - 20: <b>special move flag</b> (0 - 4)</p>
     * <p>bit 21 - 24: <b>piece</b>(0 - 11)</p>
     * <p></p>
     * <pre>
     * 0000 0000 0000 0000 0000 0000 0011 1111 -> from (6 bits)
     * 0000 0000 0000 0000 0000 1111 1100 0000 -> to (6 bits)
     * 0000 0000 0000 0000 0111 0000 0000 0000 -> captured piece type (3 bits)
     * 0000 0000 0000 0011 1000 0000 0000 0000 -> promoted piece type (3 bits)
     * 0000 0000 0001 1100 0000 0000 0000 0000 -> special move flag (3 bits):
     *                                                normal = 0,
     *                                                promotion = 1,
     *                                                en passant = 2,
     *                                                castling = 3,
     *                                                pawn start = 4
     * 0000 0001 1110 0000 0000 0000 0000 0000 -> piece (4 bits)
     * </pre>
     */
    private int move;

    /**
     * The score of this move.
     */
    private int score;

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    /**
     * Get {@link #move}.
     *
     * @return {@link #move}.
     */
    public int getMove() {
        return move;
    }

    /**
     * Get {@link #score}.
     *
     * @return {@link #score}.
     */
    public int getScore() {
        return score;
    }

    //-------------------------------------------------
    // Setter
    //-------------------------------------------------

    /**
     * Set {@link #score}.
     *
     * @param score A {@link #score} value.
     */
    public void setScore(int score) {
        this.score = score;
    }

    //-------------------------------------------------
    // From
    //-------------------------------------------------

    /**
     * Get the from square bit index.
     *
     * @return A bit index (0 - 63).
     */
    public int getFrom() {
        // 63 = 111111
        return move & 63;
    }

    /**
     * Set the from square bit index.
     *
     * @param bitIndex A bit index (0 - 63).
     */
    public void setFrom(int bitIndex) {
        // clear first 6 bits
        move &= ~63;
        // mask on the first 6 bits an OR with bitIndex
        move |= (bitIndex & 63);
    }

    //-------------------------------------------------
    // To
    //-------------------------------------------------

    /**
     * Get the target square bit index.
     *
     * @return A bit index (0 - 63).
     */
    public int getTo() {
        // 4032 = 111111000000
        return (move & 4032) >>> 6;
    }

    /**
     * Set the target square bit index.
     *
     * @param bitIndex A bit index (0 - 63).
     */
    public void setTo(int bitIndex) {
        move &= ~4032;
        move |= (bitIndex & 63) << 6;
    }

    //-------------------------------------------------
    // Captured piece type
    //-------------------------------------------------

    /**
     * Get captured piece type.
     *
     * @return The captured piece type.
     */
    public int getCapturedPieceType() {
        return (move & 28672) >>> 12;
    }

    /**
     * Set captured piece type.
     *
     * @param pieceType {@link PieceType}.
     */
    public void setCapturedPieceType(PieceType pieceType) {
        move &= ~28672;
        move |= (pieceType.value & 7) << 12;
    }

    //-------------------------------------------------
    // Promoted piece type
    //-------------------------------------------------

    /**
     * Get promoted piece type.
     *
     * @return The promoted piece type.
     */
    public int getPromotedPieceType() {
        return (move & 229376) >>> 15;
    }

    /**
     * Set promoted piece type.
     *
     * @param pieceType {@link PieceType}.
     */
    public void setPromotedPieceType(PieceType pieceType) {
        if (pieceType == PieceType.KING) {
            throw new RuntimeException("Invalid piece type given.");
        }

        move &= ~229376;
        move |= (pieceType.value & 7) << 15;
    }

    //-------------------------------------------------
    // Special move flag
    //-------------------------------------------------

    // todo
    // isDoublePawnPush
    // isEnPassant
    // isCastling

    /**
     * Get special move flag.
     *
     * @return The special move flag.
     */
    public int getSpecialMoveFlag() {
        return (move & 1835008) >>> 18;
    }

    /**
     * Set special move flag.
     *
     * @param flag {@link SpecialMoveFlag}.
     */
    public void setSpecialMoveFlag(SpecialMoveFlag flag) {
        if (flag == SpecialMoveFlag.NORMAL) {
            return;
        }

        move &= ~1835008;
        move |= (flag.value & 7) << 18;
    }

    //-------------------------------------------------
    // Piece
    //-------------------------------------------------

    /**
     * Get the piece ordinal value of this move.
     *
     * @return The ordinal value of the piece.
     */
    public int getPieceValue() {
        return (move & 31457280) >>> 21;
    }

    /**
     * Get the {@link Piece} object of this move.
     *
     * @return A {@link Piece} object.
     */
    public Piece getPiece() {
        return Piece.values()[getPieceValue()];
    }

    /**
     * Set the piece of this move.
     *
     * @param piece {@link Piece}.
     */
    public void setPiece(Piece piece) {
        move &= ~31457280;
        move |= (piece.value & 15) << 21;
    }

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The piece of this move.
     * @param from The from square bit index.
     * @param to The target square bit index.
     */
    public Move(Piece piece, int from, int to) {
        setPiece(piece);
        setFrom(from);
        setTo(to);
    }

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The piece of this move.
     * @param from The from square bit index.
     * @param to The target square bit index.
     * @param capturedPieceType The captured piece type.
     */
    public Move(Piece piece, int from, int to, PieceType capturedPieceType) {
        this(piece, from, to);
        setCapturedPieceType(capturedPieceType);
    }

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The piece of this move.
     * @param from The from square bit index.
     * @param to The target square bit index.
     * @param flag The special move flag.
     */
    public Move(Piece piece, int from, int to, SpecialMoveFlag flag) {
        this(piece, from, to);
        setSpecialMoveFlag(flag);
    }

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The piece of this move.
     * @param from The from square bit index.
     * @param to The target square bit index.
     * @param promotedPieceType The promoted piece type.
     * @param flag The special move flag.
     */
    public Move(Piece piece, int from, int to, PieceType promotedPieceType, SpecialMoveFlag flag) {
        this(piece, from, to, flag);
        setPromotedPieceType(promotedPieceType);
    }

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The piece of this move.
     * @param from The from square bit index.
     * @param to The target square bit index.
     * @param capturedPieceType The captured piece type.
     * @param promotedPieceType The promoted piece type.
     * @param flag The special move flag.
     */
    public Move(Piece piece, int from, int to, PieceType capturedPieceType, PieceType promotedPieceType, SpecialMoveFlag flag) {
        this(piece, from, to, promotedPieceType, flag);
        setCapturedPieceType(capturedPieceType);
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {
        return "Piece: " + getPiece().moveLetter +
                " from: " + Bitboard.SQUARE_STRINGS[getFrom()] +
                " to: " + Bitboard.SQUARE_STRINGS[getTo()];
    }
}
