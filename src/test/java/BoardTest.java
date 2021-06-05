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
    void makePawnStartMoveWhite() {
        var board = new Board("k7/8/8/8/p7/8/1P6/7K w - - 0 1");

        // before pawn start move
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // b2b4 make white pawn start move
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        var mg0 = new MoveGenerator(board);
        mg0.generatePseudoLegalMoves();
        var startMoves = mg0.getPseudoLegalMoves();
        board.makeMove(startMoves.get(1));

        // after pawn start move
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // undo pawn start move
        board.undoMove(startMoves.get(1));
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());
    }

    @Test
    void makePawnStartMoveBlack() {
        var board = new Board("k7/6p1/8/7P/8/8/8/7K b - - 0 1");

        // before pawn start move
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // g7g5 make black pawn start move
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        var mg0 = new MoveGenerator(board);
        mg0.generatePseudoLegalMoves();
        var startMoves = mg0.getPseudoLegalMoves();
        board.makeMove(startMoves.get(1));

        // after pawn start move
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // undo pawn start move
        board.undoMove(startMoves.get(1));
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());
    }

    @Test
    void makeEpMoveWhite() {
        var board = new Board("k7/8/8/8/p7/8/1P6/7K w - - 0 1");
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // b2b4 make white pawn start move
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        var mg0 = new MoveGenerator(board);
        mg0.generatePseudoLegalMoves();
        var startMoves = mg0.getPseudoLegalMoves();
        board.makeMove(startMoves.get(1));

        // after pawn start move
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // ---------------------------------------------------------------

        // make a4b3 - black can now make an En Passant capture
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        var mg1 = new MoveGenerator(board);
        mg1.generatePseudoLegalMoves();
        var moves = mg1.getPseudoLegalMoves();
        var epMove = moves.get(0);
        assertEquals(Move.MoveFlag.EN_PASSANT, epMove.getMoveFlag());
        board.makeMove(epMove);

        // after capture white pawn
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getOldEpIndex());

        // undo capture (a4b3)
        board.undoMove(epMove);
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getEpIndex());       // same situation after pawn start
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex()); // same situation after pawn start

        // ---------------------------------------------------------------

        // make a4a3 instead En Passant capture
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        var mg2 = new MoveGenerator(board);
        mg2.generatePseudoLegalMoves();
        var otherMoves = mg2.getPseudoLegalMoves();
        assertEquals(Move.MoveFlag.NORMAL, otherMoves.get(1).getMoveFlag());
        board.makeMove(otherMoves.get(1));

        // after a4a3
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getOldEpIndex());

        // undo a4a3
        board.undoMove(otherMoves.get(1));
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.B3_IDX, board.getEpIndex());       // same situation after pawn start
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex()); // same situation after pawn start
    }

    @Test
    void makeEpMoveBlack() {

    }

    @Test
    void perftTest() {
        // https://www.chessprogramming.org/Perft_Results

        /*
        var board = new Board("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1");
        var depth = 6;
        board.perftTest(depth, false);
        assertEquals(1134888, board.nodes);
        */

        // start position
        /*
        var boardStart = new Board();
        var depth = 6;
        boardStart.perftTest(depth);

        if (depth == 1) {
            assertEquals(20, boardStart.nodes);
            assertEquals(0, boardStart.captures[0]);
            assertEquals(0, boardStart.enPassants[0]);
            assertEquals(0, boardStart.castles[0]);
            assertEquals(0, boardStart.checks[0]);
        }

        if (depth == 2) {
            assertEquals(400, boardStart.nodes);
            assertEquals(0, boardStart.captures[0]);
            assertEquals(0, boardStart.enPassants[0]);
            assertEquals(0, boardStart.castles[0]);
            assertEquals(0, boardStart.checks[0]);
        }

        if (depth == 3) {
            assertEquals(8902, boardStart.nodes);
            assertEquals(34, boardStart.captures[0]);
            assertEquals(0, boardStart.enPassants[0]);
            assertEquals(0, boardStart.castles[0]);
            assertEquals(12, boardStart.checks[0]);
        }

        // todo: show checkmates

        if (depth == 4) {
            assertEquals(197281, boardStart.nodes);
            assertEquals(1576, boardStart.captures[0]);
            assertEquals(0, boardStart.enPassants[0]);
            assertEquals(0, boardStart.castles[0]);
            assertEquals(469, boardStart.checks[0]);
        }

        if (depth == 6) {
            assertEquals(119060324, boardStart.nodes);
            //assertEquals(2812008, boardStart.captures[0]);
            //assertEquals(5248, boardStart.enPassants[0]);
            //assertEquals(0, boardStart.castles[0]);
            //assertEquals(809099, boardStart.checks[0]);
        }
        */

        // wiki position 2, also known as Kiwipete
        /*
        var board2 = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0");
        var depth = 2;
        board2.perftTest(depth, false);

        if (depth == 1) {
            assertEquals(48, board2.nodes);
            assertEquals(8, board2.captures[0]);
            assertEquals(0, board2.enPassants[0]);
            assertEquals(2, board2.castles[0]);
            assertEquals(0, board2.checks[0]);
        }

        if (depth == 2) {
            assertEquals(2039, board2.nodes);
            assertEquals(351, board2.captures[0]);
            assertEquals(1, board2.enPassants[0]);
            assertEquals(91, board2.castles[0]);
            assertEquals(3, board2.checks[0]);
        }
        */

        // wiki position 3
        var board3 = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");
        var depth = 3;
        board3.perftTest(depth, false);

        if (depth == 1) {
            assertEquals(14, board3.nodes);
            assertEquals(1, board3.captures[0]);
            assertEquals(0, board3.enPassants[0]);
            assertEquals(0, board3.castles[0]);
            assertEquals(2, board3.checks[0]);
        }

        if (depth == 2) {
            assertEquals(191, board3.nodes);
            assertEquals(14, board3.captures[0]);
            assertEquals(0, board3.enPassants[0]);
            assertEquals(0, board3.castles[0]);
            assertEquals(10, board3.checks[0]);
        }

        if (depth == 3) {
            assertEquals(2812, board3.nodes);
            assertEquals(209, board3.captures[0]);
            assertEquals(2, board3.enPassants[0]);
            assertEquals(0, board3.castles[0]);
            assertEquals(267, board3.checks[0]);
        }
    }
}
