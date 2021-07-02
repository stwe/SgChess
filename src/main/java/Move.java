/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Represents a Move object.
 */
public class Move {

    //-------------------------------------------------
    // Flags
    //-------------------------------------------------

    /**
     * Move flags.
     */
    public enum MoveFlag {
        NORMAL,
        PROMOTION,
        EN_PASSANT,
        CASTLING,
        PAWN_START,
        CAPTURE,
        PROMOTION_CAPTURE
    }

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * A packed integer containing all of the move data.
     *
     * Currently 29 bits are used in total to store move information.
     * The format is as follows:
     *
     * <p>bit  0 -  5: <b>from</b> square (0 - 63)</p>
     * <p>bit  6 - 11: <b>to</b> square (0 - 63)</p>
     * <p>bit 12 - 14: <b>captured piece type</b> (0 - 6)</p>
     * <p>bit 15 - 17: <b>promoted piece type</b> (0 - 6)</p>
     * <p>bit 18 - 20: <b>special move flag</b> (0 - 6)</p>
     * <p>bit 21 - 24: <b>piece</b>(0 - 11)</p>
     * <p>bit 25 - 29: <b>previousCastlingRights</b> 4 bits</p>
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
     *                                                pawn start = 4,
     *                                                capture = 5
     *                                                promotion_capture = 6
     * 0000 0001 1110 0000 0000 0000 0000 0000 -> piece (4 bits)
     * 0001 1110 0000 0000 0000 0000 0000 0000 -> previous castling rights (4 bits)
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
     * Set the from square {@link Bitboard.BitIndex}.
     *
     * @param bitIndex A {@link Bitboard.BitIndex}.
     */
    public void setFrom(Bitboard.BitIndex bitIndex) {
        // clear first 6 bits
        move &= ~63;
        // mask on the first 6 bits an OR with bitIndex
        move |= (bitIndex.ordinal() & 63);
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
     * Set the target square {@link Bitboard.BitIndex}.
     *
     * @param bitIndex A {@link Bitboard.BitIndex}.
     */
    public void setTo(Bitboard.BitIndex bitIndex) {
        move &= ~4032;
        move |= (bitIndex.ordinal() & 63) << 6;
    }

    //-------------------------------------------------
    // Captured piece type
    //-------------------------------------------------

    /**
     * Get captured {@link PieceType} ordinal value.
     *
     * @return The captured {@link PieceType} ordinal value.
     */
    public int getCapturedPieceTypeValue() {
        return (move & 28672) >>> 12;
    }

    /**
     * Get the captured {@link PieceType} object.
     *
     * @return A {@link PieceType} object.
     */
    public PieceType getCapturedPieceType() {
        return PieceType.values()[getCapturedPieceTypeValue()];
    }

    /**
     * Set captured {@link PieceType}.
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
     * Get promoted {@link PieceType} ordinal value.
     *
     * @return The promoted {@link PieceType} ordinal value.
     */
    public int getPromotedPieceTypeValue() {
        return (move & 229376) >>> 15;
    }

    /**
     * Get the promoted {@link PieceType} object.
     *
     * @return A {@link PieceType} object.
     */
    public PieceType getPromotedPieceType() {
        return PieceType.values()[getPromotedPieceTypeValue()];
    }

    /**
     * Set promoted {@link PieceType}.
     *
     * @param pieceType {@link PieceType}.
     */
    public void setPromotedPieceType(PieceType pieceType) {
        if (pieceType == PieceType.KING) {
            return;
        }

        move &= ~229376;
        move |= (pieceType.value & 7) << 15;
    }

    //-------------------------------------------------
    // Move flag
    //-------------------------------------------------

    /**
     * Get {@link MoveFlag} ordinal value.
     *
     * @return A {@link MoveFlag} ordinal value.
     */
    public int getMoveFlagValue() {
        return (move & 1835008) >>> 18;
    }

    /**
     * Get the {@link MoveFlag} object.
     *
     * @return A {@link MoveFlag} object.
     */
    public MoveFlag getMoveFlag() {
        return MoveFlag.values()[getMoveFlagValue()];
    }

    /**
     * Set a {@link MoveFlag}.
     *
     * @param flag {@link MoveFlag}.
     */
    public void setMoveFlag(MoveFlag flag) {
        if (flag == MoveFlag.NORMAL) {
            return;
        }

        move &= ~1835008;
        move |= (flag.ordinal() & 7) << 18;
    }

    //-------------------------------------------------
    // Piece
    //-------------------------------------------------

    /**
     * Get the {@link Piece} ordinal value of this move.
     *
     * @return The ordinal value of the {@link Piece}.
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
     * Set the {@link Piece} of this move.
     *
     * @param piece {@link Piece}.
     */
    public void setPiece(Piece piece) {
        move &= ~31457280;
        move |= (piece.value & 15) << 21;
    }

    //-------------------------------------------------
    // Previous castling rights
    //-------------------------------------------------

    /**
     * Get the previous castling rights of this move.
     *
     * @return The previous castling rights.
     */
    public int getPreviousCastlingRights() {
        return (move & 503316480) >>> 25;
    }

    /**
     * Set the previous castling rights of this move.
     *
     * @param castlingRights A packed integer containing the previous castling rights. The order is 1111 = qkQK
     */
    public void setPreviousCastlingRights(int castlingRights) {
        move &= ~503316480;
        move |= (castlingRights & 15) << 25;
    }

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Move} object.
     *
     * @param piece The {@link Piece} of this move.
     * @param from The from square {@link Bitboard.BitIndex}.
     * @param to The target square {@link Bitboard.BitIndex}.
     */
    public Move(Piece piece, Bitboard.BitIndex from, Bitboard.BitIndex to) {
        setPiece(piece);
        setFrom(from);
        setTo(to);
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {

        /*
        NORMAL,
        PROMOTION,             => promoted piece type
        EN_PASSANT,            => captured piece type pawn
        CASTLING,
        PAWN_START,
        CAPTURE,               => captured piece type
        PROMOTION_CAPTURE      => promoted piece type, captured piece type
        */

        String promotedPiece = "";
        if (getMoveFlag() == MoveFlag.PROMOTION || getMoveFlag() == MoveFlag.PROMOTION_CAPTURE) {
            promotedPiece = " promoted piece type: " + getPromotedPieceType();
        }

        String capturedPiece = "";
        if (getMoveFlag() == MoveFlag.EN_PASSANT || getMoveFlag() == MoveFlag.CAPTURE || getMoveFlag() == MoveFlag.PROMOTION_CAPTURE) {
            capturedPiece = " captured piece type: " + getCapturedPieceType();
        }

        return "Piece: " + getPiece().moveLetter +
                " from: " + Bitboard.SQUARE_STRINGS[getFrom()] +
                " to: " + Bitboard.SQUARE_STRINGS[getTo()] +
                " flag: " + getMoveFlag() +
                promotedPiece +
                capturedPiece +
                " (score: " + score + ")";
    }
}
