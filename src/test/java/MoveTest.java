/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    void createMove() {
        var move = new Move(Piece.WHITE_PAWN, Bitboard.BitIndex.E2_IDX, Bitboard.BitIndex.E4_IDX);

        assertEquals(Piece.WHITE_PAWN, move.getPiece());
        assertEquals(Bitboard.BitIndex.E2_IDX.ordinal(), move.getFrom());
        assertEquals(Bitboard.BitIndex.E4_IDX.ordinal(), move.getTo());

        move.setPreviousCastlingRights(Bitboard.BOTH_CASTLE_BOTH_SIDES);
        assertEquals(Bitboard.BOTH_CASTLE_BOTH_SIDES, move.getPreviousCastlingRights());

        move.setPreviousCastlingRights(Bitboard.WHITE_KING_CASTLE_KING_SIDE);
        assertEquals(Bitboard.WHITE_KING_CASTLE_KING_SIDE, move.getPreviousCastlingRights());

        move.setPreviousCastlingRights(Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE);
        assertEquals(Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE, move.getPreviousCastlingRights());

        move.setPreviousCastlingRights(Bitboard.BLACK_KING_CASTLE_KING_SIDE);
        assertEquals(Bitboard.BLACK_KING_CASTLE_KING_SIDE, move.getPreviousCastlingRights());

        move.setPreviousCastlingRights(Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE);
        assertEquals(Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE, move.getPreviousCastlingRights());
    }
}
