/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getPieceFrom() {
        var boardStart = new Board();
        assertEquals(Piece.WHITE_PAWN, boardStart.getPieceFrom(Bitboard.BitIndex.E4_IDX));
    }

    @Test
    void isQueenSideCastlingAllowed() {
        var boardStart = new Board();
        assertTrue(boardStart.isQueenSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(boardStart.isQueenSideCastlingAllowed(Board.Color.BLACK));

        var board = new Board("rnbq1bnr/pppk1ppp/8/3pp3/4PP2/3P4/PPP3PP/RNBQKBNR w KQ - 0 1");
        assertTrue(board.isQueenSideCastlingAllowed(Board.Color.WHITE));
        assertFalse(board.isQueenSideCastlingAllowed(Board.Color.BLACK));
    }

    @Test
    void isKingSideCastlingAllowed() {
        var boardStart = new Board();
        assertTrue(boardStart.isKingSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(boardStart.isKingSideCastlingAllowed(Board.Color.BLACK));

        var board = new Board("rnbqkbnr/pp4pp/3ppp2/2p5/5PP1/3BP2N/PPPP3P/RNBQ1K1R w Qkq - 0 1");
        assertFalse(board.isKingSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(board.isKingSideCastlingAllowed(Board.Color.BLACK));
    }
}
