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

        // e2e4
        /*
        var m = new Move(Piece.BLACK_QUEEN, 28, 56);
        m.setSpecialMoveFlag(Move.SpecialMoveFlag.PAWN_START);

        System.out.println(m.getPiece());
        System.out.println(m.getFrom());
        System.out.println(m.getTo());
        System.out.println(m.getCapturedPieceType());
        System.out.println(m.getPromotedPieceType());
        System.out.println(m.getSpecialMoveFlag());
        */

        //var board = new Board();
        //var board = new Board("rnbqkbnr/pp4pp/2p2p2/1N2N3/8/3pp3/PPPPPPPP/R1BQKB1R w KQkq - 0 1");
        var board = new Board("rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1");
        board.setColored(true);
        System.out.println(board);

        MoveGenerator moveGenerator = new MoveGenerator();

        moveGenerator.addPawnMoves(
                Piece.WHITE_PAWN,
                board.getWhitePawns(),
                board.getBlackPieces(),
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
        System.out.println("Total: " + mlist.size() + " moves.");
    }
}
