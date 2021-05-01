public class Main {

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

        //Board board = new Board();

        Board board = new Board("7k/5b2/8/5r2/8/1R6/6B1/7K w - - 0 1");
        board.setColored(false);
        System.out.println(board);

        MoveGenerator moveGenerator = new MoveGenerator();
        //moveGenerator.addWhiteKnightMoves(board.getWhiteKnights(), board.getWhitePieces());
        //moveGenerator.addWhitePawnMoves(board.getWhitePawns(), board.getAllPieces());
        //moveGenerator.addWhiteKingMoves(board.getWhiteKing(), board.getWhitePieces());

        moveGenerator.addRookMoves(board.getWhiteRooks(), board.getAllPieces(), ~board.getWhitePieces(), 'R');
        moveGenerator.addRookMoves(board.getBlackRooks(), board.getAllPieces(), ~board.getBlackPieces(), 'r');

        //moveGenerator.addWhiteBishopMoves(board.getWhiteBishops(), board.getAllPieces(), board.getWhitePieces());
        //moveGenerator.addWhiteQueenMoves(board.getWhiteQueens(), board.getAllPieces(), board.getWhitePieces());

        var mlist = moveGenerator.getMoves();
        for (var move : mlist) {
            System.out.println(move);
        }
    }
}
