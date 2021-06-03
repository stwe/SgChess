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
        assertEquals(Piece.NO_PIECE, boardStart.getPieceFrom(Bitboard.BitIndex.E4_IDX));
        assertEquals(Piece.WHITE_PAWN, boardStart.getPieceFrom(Bitboard.BitIndex.E2_IDX));
        assertEquals(Piece.BLACK_QUEEN, boardStart.getPieceFrom(Bitboard.BitIndex.D8_IDX));
    }

    @Test
    void isQueenSideCastlingAllowed() {
        var boardStart = new Board();
        assertTrue(boardStart.isQueenSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(boardStart.isQueenSideCastlingAllowed(Board.Color.BLACK));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, boardStart.getCastlingRights());

        var board = new Board("rnbq1bnr/pppk1ppp/8/3pp3/4PP2/3P4/PPP3PP/RNBQKBNR w KQ - 0 1");
        assertTrue(board.isQueenSideCastlingAllowed(Board.Color.WHITE));
        assertFalse(board.isQueenSideCastlingAllowed(Board.Color.BLACK));
        assertEquals(
                Bitboard.WHITE_KING_CASTLE_KING_SIDE |
                        Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE,
                board.getCastlingRights()
        );
    }

    @Test
    void isKingSideCastlingAllowed() {
        var boardStart = new Board();
        assertTrue(boardStart.isKingSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(boardStart.isKingSideCastlingAllowed(Board.Color.BLACK));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, boardStart.getCastlingRights());

        var board = new Board("rnbqkbnr/pp4pp/3ppp2/2p5/5PP1/3BP2N/PPPP3P/RNBQ1K1R w Qkq - 0 1");
        assertFalse(board.isKingSideCastlingAllowed(Board.Color.WHITE));
        assertTrue(board.isKingSideCastlingAllowed(Board.Color.BLACK));
        assertEquals(
                Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE |
                        Bitboard.BLACK_KING_CASTLE_KING_SIDE |
                        Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE,
                board.getCastlingRights()
        );
    }

    @Test
    void makeCastlingMoveWhite() {
        var board = new Board("r3k2r/p3p2p/8/8/8/8/P3P2P/R3K2R w KQkq - 0 1");

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        // start
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());

        // white king castle king side
        board.makeMove(moves.get(15));
        var rest = Bitboard.BLACK_KING_CASTLE_KING_SIDE | Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(15));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());

        // white king castle queen side
        board.makeMove(moves.get(16));
        rest = Bitboard.BLACK_KING_CASTLE_KING_SIDE | Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(16));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());
    }

    @Test
    void makeCastlingMoveBlack() {
        var board = new Board("r3k2r/p3p2p/8/8/8/8/P3P2P/R3K2R b KQkq - 0 1");

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        // start
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());

        // black king castle king side
        board.makeMove(moves.get(15));
        var rest = Bitboard.WHITE_KING_CASTLE_KING_SIDE | Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(15));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());

        // black queen castle queen side
        board.makeMove(moves.get(16));
        rest = Bitboard.WHITE_KING_CASTLE_KING_SIDE | Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(16));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());
    }

    @Test
    void makePromotionCaptureMoveWhite() {
        // f7e8=queen
        var board = new Board("k3n3/5P2/8/8/8/8/8/K7 w - - 0 1");

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        board.makeMove(moves.get(7));
        assertEquals(0, board.getBlackKnights());
        assertEquals(Piece.WHITE_QUEEN, board.getPieceFrom(Bitboard.BitIndex.E8_IDX));

        board.undoMove(moves.get(7));
        assertEquals(0, board.getWhiteQueens());
        assertEquals(Piece.BLACK_KNIGHT, board.getPieceFrom(Bitboard.BitIndex.E8_IDX));
    }

    @Test
    void makePromotionCaptureMoveBlack() {
        // d2c1=queen
        var board = new Board("k7/8/8/8/8/8/3p4/2N4K b - - 0 1");

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        board.makeMove(moves.get(7));
        assertEquals(0, board.getWhiteKnights());
        assertEquals(Piece.BLACK_QUEEN, board.getPieceFrom(Bitboard.BitIndex.C1_IDX));

        board.undoMove(moves.get(7));
        assertEquals(0, board.getBlackQueens());
        assertEquals(Piece.WHITE_KNIGHT, board.getPieceFrom(Bitboard.BitIndex.C1_IDX));
    }

    @Test
    void perftTest() {
        // start position, depth 3
        var boardStart = new Board();
        boardStart.perftTest(3);
        assertEquals(8902, boardStart.nodes);
        assertEquals(34, boardStart.captures);
        assertEquals(12, boardStart.checks);

        // wiki position 2, also known as Kiwipete, depth 1
        var board2 = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0");
        board2.perftTest(1);
        assertEquals(48, board2.nodes);
        assertEquals(8, board2.captures);
        //assertEquals(0, board2.checks);

        // wiki position 3, depth 2
        var board3 = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");
        board3.perftTest(2);
        assertEquals(191, board3.nodes);
        assertEquals(14, board3.captures);
        assertEquals(10, board3.checks);
    }
}
