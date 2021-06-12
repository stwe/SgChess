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

    NO_PIECE(0, 0, new int[2][64]),
    PAWN(1, 100, EvaluationTables.PAWN_EVAL_TABLE),
    KNIGHT(2, 310, EvaluationTables.KNIGHT_EVAL_TABLE),
    BISHOP(3, 325, EvaluationTables.BISHOP_EVAL_TABLE),
    ROOK(4, 500, EvaluationTables.ROOK_EVAL_TABLE),
    QUEEN(5, 900, EvaluationTables.QUEEN_EVAL_TABLE),
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
     * If an invalid color is passed, -1 is returned.
     *
     * @param pieceType The {@link PieceType}.
     * @param color {@link Board.Color}.
     *
     * @return An index or -1 if an invalid color was passed.
     */
    public static int getBitboardNumber(PieceType pieceType, Board.Color color) {
        if (color == Board.Color.NONE) {
            return -1;
        }

        return (pieceType.value + color.value * 6) - 1;
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
