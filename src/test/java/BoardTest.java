/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void parseMove() {
        var b0 = new Board();
        var m0 = b0.parseMove("e2e4");
        assertEquals(Move.MoveFlag.PAWN_START, m0.getMoveFlag());
        assertTrue(b0.makeMove(m0));

        var b1 = new Board("k7/4P3/1p6/8/8/8/8/K7 w - - 0 1");
        var m1 = b1.parseMove("e7e8q");
        assertEquals(Move.MoveFlag.PROMOTION, m1.getMoveFlag());
        assertEquals(PieceType.QUEEN, m1.getPromotedPieceType());
        assertTrue(b1.makeMove(m1));

        var b2 = new Board("7k/8/8/1P6/8/8/5p2/K3N1R1 b - - 0 1");
        var m2 = b2.parseMove("f2e1b");
        assertEquals(Move.MoveFlag.PROMOTION_CAPTURE, m2.getMoveFlag());
        assertEquals(PieceType.BISHOP, m2.getPromotedPieceType());
        assertTrue(b2.makeMove(m2));

        var m3 = b2.parseMove("f2e1B");
        assertNull(m3);
    }

    @Test
    void isNeighborAnEnemyPawn() {
        var board = new Board("k7/8/8/8/1P1pPp2/p1P5/8/7K w - - 0 1");

        // check white pawns
        assertTrue(board.isNeighborAnEnemyPawn(Bitboard.BitIndex.E4_IDX.ordinal(), Board.Color.WHITE));
        assertFalse(board.isNeighborAnEnemyPawn(Bitboard.BitIndex.B4_IDX.ordinal(), Board.Color.WHITE));

        // check black pawns
        assertFalse(board.isNeighborAnEnemyPawn(Bitboard.BitIndex.A3_IDX.ordinal(), Board.Color.BLACK));
        assertTrue(board.isNeighborAnEnemyPawn(Bitboard.BitIndex.D4_IDX.ordinal(), Board.Color.BLACK));
        assertTrue(board.isNeighborAnEnemyPawn(Bitboard.BitIndex.F4_IDX.ordinal(), Board.Color.BLACK));
    }

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
        var startKey = board.getZkey();

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
        assertEquals(startKey, board.getZkey());

        // white king castle queen side
        board.makeMove(moves.get(16));
        rest = Bitboard.BLACK_KING_CASTLE_KING_SIDE | Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(16));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());
        assertEquals(startKey, board.getZkey());
    }

    @Test
    void makeCastlingMoveBlack() {
        var board = new Board("r3k2r/p3p2p/8/8/8/8/P3P2P/R3K2R b KQkq - 0 1");
        var startKey = board.getZkey();

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
        assertEquals(startKey, board.getZkey());

        // black queen castle queen side
        board.makeMove(moves.get(16));
        rest = Bitboard.WHITE_KING_CASTLE_KING_SIDE | Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE;
        assertEquals(rest, board.getCastlingRights());

        // undo castling
        board.undoMove(moves.get(16));
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, board.getCastlingRights());
        assertEquals(startKey, board.getZkey());
    }

    @Test
    void makePromotionCaptureMoveWhite() {
        // f7e8=queen
        var board = new Board("k3n3/5P2/8/8/8/8/8/K7 w - - 0 1");
        var startKey = board.getZkey();

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        board.makeMove(moves.get(7));
        assertEquals(0, board.getBlackKnights());
        assertEquals(Piece.WHITE_QUEEN, board.getPieceFrom(Bitboard.BitIndex.E8_IDX));

        board.undoMove(moves.get(7));
        assertEquals(0, board.getWhiteQueens());
        assertEquals(Piece.BLACK_KNIGHT, board.getPieceFrom(Bitboard.BitIndex.E8_IDX));
        assertEquals(startKey, board.getZkey());
    }

    @Test
    void makePromotionCaptureMoveBlack() {
        // d2c1=queen
        var board = new Board("k7/8/8/8/8/8/3p4/2N4K b - - 0 1");
        var startKey = board.getZkey();

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        board.makeMove(moves.get(7));
        assertEquals(0, board.getWhiteKnights());
        assertEquals(Piece.BLACK_QUEEN, board.getPieceFrom(Bitboard.BitIndex.C1_IDX));

        board.undoMove(moves.get(7));
        assertEquals(0, board.getBlackQueens());
        assertEquals(Piece.WHITE_KNIGHT, board.getPieceFrom(Bitboard.BitIndex.C1_IDX));
        assertEquals(startKey, board.getZkey());
    }

    @Test
    void makePawnStartMoveWhite() {
        var board = new Board("k7/8/8/8/p7/8/1P6/7K w - - 0 1");
        var startKey = board.getZkey();

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
        assertEquals(startKey, board.getZkey());
    }

    @Test
    void makePawnStartMoveBlack() {
        var board = new Board("k7/6p1/8/7P/8/8/8/7K b - - 0 1");
        var startKey = board.getZkey();

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
        assertEquals(startKey, board.getZkey());
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
        var pawnStartKey = board.getZkey();

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
        assertEquals(pawnStartKey, board.getZkey());

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
        assertEquals(pawnStartKey, board.getZkey());
    }

    @Test
    void makeEpMoveBlack() {
        var board = new Board("k7/2P3p1/8/7P/8/8/8/7K b - - 0 1");
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // g7g5 make black pawn start move
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        var mg0 = new MoveGenerator(board);
        mg0.generatePseudoLegalMoves();
        var startMoves = mg0.getPseudoLegalMoves();
        board.makeMove(startMoves.get(1));
        var pawnStartKey = board.getZkey();

        // after pawn start move
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex());

        // ---------------------------------------------------------------

        // make h5g6 - white can now make an En Passant capture
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        var mg1 = new MoveGenerator(board);
        mg1.generatePseudoLegalMoves();
        var moves = mg1.getPseudoLegalMoves();
        var epMove = moves.get(0);
        assertEquals(Move.MoveFlag.EN_PASSANT, epMove.getMoveFlag());
        board.makeMove(epMove);

        // after capture black pawn
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getOldEpIndex());

        // undo capture (h5g6)
        board.undoMove(epMove);
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getEpIndex());       // same situation after pawn start
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex()); // same situation after pawn start
        assertEquals(pawnStartKey, board.getZkey());

        // ---------------------------------------------------------------

        // make c7c8 (promotion move) instead En Passant capture
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        var mg2 = new MoveGenerator(board);
        mg2.generatePseudoLegalMoves();
        var otherMoves = mg2.getPseudoLegalMoves();
        assertEquals(Move.MoveFlag.PROMOTION, otherMoves.get(5).getMoveFlag());
        board.makeMove(otherMoves.get(5));

        // after c7c8
        assertEquals(Board.Color.BLACK, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getEpIndex());
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getOldEpIndex());

        // undo c7c8 promotion move
        board.undoMove(otherMoves.get(5));
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        assertEquals(Bitboard.BitIndex.G6_IDX, board.getEpIndex());       // same situation after pawn start
        assertEquals(Bitboard.BitIndex.NO_SQUARE, board.getOldEpIndex()); // same situation after pawn start
        assertEquals(pawnStartKey, board.getZkey());
    }

    void runStartPosition(int depth) {
        var board = new Board();
        board.perftTest(depth);

        if (depth == 1) {
            assertEquals(20, board.nodes);
            assertEquals(0, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(400, board.nodes);
            assertEquals(0, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(8902, board.nodes);
            assertEquals(34, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(12, board.checks[0]);
        }

        // todo: show checkmates

        if (depth == 4) {
            assertEquals(197281, board.nodes);
            //assertEquals(1576, board.captures[0]);
            //assertEquals(0, board.enPassants[0]);
            //assertEquals(0, board.castles[0]);
            //assertEquals(469, board.checks[0]);
        }

        if (depth == 6) {
            assertEquals(119060324, board.nodes);
            //assertEquals(2812008, board.captures[0]);
            //assertEquals(5248, board.enPassants[0]);
            //assertEquals(0, board.castles[0]);
            //assertEquals(809099, board.checks[0]);
        }
    }

    void runKiwipetePosition(int depth) {
        var board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0");
        board.perftTest(depth, false);

        if (depth == 1) {
            assertEquals(48, board.nodes);
            assertEquals(8, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(2, board.castles[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(2039, board.nodes);
            assertEquals(351, board.captures[0]);
            assertEquals(1, board.enPassants[0]);
            assertEquals(91, board.castles[0]);
            assertEquals(3, board.checks[0]);
        }
    }

    void runWiki3Position(int depth) {
        var board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");
        board.perftTest(depth, false);

        if (depth == 1) {
            assertEquals(14, board.nodes);
            assertEquals(1, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(2, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(191, board.nodes);
            assertEquals(14, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(10, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(2812, board.nodes);
            assertEquals(209, board.captures[0]);
            assertEquals(2, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(267, board.checks[0]);
        }
    }

    // https://www.chessprogramming.org/Perft_Results

    @Test
    void perftTest() {
        runStartPosition(4);
        runKiwipetePosition(2);
        runWiki3Position(3);
    }
}
