/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttackTest {

    @Test
    void getKingMoves() {
        // king on E4 = 8 moves
        var kingMoves = Attack.getKingMoves(Bitboard.BitIndex.E4_IDX);

        // count moves
        assertEquals(8, Bitboard.bitCount(kingMoves));

        // check moves
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.D5_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.E5_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.F5_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.F4_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.F3_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.E3_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.D3_IDX));
        assertTrue(Bitboard.isBitSet(kingMoves, Bitboard.BitIndex.D4_IDX));
    }

    @Test
    void getKnightMoves() {
        // knight on B1 = 3 moves
        var knightMoves = Attack.getKnightMoves(Bitboard.BitIndex.B1_IDX);

        // count moves
        assertEquals(3, Bitboard.bitCount(knightMoves));

        // check moves
        assertTrue(Bitboard.isBitSet(knightMoves, Bitboard.BitIndex.A3_IDX));
        assertTrue(Bitboard.isBitSet(knightMoves, Bitboard.BitIndex.C3_IDX));
        assertTrue(Bitboard.isBitSet(knightMoves, Bitboard.BitIndex.D2_IDX));
    }

    @Test
    void getPawnAttacks() {
        // white pawn on E2 attacks D3 and F3
        var whitePawnAttacks = Attack.getPawnAttacks(Board.Color.WHITE, Bitboard.BitIndex.E2_IDX);
        assertTrue(Bitboard.isBitSet(whitePawnAttacks, Bitboard.BitIndex.D3_IDX));
        assertTrue(Bitboard.isBitSet(whitePawnAttacks, Bitboard.BitIndex.F3_IDX));

        // black pawn on C7 attacks B6 and D6
        var blackPawnAttacks = Attack.getPawnAttacks(Board.Color.BLACK, Bitboard.BitIndex.C7_IDX);
        assertTrue(Bitboard.isBitSet(blackPawnAttacks, Bitboard.BitIndex.B6_IDX));
        assertTrue(Bitboard.isBitSet(blackPawnAttacks, Bitboard.BitIndex.D6_IDX));
    }

    @Test
    void getRookMoves() {
        var board = new Board("rnbqkbnr/1ppppppp/8/8/8/pP6/1PPPPPPP/RNBQKBNR w KQkq - 0 1");
        var rookMoves = Attack.getRookMoves(Bitboard.BitIndex.A1_IDX, board.getAllPieces());

        assertEquals(3, Bitboard.bitCount(rookMoves));

        assertTrue(Bitboard.isBitSet(rookMoves, Bitboard.BitIndex.A2_IDX));
        assertTrue(Bitboard.isBitSet(rookMoves, Bitboard.BitIndex.A3_IDX)); // black pawn
        assertTrue(Bitboard.isBitSet(rookMoves, Bitboard.BitIndex.B1_IDX)); // white knight
    }

    @Test
    void getBishopMoves() {
        var board = new Board("rnbqkbnr/ppppp1pp/8/8/4Pp2/3P4/PPP2PPP/RNBQKBNR w KQkq - 0 1");
        var bishopMoves = Attack.getBishopMoves(Bitboard.BitIndex.C1_IDX, board.getAllPieces());

        assertEquals(4, Bitboard.bitCount(bishopMoves));

        assertTrue(Bitboard.isBitSet(bishopMoves, Bitboard.BitIndex.D2_IDX));
        assertTrue(Bitboard.isBitSet(bishopMoves, Bitboard.BitIndex.E3_IDX));
        assertTrue(Bitboard.isBitSet(bishopMoves, Bitboard.BitIndex.F4_IDX)); // black pawn
        assertTrue(Bitboard.isBitSet(bishopMoves, Bitboard.BitIndex.B2_IDX)); // white pawn
    }

    @Test
    void getQueenMoves() {
        var board = new Board("rnbqkbnr/p1pppppp/8/8/8/1pP5/PP1PPPPP/RNBQKBNR w KQkq - 0 1");
        var queenMoves = Attack.getQueenMoves(Bitboard.BitIndex.D1_IDX, board.getAllPieces());

        assertEquals(6, Bitboard.bitCount(queenMoves));

        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.C1_IDX)); // white bishop
        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.C2_IDX));
        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.B3_IDX)); // black pawn
        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.D2_IDX)); // white pawn
        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.E2_IDX)); // white pawn
        assertTrue(Bitboard.isBitSet(queenMoves, Bitboard.BitIndex.E1_IDX)); // white king
    }

    @Test
    void getAttackersToSquare() {
        var board = new Board("rn2kb1r/ppp1pppp/5n2/3p4/1q1PP1b1/6PP/PPP2P2/RNBQKBNR w KQkq - 1 2");

        // white pawn on E4 (attackers: black pawn on D5 && black knight on F6)
        var e4Attackers = Attack.getAttackersToSquare(Board.Color.WHITE, Bitboard.BitIndex.E4_IDX, board);
        assertEquals(2, Bitboard.bitCount(e4Attackers));

        var blackPawnD5 = e4Attackers & board.getBlackPawns();
        assertEquals(Bitboard.D5, blackPawnD5);

        var blackKnightF6 = e4Attackers & board.getBlackKnights();
        assertEquals(Bitboard.F6, blackKnightF6);

        // black bishop on G4 (attackers: white pawn on H3 && white queen on D1)
        var g4Attackers = Attack.getAttackersToSquare(Board.Color.BLACK, Bitboard.BitIndex.G4_IDX, board);
        assertEquals(2, Bitboard.bitCount(g4Attackers));

        var whitePawnH3 = g4Attackers & board.getWhitePawns();
        assertEquals(Bitboard.H3, whitePawnH3);

        var whiteQueenD1 = g4Attackers & board.getWhiteQueens();
        assertEquals(Bitboard.D1, whiteQueenD1);
    }

    @Test
    void isSquareAttacked() {
        var board = new Board("rn2kb1r/ppp1pppp/5n2/3p4/1q1PP1b1/6PP/PPP2P2/RNBQKBNR w KQkq - 1 2");

        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.E1_IDX, board)); // white king
        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.D1_IDX, board)); // white queen
        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.E4_IDX, board)); // white pawn
        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.D4_IDX, board)); // white pawn
        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.B2_IDX, board)); // white pawn
        assertTrue(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.H3_IDX, board)); // white pawn

        assertFalse(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.A1_IDX, board)); // white rook
        assertFalse(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.G1_IDX, board)); // white knight
        assertFalse(Attack.isWhiteSquareAttacked(Bitboard.BitIndex.F2_IDX, board)); // white pawn

        assertTrue(Attack.isBlackSquareAttacked(Bitboard.BitIndex.D5_IDX, board)); // black pawn
        assertTrue(Attack.isBlackSquareAttacked(Bitboard.BitIndex.G4_IDX, board)); // black bishop
    }

    @Test
    void areOneOrMoreSquaresAttacked() {
        var board = new Board("rn2k1nr/pp3ppp/2p5/q1bpP3/4PPP1/7b/PPP4P/RNBQKBNR w KQkq - 0 1");

        assertTrue(Attack.areOneOrMoreSquaresAttacked(
                Board.Color.WHITE,
                board,
                Bitboard.BitIndex.E1_IDX, Bitboard.BitIndex.F1_IDX, Bitboard.BitIndex.G1_IDX
                )
        );

        assertFalse(Attack.areOneOrMoreSquaresAttacked(
                Board.Color.WHITE,
                board,
                Bitboard.BitIndex.A1_IDX, Bitboard.BitIndex.B1_IDX, Bitboard.BitIndex.C1_IDX
                )
        );

        assertTrue(Attack.areOneOrMoreSquaresAttacked(
                Board.Color.BLACK,
                board,
                Bitboard.BitIndex.D5_IDX, Bitboard.BitIndex.H3_IDX
                )
        );
    }
}
