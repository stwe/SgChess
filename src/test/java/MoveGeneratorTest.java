/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    /**
     * 26 white pawn moves
     */
    private static final String WHITE_PAWNS = "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1";

    /**
     * 26 black pawn moves
     */
    private static final String BLACK_PAWNS = "rnbqkbnr/p1p1p3/3p3p/1p1p4/2P1Pp2/8/PP1P1PpP/RNBQKB1R b KQkq e3 0 1";

    /**
     * b: 11 knight moves + 5 king moves
     * w: 14 knight moves + 5 king moves
     */
    private static final String KNIGHTS_AND_KINGS = "5k2/1n6/4n3/6N1/8/3N4/8/5K2 w - - 0 1";

    /**
     * b: 12 rook moves + 6 knight moves + 5 king moves
     * w: 13 rook moves + 7 knight moves + 5 king moves
     */
    private static final String ROOKS = "6k1/8/5r2/8/1nR5/5N2/8/6K1 w - - 0 1";

    /**
     * b: 17 queen moves + 14 knights moves + 5 king moves
     * w: 22 queen moves + 10 knights moves + 5 king moves
     */
    private static final String QUEENS = "6k1/8/4nq2/8/1nQ5/5N2/1N6/6K1 w - - 0 1 ";

    /**
     * b: 13 bishops moves + 14 knights moves + 5 king moves
     * w: 11 bishops moves + 11 knights moves + 5 king moves
     */
    private static final String BISHOPS = "6k1/1b6/4n3/8/1n4B1/1B3N2/1N6/2b3K1 w - - 0 1 ";

    /**
     * tricky position = 48 moves
     */
    private static final String TEST48 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";

    @Test
    void getPseudoLegalMoves() {
        // start position with 20 moves
        var boardStart = new Board();
        var mgStart = new MoveGenerator(boardStart);
        assertEquals(20, mgStart.getPseudoLegalMoves().size());
        assertEquals(0, mgStart.filterPseudoLegalMovesBy(Move.MoveFlag.CAPTURE).size());
        assertEquals(0, mgStart.filterPseudoLegalMovesBy(Move.MoveFlag.CASTLING).size());

        // tricky position with 48 moves
        var board48 = new Board(TEST48);
        var mg48 = new MoveGenerator(board48);
        assertEquals(48, mg48.getPseudoLegalMoves().size());
        assertEquals(2, mg48.filterPseudoLegalMovesBy(Move.MoveFlag.CASTLING).size());

        // 26 white pawn moves
        var wpBoard = new Board(WHITE_PAWNS);
        var wpMg = new MoveGenerator(wpBoard);
        assertEquals(26, wpMg.filterPseudoLegalMovesBy(Piece.WHITE_PAWN).size());
        assertEquals(1, wpMg.filterPseudoLegalMovesBy(Move.MoveFlag.ENPASSANT).size());

        // 26 black pawn moves
        var bpBoard = new Board(BLACK_PAWNS);
        var bpMg = new MoveGenerator(bpBoard);
        assertEquals(26, bpMg.filterPseudoLegalMovesBy(Piece.BLACK_PAWN).size());
        assertEquals(1, bpMg.filterPseudoLegalMovesBy(Move.MoveFlag.ENPASSANT).size());

        // 14 white knight moves + 5 white king moves
        var kkBoard = new Board(KNIGHTS_AND_KINGS);
        var kkMg = new MoveGenerator(kkBoard);
        assertEquals(14, kkMg.filterPseudoLegalMovesBy(Piece.WHITE_KNIGHT).size());
        assertEquals(5, kkMg.filterPseudoLegalMovesBy(Piece.WHITE_KING).size());
        assertEquals(0, kkMg.filterPseudoLegalMovesBy(Move.MoveFlag.CASTLING).size());

        // 13 white rooks moves
        var wrBoard = new Board(ROOKS);
        var wrMg = new MoveGenerator(wrBoard);
        assertEquals(13, wrMg.filterPseudoLegalMovesBy(Piece.WHITE_ROOK).size());

        // 22 white queen moves
        var wqBoard = new Board(QUEENS);
        var wqMg = new MoveGenerator(wqBoard);
        assertEquals(22, wqMg.filterPseudoLegalMovesBy(Piece.WHITE_QUEEN).size());

        // 22 white queen moves
        var wbBoard = new Board(BISHOPS);
        var wbMg = new MoveGenerator(wbBoard);
        assertEquals(11, wbMg.filterPseudoLegalMovesBy(Piece.WHITE_BISHOP).size());

        // 6 capture moves
        var boardCap = new Board("rnbqkbnr/1p2pppp/p1p5/3p3Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 1");
        var capMg = new MoveGenerator(boardCap);
        assertEquals(6, capMg.filterPseudoLegalMovesBy(Move.MoveFlag.CAPTURE).size());
    }
}
