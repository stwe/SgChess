/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitboardTest {

    @Test
    void getSquareBitboardFromBitIndex() {
        assertEquals(Bitboard.A1, Bitboard.getSquareBitboardFromBitIndex(Bitboard.BitIndex.A1_IDX));
        assertEquals(Bitboard.H8, Bitboard.getSquareBitboardFromBitIndex(Bitboard.BitIndex.H8_IDX));
    }

    @Test
    void isBitSet() {
        // use the start position
        var board = new Board();

        // check the white king on E1
        assertTrue(Bitboard.isBitSet(board.getWhiteKing(), Bitboard.File.FILE_E, Bitboard.Rank.RANK_1));
        assertTrue(Bitboard.isBitSet(board.getWhiteKing(), Bitboard.BitIndex.E1_IDX));

        // check the black pawn on G7
        assertTrue(Bitboard.isBitSet(board.getBlackPawns(), Bitboard.File.FILE_G, Bitboard.Rank.RANK_7));
        assertTrue(Bitboard.isBitSet(board.getBlackPawns(), Bitboard.BitIndex.G7_IDX));

        // check empty square on B4
        assertFalse(Bitboard.isBitSet(board.getAllPieces(), Bitboard.File.FILE_B, Bitboard.Rank.RANK_4));
        assertFalse(Bitboard.isBitSet(board.getAllPieces(), Bitboard.BitIndex.B4_IDX));
    }

    @Test
    void getBitIndexByFileAndRank() {
        assertEquals(Bitboard.BitIndex.A1_IDX, Bitboard.getBitIndexByFileAndRank(Bitboard.File.FILE_A, Bitboard.Rank.RANK_1));
        assertEquals(Bitboard.BitIndex.E4_IDX, Bitboard.getBitIndexByFileAndRank(Bitboard.File.FILE_E, Bitboard.Rank.RANK_4));
        assertEquals(Bitboard.BitIndex.H8_IDX, Bitboard.getBitIndexByFileAndRank(Bitboard.File.FILE_H, Bitboard.Rank.RANK_8));
    }

    @Test
    void getLsb() {
        assertEquals(0, Bitboard.getLsb(1L).ordinal());
        assertEquals(2, Bitboard.getLsb(4L).ordinal());
        assertEquals(10, Bitboard.getLsb(3072L).ordinal());

        assertEquals(64, Bitboard.getLsb(0L).ordinal()); // no one-bits = 64 (NO_SQUARE)

        // use the start position
        var board = new Board();
        assertEquals(4, Bitboard.getLsb(board.getWhiteKing()).ordinal());
        assertEquals(Bitboard.BitIndex.D1_IDX, Bitboard.getLsb(board.getWhiteQueens()));
    }

    @Test
    void bitCount() {
        // use the start position
        var board = new Board();
        assertEquals(32, Bitboard.bitCount(board.getAllPieces()));
    }
}
