public class Main {

    /**
     * 26 pawn moves
     */
    private static final String WHITE_PAWNS = "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1";

    /**
     * 26 pawn moves
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
     * b:
     * w:
     */
    private static final String QUEENS = "6k1/8/4nq2/8/1nQ5/5N2/1N6/6K1 b - - 0 1 ";

    /**
     * b:
     * w:
     */
    private static final String BISHOPS = "6k1/1b6/4n3/8/1n4B1/1B3N2/1N6/2b3K1 b - - 0 1 ";

    public static void main(String[] args) {
        /*
        int i = 0;

        Scanner keyboard = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("Enter command (q to exit):");
            String input = keyboard.nextLine();

            if(input != null) {
                System.out.println("Your input is : " + input);
                if ("q".equals(input)) {
                    System.out.println("Exit programm");
                    exit = true;
                } else if ("n".equals(input)) {
                    System.out.println("current i: " + i);
                    Bitboard.printBitboard(Moves.KING_MOVES[i++]);
                }
            }
        }

        keyboard.close();
        */

        var board = new Board(ROOKS);
        board.setColored(true);
        System.out.println(board);

        var mlist = board.getMoveGenerator().getMoves();
        for (var move : mlist) {
            System.out.println(move);
        }
    }
}
