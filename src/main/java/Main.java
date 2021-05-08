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

        var m = new Move();
        m.setFrom(28);
        m.setTo(56);
        System.out.println(m.getFrom());
        System.out.println(m.getTo());

        //Board board = new Board();
        Board board = new Board("rnbqkbnr/pp4pp/2p2p2/1N2N3/8/3pp3/PPPPPPPP/R1BQKB1R w KQkq - 0 1");
        board.setColored(true);
        System.out.println(board);

        MoveGenerator moveGenerator = new MoveGenerator();

        /*
        moveGenerator.addPawnMoves(
                Piece.WHITE_PAWN,
                board.getWhitePawns(),
                board.getBlackPieces(),
                board.getAllPieces()
        );
        */

        moveGenerator.addPawnMoves(
                Piece.BLACK_PAWN,
                board.getBlackPawns(),
                board.getWhitePieces(),
                board.getAllPieces()
        );

        /*
        moveGenerator.addNonslidingPiecesMoves(
                Piece.WHITE_KNIGHT,
                board.getWhiteKnights(),
                ~board.getWhitePieces()
        );
        */

        /*
        moveGenerator.addSlidingPiecesMoves(
                Piece.WHITE_BISHOP,
                board.getWhiteBishops(),
                board.getAllPieces(),
                ~board.getWhitePieces()
        );
        */

        var mlist = moveGenerator.getMoves();
        for (var move : mlist) {
            System.out.println(move);
        }
    }
}
