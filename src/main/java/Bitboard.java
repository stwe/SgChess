import java.util.HashMap;
import java.util.Map;

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
    public static final int WHITE_PAWNS = 0;

    /**
     * The index for the white knights initial location bitboard.
     */
    public static final int WHITE_KNIGHTS = 1;

    /**
     * The index for the white bishops initial location bitboard.
     */
    public static final int WHITE_BISHOPS = 2;

    /**
     * The index for the white rooks initial location bitboard.
     */
    public static final int WHITE_ROOKS = 3;

    /**
     * The index for the white queens initial location bitboard.
     */
    public static final int WHITE_QUEENS = 4;

    /**
     * The index for the white king initial location bitboard.
     */
    public static final int WHITE_KING = 5;

    //-------------------------------------------------
    // Bitboard Index - Black
    //-------------------------------------------------

    /**
     * The index for the black pawns initial location bitboard.
     */
    public static final int BLACK_PAWNS = 6;

    /**
     * The index for the black knights initial location bitboard.
     */
    public static final int BLACK_KNIGHTS = 7;

    /**
     * The index for the black bishops initial location bitboard.
     */
    public static final int BLACK_BISHOPS = 8;

    /**
     * The index for the black rooks initial location bitboard.
     */
    public static final int BLACK_ROOKS = 9;

    /**
     * The index for the black queens initial location bitboard.
     */
    public static final int BLACK_QUEENS = 10;

    /**
     * The index for the black king initial location bitboard.
     */
    public static final int BLACK_KING = 11;

    //-------------------------------------------------
    // Bitboard Index - All
    //-------------------------------------------------

    /**
     * The index for the white pieces initial location bitboard.
     */
    public static final int ALL_WHITE_PIECES = 12;

    /**
     * The index for the black pieces initial location bitboard.
     */
    public static final int ALL_BLACK_PIECES = 13;

    /**
     * The index for the all pawns initial location bitboard.
     */
    public static final int ALL_PAWNS = 14;

    /**
     * The index for the all knights initial location bitboard.
     */
    public static final int ALL_KNIGHTS = 15;

    /**
     * The index for the all bishops initial location bitboard.
     */
    public static final int ALL_BISHOPS = 16;

    /**
     * The index for the all rooks initial location bitboard.
     */
    public static final int ALL_ROOKS = 17;

    /**
     * The index for the all queens initial location bitboard.
     */
    public static final int ALL_QUEENS = 18;

    /**
     * The index for the all kings initial location bitboard.
     */
    public static final int ALL_KINGS = 19;

    //-------------------------------------------------
    // Squares
    //-------------------------------------------------

    /*
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
     * Creates the square bitboard bit indices.
     */
    private static final Map<Long, Integer> squareBitIndex;
    static {
        squareBitIndex = new HashMap<>();
        for (var i = 0; i < 64; i++) {
            squareBitIndex.put(1L << i, i);
        }
    }

    /**
     * Returns the bit index for a given square bitboard (for example square E4 => returns bit index 28).
     *
     * @param square A square bitboard.
     *
     * @return The bit index.
     */
    public static int getSquareBitIndex(long square) {
        return squareBitIndex.get(square);
    }

    /**
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
     * Checks whether a bit is set on the given File and Rank position on a bitboard.
     *
     * @param bitboard The bitboard to be checked.
     * @param file A vertical line on the chessboard.
     * @param rank A horizontal line on the chessboard.
     *
     * @return boolean
     */
    public static boolean isBitSet(long bitboard, int file, int rank) {
        var square = getSquareByFileAndRank(file, rank);
        return (1L << square) == ((1L << square) & bitboard); // todo
    }

    /**
     * Converting 2D coordinates into 1D index.
     *
     * @param file A vertical line on the chessboard.
     * @param rank A horizontal line on the chessboard.
     *
     * @return int
     */
    public static int getSquareByFileAndRank(int file, int rank) {
        return 8 * rank + file;
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    /**
     * Renders a bitboard in the console.
     *
     * @param bitboard The bitboard which should be output.
     */
    public static void printBitboard(long bitboard) {
        String s;
        System.out.println();
        for (var rank = 8; rank >= 1; rank--) {

            s = rank + "| ";
            System.out.print(s);

            for (var file = 1; file <= 8; file++) {
                var ch = isBitSet(bitboard, file - 1, rank - 1) ? "1 " : "0 ";
                System.out.print(ch);
            }

            System.out.println();
        }

        System.out.println("   _______________");
        System.out.println("   a b c d e f g h");
    }

    /**
     * Renders a bitboard in the console as string.
     *
     * @param bitboard The bitboard which should be output.
     */
    public static void printBitboardAsBinaryString(long bitboard) {
        var result = String.format("%" + Long.SIZE + "s", Long.toBinaryString(bitboard)).replace(' ', '0');
        System.out.println(result);
    }
}
