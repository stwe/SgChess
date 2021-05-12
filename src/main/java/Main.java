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

        var board = new Board(BLACK_PAWNS);
        board.setColored(true);
        System.out.println(board);

        var mlist = board.getMoveGenerator().getMoves();
        for (var move : mlist) {
            System.out.println(move);
        }
        System.out.println("Total: " + mlist.size() + " moves.");
    }
}
