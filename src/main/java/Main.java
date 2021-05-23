import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, IllegalAccessException {
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

        ConfigLoader.load(Config.class, "/config.properties");

        var board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        System.out.println(board);

        var mg = new MoveGenerator(board);

        //mg.generateLegalMoves();

        var mlist = mg.getPseudoLegalMoves();
        for (var move : mlist) {
            System.out.println(move);
        }
        System.out.println("Total moves: " + mlist.size());
    }
}
