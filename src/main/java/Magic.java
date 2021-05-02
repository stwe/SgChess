/**
 * Represents a Magic object.
 */
public class Magic {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * A bitboard containing all squares that can block a rook or a bishop.
     */
    public long blockerMask;

    /**
     * The generated move boards.
     */
    public long[] moveBoards;

    /**
     * Precomputed value to determine the correct move board index.
     */
    public int shift;

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    /**
     * Outputs some information for debugging.
     */
    public void print() {
        System.out.println("Blocker mask:");
        Bitboard.printBitboardAsBinaryString(blockerMask);
        Bitboard.printBitboard(blockerMask);
    }
}
