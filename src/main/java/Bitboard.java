/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Bitboard constants and utils.
 */
public class Bitboard {

    //-------------------------------------------------
    // Bitboard Index
    //-------------------------------------------------

    /**
     * The number of bitboards to represent the positions of each kind and color of piece on a chessboard.
     */
    public static final int NUMBER_OF_BITBOARDS = 20;

    //-------------------------------------------------
    // Bitboard Index - White
    //-------------------------------------------------

    /**
     * The index for the white pawns initial location bitboard.
     */
    public static final int WHITE_PAWNS_BITBOARD = 0;

    /**
     * The index for the white knights initial location bitboard.
     */
    public static final int WHITE_KNIGHTS_BITBOARD = 1;

    /**
     * The index for the white bishops initial location bitboard.
     */
    public static final int WHITE_BISHOPS_BITBOARD = 2;

    /**
     * The index for the white rooks initial location bitboard.
     */
    public static final int WHITE_ROOKS_BITBOARD = 3;

    /**
     * The index for the white queens initial location bitboard.
     */
    public static final int WHITE_QUEENS_BITBOARD = 4;

    /**
     * The index for the white king initial location bitboard.
     */
    public static final int WHITE_KING_BITBOARD = 5;

    //-------------------------------------------------
    // Bitboard Index - Black
    //-------------------------------------------------

    /**
     * The index for the black pawns initial location bitboard.
     */
    public static final int BLACK_PAWNS_BITBOARD = 6;

    /**
     * The index for the black knights initial location bitboard.
     */
    public static final int BLACK_KNIGHTS_BITBOARD = 7;

    /**
     * The index for the black bishops initial location bitboard.
     */
    public static final int BLACK_BISHOPS_BITBOARD = 8;

    /**
     * The index for the black rooks initial location bitboard.
     */
    public static final int BLACK_ROOKS_BITBOARD = 9;

    /**
     * The index for the black queens initial location bitboard.
     */
    public static final int BLACK_QUEENS_BITBOARD = 10;

    /**
     * The index for the black king initial location bitboard.
     */
    public static final int BLACK_KING_BITBOARD = 11;

    //-------------------------------------------------
    // Bitboard Index - All
    //-------------------------------------------------

    /**
     * The index for the white pieces initial location bitboard.
     */
    public static final int ALL_WHITE_PIECES_BITBOARD = 12;

    /**
     * The index for the black pieces initial location bitboard.
     */
    public static final int ALL_BLACK_PIECES_BITBOARD = 13;

    /**
     * The index for the all pawns initial location bitboard.
     */
    public static final int ALL_PAWNS_BITBOARD = 14;

    /**
     * The index for the all knights initial location bitboard.
     */
    public static final int ALL_KNIGHTS_BITBOARD = 15;

    /**
     * The index for the all bishops initial location bitboard.
     */
    public static final int ALL_BISHOPS_BITBOARD = 16;

    /**
     * The index for the all rooks initial location bitboard.
     */
    public static final int ALL_ROOKS_BITBOARD = 17;

    /**
     * The index for the all queens initial location bitboard.
     */
    public static final int ALL_QUEENS_BITBOARD = 18;

    /**
     * The index for the all kings initial location bitboard.
     */
    public static final int ALL_KINGS_BITBOARD = 19;

    //-------------------------------------------------
    // Squares
    //-------------------------------------------------

    /*
      Little endian rank file mapping of each square.
      Position A1 is the least signficant bit (LSB), bit 0, of the 64 bit number and H8 is the
      most significant bit (MSB), bit 63. The squares will be assigned in a left to right, bottom to top ordering to
      each bit index in the 64 bit number from LSB to MSB.

      8 | 56 57 58 59 60 61 62 63
      7 | 48 49 50 51 52 53 54 55
      6 | 40 41 42 43 44 45 46 47
      5 | 32 33 34 35 36 37 38 39
      4 | 24 25 26 27 28 29 30 31
      3 | 16 17 18 19 20 21 22 23
      2 |  8  9 10 11 12 13 14 15
      1 |  0  1  2  3  4  5  6  7
        -------------------------
           A  B  C  D  E  F  G  H

      Left shift '<<' means +1 on the chessboard
      Right shift '>>>' means -1 on the chessboard

      +7  +8  +9
      -1   0  +1
      -9  -8  -7
    */

    public static final long A1 = 1L;
    public static final long B1 = A1 << 1;
    public static final long C1 = B1 << 1;
    public static final long D1 = C1 << 1;
    public static final long E1 = D1 << 1;
    public static final long F1 = E1 << 1;
    public static final long G1 = F1 << 1;
    public static final long H1 = G1 << 1;

    public static final long A2 = H1 << 1;
    public static final long B2 = A2 << 1;
    public static final long C2 = B2 << 1;
    public static final long D2 = C2 << 1;
    public static final long E2 = D2 << 1;
    public static final long F2 = E2 << 1;
    public static final long G2 = F2 << 1;
    public static final long H2 = G2 << 1;

    public static final long A3 = H2 << 1;
    public static final long B3 = A3 << 1;
    public static final long C3 = B3 << 1;
    public static final long D3 = C3 << 1;
    public static final long E3 = D3 << 1;
    public static final long F3 = E3 << 1;
    public static final long G3 = F3 << 1;
    public static final long H3 = G3 << 1;

    public static final long A4 = H3 << 1;
    public static final long B4 = A4 << 1;
    public static final long C4 = B4 << 1;
    public static final long D4 = C4 << 1;
    public static final long E4 = D4 << 1;
    public static final long F4 = E4 << 1;
    public static final long G4 = F4 << 1;
    public static final long H4 = G4 << 1;

    public static final long A5 = H4 << 1;
    public static final long B5 = A5 << 1;
    public static final long C5 = B5 << 1;
    public static final long D5 = C5 << 1;
    public static final long E5 = D5 << 1;
    public static final long F5 = E5 << 1;
    public static final long G5 = F5 << 1;
    public static final long H5 = G5 << 1;

    public static final long A6 = H5 << 1;
    public static final long B6 = A6 << 1;
    public static final long C6 = B6 << 1;
    public static final long D6 = C6 << 1;
    public static final long E6 = D6 << 1;
    public static final long F6 = E6 << 1;
    public static final long G6 = F6 << 1;
    public static final long H6 = G6 << 1;

    public static final long A7 = H6 << 1;
    public static final long B7 = A7 << 1;
    public static final long C7 = B7 << 1;
    public static final long D7 = C7 << 1;
    public static final long E7 = D7 << 1;
    public static final long F7 = E7 << 1;
    public static final long G7 = F7 << 1;
    public static final long H7 = G7 << 1;

    public static final long A8 = H7 << 1;
    public static final long B8 = A8 << 1;
    public static final long C8 = B8 << 1;
    public static final long D8 = C8 << 1;
    public static final long E8 = D8 << 1;
    public static final long F8 = E8 << 1;
    public static final long G8 = F8 << 1;
    public static final long H8 = G8 << 1;

    /**
     * Little endian rank-file (LERF) mapping.
     * Returns the square bitboard for a given bit index.
     */
    public static final long[] SQUARES = {
            A1, B1, C1, D1, E1, F1, G1, H1,
            A2, B2, C2, D2, E2, F2, G2, H2,
            A3, B3, C3, D3, E3, F3, G3, H3,
            A4, B4, C4, D4, E4, F4, G4, H4,
            A5, B5, C5, D5, E5, F5, G5, H5,
            A6, B6, C6, D6, E6, F6, G6, H6,
            A7, B7, C7, D7, E7, F7, G7, H7,
            A8, B8, C8, D8, E8, F8, G8, H8
    };

    /**
     * Returns the square String for a given bit index.
     */
    public static final String[] SQUARE_STRINGS = {
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
    };

    //-------------------------------------------------
    // Squares Bit Index
    //-------------------------------------------------

    /**
     * Returns the bit index for a given square (Little endian rank-file (LERF) mapping) bitboard
     * (for example square E4 => returns bit index 28).
     */
    public enum BitIndex {
        A1_IDX, B1_IDX, C1_IDX, D1_IDX, E1_IDX, F1_IDX, G1_IDX, H1_IDX,
        A2_IDX, B2_IDX, C2_IDX, D2_IDX, E2_IDX, F2_IDX, G2_IDX, H2_IDX,
        A3_IDX, B3_IDX, C3_IDX, D3_IDX, E3_IDX, F3_IDX, G3_IDX, H3_IDX,
        A4_IDX, B4_IDX, C4_IDX, D4_IDX, E4_IDX, F4_IDX, G4_IDX, H4_IDX,
        A5_IDX, B5_IDX, C5_IDX, D5_IDX, E5_IDX, F5_IDX, G5_IDX, H5_IDX,
        A6_IDX, B6_IDX, C6_IDX, D6_IDX, E6_IDX, F6_IDX, G6_IDX, H6_IDX,
        A7_IDX, B7_IDX, C7_IDX, D7_IDX, E7_IDX, F7_IDX, G7_IDX, H7_IDX,
        A8_IDX, B8_IDX, C8_IDX, D8_IDX, E8_IDX, F8_IDX, G8_IDX, H8_IDX,
        NO_SQUARE
    }

    //-------------------------------------------------
    // Files && Ranks
    //-------------------------------------------------

    /**
     * A vertical line on the chessboard.
     */
    public enum File {
        FILE_A, FILE_B, FILE_C, FILE_D,
        FILE_E, FILE_F, FILE_G, FILE_H
    }

    /**
     * A horizontal line on the chessboard.
     */
    public enum Rank {
        RANK_1, RANK_2, RANK_3, RANK_4,
        RANK_5, RANK_6, RANK_7, RANK_8
    }

    //-------------------------------------------------
    // Clear && Mask
    //-------------------------------------------------

    // sets all bits to zero, except those of a rank

    public static final long MASK_RANK_1 = A1 | B1 | C1 | D1 | E1 | F1 | G1 | H1;
    public static final long MASK_RANK_2 = A2 | B2 | C2 | D2 | E2 | F2 | G2 | H2;
    public static final long MASK_RANK_3 = A3 | B3 | C3 | D3 | E3 | F3 | G3 | H3;
    public static final long MASK_RANK_4 = A4 | B4 | C4 | D4 | E4 | F4 | G4 | H4;
    public static final long MASK_RANK_5 = A5 | B5 | C5 | D5 | E5 | F5 | G5 | H5;
    public static final long MASK_RANK_6 = A6 | B6 | C6 | D6 | E6 | F6 | G6 | H6;
    public static final long MASK_RANK_7 = A7 | B7 | C7 | D7 | E7 | F7 | G7 | H7;
    public static final long MASK_RANK_8 = A8 | B8 | C8 | D8 | E8 | F8 | G8 | H8;

    public static final long MASK_FILE_A = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
    public static final long MASK_FILE_B = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
    public static final long MASK_FILE_C = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8;
    public static final long MASK_FILE_D = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8;
    public static final long MASK_FILE_E = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8;
    public static final long MASK_FILE_F = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8;
    public static final long MASK_FILE_G = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
    public static final long MASK_FILE_H = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;

    // sets all bits to one, except those of one or more files

    public static final long CLEAR_RANK_1 = ~MASK_RANK_1;
    public static final long CLEAR_RANK_2 = ~MASK_RANK_2;
    public static final long CLEAR_RANK_3 = ~MASK_RANK_3;
    public static final long CLEAR_RANK_4 = ~MASK_RANK_4;
    public static final long CLEAR_RANK_5 = ~MASK_RANK_5;
    public static final long CLEAR_RANK_6 = ~MASK_RANK_6;
    public static final long CLEAR_RANK_7 = ~MASK_RANK_7;
    public static final long CLEAR_RANK_8 = ~MASK_RANK_8;

    public static final long CLEAR_FILE_A = ~MASK_FILE_A;
    public static final long CLEAR_FILE_B = ~MASK_FILE_B;
    public static final long CLEAR_FILE_C = ~MASK_FILE_C;
    public static final long CLEAR_FILE_D = ~MASK_FILE_D;
    public static final long CLEAR_FILE_E = ~MASK_FILE_E;
    public static final long CLEAR_FILE_F = ~MASK_FILE_F;
    public static final long CLEAR_FILE_G = ~MASK_FILE_G;
    public static final long CLEAR_FILE_H = ~MASK_FILE_H;

    public static final long CLEAR_FILE_AB = CLEAR_FILE_A & CLEAR_FILE_B;
    public static final long CLEAR_FILE_GH = CLEAR_FILE_G & CLEAR_FILE_H;

    //-------------------------------------------------
    // Utils
    //-------------------------------------------------

    /**
     * Get the square bitboard for a given {@link BitIndex}.
     *
     * @param bitIndex A {@link BitIndex}.
     *
     * @return The square bitboard for a given {@link BitIndex}.
     */
    public static long getSquareBitboardFromBitIndex(BitIndex bitIndex) {
        return SQUARES[bitIndex.ordinal()];
    }

    /**
     * Checks whether a bit is set on the given bitboard {@link BitIndex}.
     *
     * @param bitboard The bitboard to be checked.
     * @param bitIndex The {@link BitIndex} of a square.
     *
     * @return boolean
     */
    public static boolean isBitSet(long bitboard, BitIndex bitIndex) {
        return (getSquareBitboardFromBitIndex(bitIndex) & bitboard) != 0;
    }

    /**
     * Checks whether a bit is set on the given bitboard {@link File} and {@link Rank} position.
     *
     * @param bitboard The bitboard to be checked.
     * @param file A {@link File}.
     * @param rank A {@link Rank}.
     *
     * @return boolean
     */
    public static boolean isBitSet(long bitboard, File file, Rank rank) {
        return isBitSet(bitboard, getBitIndexByFileAndRank(file, rank));
    }

    /**
     * Converting 2D {@link File} and {@link Rank} into 1D {@link BitIndex}.
     *
     * @param file A {@link File}.
     * @param rank A {@link Rank}.
     *
     * @return {@link BitIndex}
     */
    public static BitIndex getBitIndexByFileAndRank(File file, Rank rank) {
        return BitIndex.values()[8 * rank.ordinal() + file.ordinal()];
    }

    /**
     * Retrieve the {@link BitIndex} of the given bitboard's least significant bit.
     *
     * @param bitboard The bitboard whose LSB index is retrieved.
     *
     * @return The {@link BitIndex} of the given bitboard's least significant bit.
     */
    public static BitIndex getLsb(long bitboard) {
        return BitIndex.values()[Long.numberOfTrailingZeros(bitboard)];
    }

    /**
     * Returns the number of one-bits.
     *
     * @param bitboard The bitboard whose bits are to be counted.
     *
     * @return The number of one-bits.
     */
    public static int bitCount(long bitboard) {
        return Long.bitCount(bitboard);
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    /**
     * A chessboard representation of this bitboard.
     *
     * @param bitboard The bitboard which should be output.
     */
    public static void printBitboard(long bitboard) {
        String s;
        System.out.println();

        for (var rank = Rank.RANK_8.ordinal(); rank >= Rank.RANK_1.ordinal(); rank--) {
            s = (rank + 1) + "| ";
            System.out.print(s);

            for (var file : File.values()) {
                var ch = isBitSet(bitboard, file, Rank.values()[rank]) ? "1 " : "0 ";
                System.out.print(ch);
            }

            System.out.println();
        }

        System.out.println("   _______________");
        System.out.println("   a b c d e f g h");
    }

    /**
     * A pretty readable string representation of this bitboard.
     *
     * @param bitboard The bitboard which should be output.
     */
    public static void printBitboardAsBinaryString(long bitboard) {
        var result = String.format("%" + Long.SIZE + "s", Long.toBinaryString(bitboard)).replace(' ', '0');
        System.out.println(result);
    }
}
