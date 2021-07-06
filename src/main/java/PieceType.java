/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Represents a PieceType.
 */
public enum PieceType {

    //-------------------------------------------------
    // Piece types
    //-------------------------------------------------

    /*
    material score

    ♙ =   100 = ♙
    ♘ =   300 = ♙ * 3
    ♗ =   350 = ♙ * 3 + ♙ * 0.5
    ♖ =   500 = ♙ * 5
    ♕ =  1000 = ♙ * 10
    ♔ = 10000 = ♙ * 100
    */

    NO_PIECE(0, 0, new int[2][64]),
    PAWN(1, 100, EvaluationTables.PAWN_EVAL_TABLE),
    KNIGHT(2, 300, EvaluationTables.KNIGHT_EVAL_TABLE),
    BISHOP(3, 350, EvaluationTables.BISHOP_EVAL_TABLE),
    ROOK(4, 500, EvaluationTables.ROOK_EVAL_TABLE),
    QUEEN(5, 1000, EvaluationTables.QUEEN_EVAL_TABLE),
    KING(6, 10000, EvaluationTables.KING_EVAL_TABLE);

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The ordinal value.
     */
    public final int value;

    /**
     * The material score.
     */
    public final int materialScore;

    /**
     * The evalation tables for both colors.
     */
    public final int[][] evaluationTables;

    /**
     * To get the {@link PieceType} by ordinal value.
     */
    public final PieceType[] values = new PieceType[7];

    //-------------------------------------------------
    // Util
    //-------------------------------------------------

    /**
     * Returns the bitboard index of a white or black {@link PieceType}.
     * If an invalid piece type or color is passed, -1 is returned.
     *
     * @param pieceType The {@link PieceType}.
     * @param color The {@link Board.Color}.
     *
     * @return An index or -1 if an invalid type was passed.
     */
    public static int getBitboardNumber(PieceType pieceType, Board.Color color) {
        return getBitboardNumber(pieceType.value, color.value);
    }

    /**
     * Returns the bitboard index of a white or black {@link PieceType} value.
     * If an invalid piece type or color value is passed, -1 is returned.
     *
     * @param pieceType The {@link PieceType} value.
     * @param color The {@link Board.Color} value.
     *
     * @return An index or -1 if an invalid value was passed.
     */
    public static int getBitboardNumber(int pieceType, int color) {
        if (pieceType == PieceType.NO_PIECE.value || color == Board.Color.NONE.value) {
            return -1;
        }

        return (pieceType + color * 6) - 1;
    }

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link PieceType} enum.
     *
     * @param value {@link #value}.
     * @param materialScore {@link #materialScore}
     * @param evaluationTables {@link #evaluationTables}
     */
    PieceType(int value, int materialScore, int[][] evaluationTables) {
        this.value = value;
        this.materialScore = materialScore;
        this.evaluationTables = evaluationTables;

        this.values[value] = this;
    }
}
