import java.security.SecureRandom;

public class Zkey {

    public static final long sideToMove;
    public static final long[] castling = new long[16]; // vs. 4
    public static final long[] epIndex = new long[48];
    public static final long[][][] piece = new long[2][7][64];

    static {
        var rndg = new SecureRandom();
        rndg.setSeed(System.currentTimeMillis());

        for (var color = 0; color <= 1; color++) {
            for (var pieceType = 0; pieceType <= 6; pieceType++) {
                for (var square = 0; square < 64; square++) {
                    piece[color][pieceType][square] = rndg.nextLong();
                }
            }
        }

        /*
        for (int i = 0; i < castling.length; i++) {
            castling[i] = rndg.nextLong();
        }
        */

        // skip first item: contains only zeros, default value and has no effect when xorring
        /*
        for (int i = 1; i < epIndex.length; i++) {
            epIndex[i] = rndg.nextLong();
        }
        */

        sideToMove = rndg.nextLong();
    }
}
