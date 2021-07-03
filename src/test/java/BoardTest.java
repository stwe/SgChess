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

    //-------------------------------------------------
    // Perft test
    //-------------------------------------------------

    /**
     * @see <a href="https://www.chessprogramming.org/Perft_Results">Start position</a>
     *
     * @param depth Perft test depth.
     * @param quiet Shows a summary.
     */
    void runStartPosition(int depth, boolean quiet) {
        var board = new Board();
        board.perftTest(depth, quiet, "Start position depth: " + depth);

        if (depth == 1) {
            assertEquals(20, board.nodes);
            assertEquals(0, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(400, board.nodes);
            assertEquals(0, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(8902, board.nodes);
            assertEquals(34, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(12, board.checks[0]);
        }

        if (depth == 4) {
            assertEquals(197281, board.nodes);
            assertEquals(1576, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(469, board.checks[0]);
            // 8 checkmates
        }

        if (depth == 5) {
            assertEquals(4865609, board.nodes);
            assertEquals(82719, board.captures[0]);
            assertEquals(258, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(27351, board.checks[0]);
            // 347 checkmates
        }

        if (depth == 6) {
            assertEquals(119060324, board.nodes);
            assertEquals(2812008, board.captures[0]);
            assertEquals(5248, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(809099, board.checks[0]);
            // 10828 checkmates
        }
    }

    /**
     * @see <a href="https://www.chessprogramming.org/Perft_Results">Kiwipete</a>
     *
     * @param depth Perft test depth.
     * @param quiet Shows a summary.
     */
    void runKiwipetePosition(int depth, boolean quiet) {
        var board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        board.perftTest(depth, quiet, "Kiwipete depth: " + depth);

        if (depth == 1) {
            assertEquals(48, board.nodes);
            assertEquals(8, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(2, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(2039, board.nodes);
            assertEquals(351, board.captures[0]);
            assertEquals(1, board.enPassants[0]);
            assertEquals(91, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(3, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(97862, board.nodes);
            assertEquals(17102, board.captures[0]);
            assertEquals(45, board.enPassants[0]);
            assertEquals(3162, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(993, board.checks[0]);
            // 1 checkmate
        }

        if (depth == 4) {
            assertEquals(4085603, board.nodes);
            assertEquals(757163, board.captures[0]);
            assertEquals(1929, board.enPassants[0]);
            assertEquals(128013, board.castles[0]);
            assertEquals(15172, board.promotions[0]);
            assertEquals(25523, board.checks[0]);
            // 43 checkmate
        }

        if (depth == 5) {
            assertEquals(193690690, board.nodes);
            assertEquals(35043416, board.captures[0]);
            assertEquals(73365, board.enPassants[0]);
            assertEquals(4993637, board.castles[0]);
            assertEquals(8392, board.promotions[0]);
            assertEquals(3309887, board.checks[0]);
            // 30171 checkmate
        }
    }

    /**
     * @see <a href="https://www.chessprogramming.org/Perft_Results">Wiki Position 3</a>
     *
     * @param depth Perft test depth.
     * @param quiet Shows a summary.
     */
    void runWiki3Position(int depth, boolean quiet) {
        var board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");
        board.perftTest(depth, quiet, "Wiki position 3 depth: " + depth);

        if (depth == 1) {
            assertEquals(14, board.nodes);
            assertEquals(1, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(2, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(191, board.nodes);
            assertEquals(14, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(10, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(2812, board.nodes);
            assertEquals(209, board.captures[0]);
            assertEquals(2, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(267, board.checks[0]);
        }

        if (depth == 4) {
            assertEquals(43238, board.nodes);
            assertEquals(3348, board.captures[0]);
            assertEquals(123, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(1680, board.checks[0]);
            // 17 checkmates
        }

        if (depth == 5) {
            assertEquals(674624, board.nodes);
            assertEquals(52051, board.captures[0]);
            assertEquals(1165, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(52950, board.checks[0]);
        }

        if (depth == 6) {
            assertEquals(11030083, board.nodes);
            assertEquals(940350, board.captures[0]);
            assertEquals(33325, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(7552, board.promotions[0]);
            assertEquals(452473, board.checks[0]);
            // 2733 checkmates
        }

        if (depth == 7) {
            assertEquals(178633661, board.nodes);
            assertEquals(14519036, board.captures[0]);
            assertEquals(294874, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(140024, board.promotions[0]);
            assertEquals(12797406, board.checks[0]);
            // 87 checkmates
        }
    }

    /**
     * @see <a href="https://www.chessprogramming.org/Perft_Results">Wiki Position 4</a>
     *
     * @param depth Perft test depth.
     * @param quiet Shows a summary.
     */
    void runWiki4Position(int depth, boolean quiet) {
        var board = new Board("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        board.perftTest(depth, quiet, "Wiki position 4 depth: " + depth);

        if (depth == 1) {
            assertEquals(6, board.nodes);
            assertEquals(0, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(0, board.promotions[0]);
            assertEquals(0, board.checks[0]);
        }

        if (depth == 2) {
            assertEquals(264, board.nodes);
            assertEquals(87, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(6, board.castles[0]);
            assertEquals(48, board.promotions[0]);
            assertEquals(10, board.checks[0]);
        }

        if (depth == 3) {
            assertEquals(9467, board.nodes);
            assertEquals(1021, board.captures[0]);
            assertEquals(4, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(120, board.promotions[0]);
            assertEquals(38, board.checks[0]);
            // 22 checkmates
        }

        if (depth == 4) {
            assertEquals(422333, board.nodes);
            assertEquals(131393, board.captures[0]);
            assertEquals(0, board.enPassants[0]);
            assertEquals(7795, board.castles[0]);
            assertEquals(60032, board.promotions[0]);
            assertEquals(15492, board.checks[0]);
            // 5 checkmates
        }

        if (depth == 5) {
            assertEquals(15833292, board.nodes);
            assertEquals(2046173, board.captures[0]);
            assertEquals(6512, board.enPassants[0]);
            assertEquals(0, board.castles[0]);
            assertEquals(329464, board.promotions[0]);
            assertEquals(200568, board.checks[0]);
            // 50562 checkmates
        }

        if (depth == 6) {
            assertEquals(706045033, board.nodes);
            assertEquals(210369132, board.captures[0]);
            assertEquals(212, board.enPassants[0]);
            assertEquals(10882006, board.castles[0]);
            assertEquals(81102984, board.promotions[0]);
            assertEquals(26973664, board.checks[0]);
            // 81076 checkmates
        }
    }

    /**
     * TalkChess PERFT Tests.
     * Positions which contain the special chess moves.
     *
     * @see <a href="https://www.chessprogramming.net/perfect-perft/">TalkChess PERFT Tests</a>
     */
    void talkChessPositions() {
        //--Illegal ep move #1
        var b0 = new Board("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1");
        b0.perftTest(6, "Illegal ep move #1");
        assertEquals(1134888, b0.nodes);

        //--Illegal ep move #2
        var b1 = new Board("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1");
        b1.perftTest(6, "Illegal ep move #2");
        assertEquals(1015133, b1.nodes);

        //--EP Capture Checks Opponent
        var b2 = new Board("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1");
        b2.perftTest(6, "EP Capture Checks Opponent");
        assertEquals(1440467, b2.nodes);

        //--Short Castling Gives Check
        var b3 = new Board("5k2/8/8/8/8/8/8/4K2R w K - 0 1");
        b3.perftTest(6, "Short Castling Gives Check");
        assertEquals(661072, b3.nodes);

        //--Long Castling Gives Check
        var b4 = new Board("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1");
        b4.perftTest(6, "Long Castling Gives Check");
        assertEquals(803711, b4.nodes);

        //--Castle Rights
        var b5 = new Board("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1");
        b5.perftTest(4, "Castle Rights");
        assertEquals(1274206, b5.nodes);

        //--Castling Prevented
        var b6 = new Board("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1");
        b6.perftTest(4, "Castling Prevented");
        assertEquals(1720476, b6.nodes);

        //--Promote out of Check
        var b7 = new Board("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1");
        b7.perftTest(6, "Promote out of Check");
        assertEquals(3821001, b7.nodes);

        //--Discovered Check
        var b8 = new Board("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1");
        b8.perftTest(5, "Discovered Check");
        assertEquals(1004658, b8.nodes);

        //--Promote to give check
        var b9 = new Board("4k3/1P6/8/8/8/8/K7/8 w - - 0 1");
        b9.perftTest(6, "Promote to give check");
        assertEquals(217342, b9.nodes);

        //--Under Promote to give check
        var b10 = new Board("8/P1k5/K7/8/8/8/8/8 w - - 0 1");
        b10.perftTest(6, "Under Promote to give check");
        assertEquals(92683, b10.nodes);

        //--Self Stalemate
        var b11 = new Board("K1k5/8/P7/8/8/8/8/8 w - - 0 1");
        b11.perftTest(6, "Self Stalemate");
        assertEquals(2217, b11.nodes);

        //--Stalemate & Checkmate
        var b12 = new Board("8/k1P5/8/1K6/8/8/8/8 w - - 0 1");
        b12.perftTest(7, "Stalemate & Checkmat");
        assertEquals(567584, b12.nodes);

        //--Stalemate & Checkmate
        var b13 = new Board("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1");
        b13.perftTest(4, "Stalemate & Checkmate");
        assertEquals(23527, b13.nodes);
    }

    @Test
    void perftTest() {
        for (var i = 1; i < 7; i++) {
            runStartPosition(i, false);
        }

        for (var i = 1; i < 6; i++) {
            runKiwipetePosition(i, false);
        }

        for (var i = 1; i < 8; i++) {
            runWiki3Position(i, false);
        }

        for (var i = 1; i < 7; i++) {
            runWiki4Position(i, false);
        }

        talkChessPositions();
    }
}
