/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTypeTest {

    @Test
    void getBitboardNumber() {
        // piece = bitboard number
        // wp = 0, wn = 1, wb = 2, wr = 3, wq = 4, wk = 5
        // bp = 6, bn = 7, bb = 8, br = 9, bq = 10, bk = 11

        assertEquals(0, PieceType.getBitboardNumber(PieceType.PAWN, Board.Color.WHITE));
        assertEquals(1, PieceType.getBitboardNumber(PieceType.KNIGHT, Board.Color.WHITE));
        assertEquals(2, PieceType.getBitboardNumber(PieceType.BISHOP, Board.Color.WHITE));
        assertEquals(3, PieceType.getBitboardNumber(PieceType.ROOK, Board.Color.WHITE));
        assertEquals(4, PieceType.getBitboardNumber(PieceType.QUEEN, Board.Color.WHITE));
        assertEquals(5, PieceType.getBitboardNumber(PieceType.KING, Board.Color.WHITE));

        assertEquals(0, PieceType.getBitboardNumber(1, 0));
        assertEquals(1, PieceType.getBitboardNumber(2, 0));
        assertEquals(2, PieceType.getBitboardNumber(3, 0));
        assertEquals(3, PieceType.getBitboardNumber(4, 0));
        assertEquals(4, PieceType.getBitboardNumber(5, 0));
        assertEquals(5, PieceType.getBitboardNumber(6, 0));

        assertEquals(6, PieceType.getBitboardNumber(PieceType.PAWN, Board.Color.BLACK));
        assertEquals(7, PieceType.getBitboardNumber(PieceType.KNIGHT, Board.Color.BLACK));
        assertEquals(8, PieceType.getBitboardNumber(PieceType.BISHOP, Board.Color.BLACK));
        assertEquals(9, PieceType.getBitboardNumber(PieceType.ROOK, Board.Color.BLACK));
        assertEquals(10, PieceType.getBitboardNumber(PieceType.QUEEN, Board.Color.BLACK));
        assertEquals(11, PieceType.getBitboardNumber(PieceType.KING, Board.Color.BLACK));

        assertEquals(6, PieceType.getBitboardNumber(1, 1));
        assertEquals(7, PieceType.getBitboardNumber(2, 1));
        assertEquals(8, PieceType.getBitboardNumber(3, 1));
        assertEquals(9, PieceType.getBitboardNumber(4, 1));
        assertEquals(10, PieceType.getBitboardNumber(5, 1));
        assertEquals(11, PieceType.getBitboardNumber(6, 1));

        assertEquals(-1, PieceType.getBitboardNumber(PieceType.KING, Board.Color.NONE));
        assertEquals(-1, PieceType.getBitboardNumber(PieceType.NO_PIECE, Board.Color.WHITE));
    }
}
