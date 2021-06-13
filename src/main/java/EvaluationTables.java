/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Represents Piece-Square Tables.
 */
public class EvaluationTables {

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    // [0][64] = white, [1][64] = black

    public static final int[][] PAWN_EVAL_TABLE = {
            {
                0,   0,   0,   0,   0,   0,   0,   0,
              -10, -10, -10, -40, -40, -10, -10, -10,
              -10, -15, -15,   0,   0, -15, -15, -10,
              -15, -10,   0, +20, +20,   0, -10, -15,
              -20, -20,  +5, +40, +40,  +5, -20, -20,
              -20, -20, +15, +30, +30, +15, -20, -20,
              -20, -20, +10, +10, +10, +10, -20, -20,
                0,   0,   0,   0,   0,   0,   0,   0
            },
            {
                0,   0,   0,   0,   0,   0,   0,   0,
              -20, -20, +10, +10, +10, +10, -20, -20,
              -20, -20, +15, +30, +30, +15, -20, -20,
              -20, -20,  +5, +40, +40,  +5, -20, -20,
              -15, -10,   0, +20, +20,   0, -10, -15,
              -10, -15, -15,   0,   0, -15, -15, -10,
              -10, -10, -10, -40, -40, -10, -10, -10,
                0,   0,   0,   0,   0,   0,   0,   0
            }
    };

    public static final int[][] KNIGHT_EVAL_TABLE = {
            {
              -70, -20, -30, -25, -25, -30, -20, -70,
              -40, -20,   0,   5,   5,   0, -20, -40,
              -30,   5,  10,  20,  20,  10,   5, -30,
              -30,   0,  15,  15,  15,  15,   0, -30,
              -30,   5,  15,  20,  20,  15,   5, -30,
              -30,   0,  10,  15,  15,  10,   0, -30,
              -40, -20, -15, -10, -10, -15, -20, -40,
              -60, -40, -35, -30, -30, -35, -40, -60
            },
            {
              -60, -40, -35, -30, -30, -35, -40, -60,
              -40, -20, -15, -10, -10, -15, -20, -40,
              -30,   0,  10,  15,  15,  10,   0, -30,
              -30,   5,  15,  20,  20,  15,   5, -30,
              -30,   0,  15,  15,  15,  15,   0, -30,
              -30,   5,  10,  20,  20,  10,   5, -30,
              -40, -20,   0,   5,   5,   0, -20, -40,
              -70, -20, -30, -25, -25, -30, -20, -70
            }
    };

    public static final int[][] BISHOP_EVAL_TABLE = {
            {
              -20, -10, -40, -10, -10, -40, -10, -20,
              -10,   5,   0,   0,   0,   0,   5, -10,
              -10,  10,  10,  10,  10,  10,  10, -10,
              -10,   0,  10,  10,  10,  10,   0, -10,
              -10,   5,   5,  10,  10,   5,   5, -10,
              -10,   0,   5,  10,  10,   5,   0, -10,
              -10,   0,   0,   0,   0,   0,   0, -10,
              -20, -10, -10, -10, -10, -10, -10, -20
            },
            {
              -20, -10, -10, -10, -10, -10, -10, -20,
              -10,   0,   0,   0,   0,   0,   0, -10,
              -10,   0,   5,  10,  10,   5,   0, -10,
              -10,   5,   5,  10,  10,   5,   5, -10,
              -10,   0,  10,  10,  10,  10,   0, -10,
              -10,  10,  10,  10,  10,  10,  10, -10,
              -10,   5,   0,   0,   0,   0,   5, -10,
              -20, -10, -40, -10, -10, -40, -10, -20
            }
    };

    public static final int[][] ROOK_EVAL_TABLE = {
            {
              -15, -10, -5,  5,  5, -5, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10, -5,  0,  0, -5, -10, -15
            },
            {
              -15, -10, -5,  0,  0, -5, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10,  0, 10, 10,  0, -10, -15,
              -15, -10, -5,  5,  5, -5, -10, -15
            }
    };

    public static final int[][] QUEEN_EVAL_TABLE = {
            {
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
            },
            {
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
            }
    };

    public static final int[][] KING_EVAL_TABLE = {
            {
                20,  30,  10,   0,   0,  10,  30,  20,
                20,  20,   0,   0,   0,   0,  20,  20,
               -10, -20, -20, -20, -20, -20, -20, -10,
               -20, -30, -30, -40, -40, -30, -30, -20,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
            },
            {
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -30, -40, -40, -50, -50, -40, -40, -30,
               -20, -30, -30, -40, -40, -30, -30, -20,
               -10, -20, -20, -20, -20, -20, -20, -10,
                20,  20,   0,   0,   0,   0,  20,  20,
                20,  30,  10,   0,   0,  10,  30,  20
            }
    };
}
