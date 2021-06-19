/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.security.SecureRandom;

public class Zkey {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The two sides to move possibilities.
     */
    public static final long colorToMove;

    /**
     * 16 castling possibilities.
     */
    public static final long[] castling = new long[16];

    /**
     * 48 ep possibilities, the last two ranks are never needed.
     */
    public static final long[] epIndex = new long[48];

    /**
     * Possible piece on square variants for both colors.
     * Two different {@link Board.Color}, seven different {@link PieceType}, 64 different squares.
     */
    public static final long[][][] piece = new long[2][7][64];

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    static {
        var rndg = new SecureRandom();
        rndg.setSeed(System.currentTimeMillis());

        // color to move
        colorToMove = rndg.nextLong();

        // castling
        for (int i = 0; i < castling.length; i++) {
            castling[i] = rndg.nextLong();
        }

        // ep index
        for (int i = 0; i < epIndex.length; i++) {
            epIndex[i] = rndg.nextLong();
        }

        // piece
        for (var color = 0; color <= 1; color++) {
            for (var pieceType = 0; pieceType <= 6; pieceType++) {
                for (var square = 0; square < 64; square++) {
                    piece[color][pieceType][square] = rndg.nextLong();
                }
            }
        }
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    /**
     * Create the Zobrist key for a given {@link Board}.
     *
     * @param board A {@link Board}.
     */
    public static void createKey(Board board) {
        var key = 0L;

        // color to move
        if (board.getColorToMove() == Board.Color.WHITE) {
            key ^= colorToMove;
        }

        // castling
        key ^= castling[board.getCastlingRights()];

        // ep index
        if (board.getEpIndex() != Bitboard.BitIndex.NO_SQUARE) {
            key ^= epIndex[board.getEpIndex().ordinal()];
        }

        // piece
        for (var color = 0; color <= 1; color++) {
            for (var pieceType = 1; pieceType <= 6; pieceType++) {
                var bitboardNr = PieceType.getBitboardNumber(pieceType, color);
                var bitboard = board.getBitboards()[bitboardNr];
                while (bitboard != 0) {
                    key ^= piece[color][pieceType][Bitboard.getLsb(bitboard).ordinal()];

                    bitboard &= bitboard - 1;
                }
            }
        }

        board.setZkey(key);
    }
}
