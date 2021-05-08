import java.util.ArrayList;

/*
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

/**
 * Represents a MoveGenerator object.
 */
public class MoveGenerator {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * A list with all generated moves.
     */
    private final ArrayList<Move> moves = new ArrayList<>();

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    /**
     * Get {@link #moves}.
     *
     * @return {@link #moves}
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    //-------------------------------------------------
    // Nonsliding pieces (Knight, King)
    //-------------------------------------------------

    /**
     * The method adds moves for the nonsliding pieces except pawns (Knight, King).
     *
     * @param piece Specifies for which type of piece the moves are to be added.
     * @param piecesBitboard A bitboard with all the knights or the king.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    public void addNonslidingPiecesMoves(Piece piece, long piecesBitboard, long possiblePositionsBitboard) {
        while (piecesBitboard != 0) {
            var fromBitIndex = Long.numberOfTrailingZeros(piecesBitboard);

            var movesBitboard = 0L;

            switch (piece) {
                case WHITE_KNIGHT:
                case BLACK_KNIGHT:
                    movesBitboard = Attack.knightMovesAndAttacks[fromBitIndex] & possiblePositionsBitboard;
                    break;
                case WHITE_KING:
                case BLACK_KING:
                    movesBitboard = Attack.kingMovesAndAttacks[fromBitIndex] & possiblePositionsBitboard;
                    break;
                default:
            }

            while (movesBitboard != 0) {
                moves.add(new Move(piece, fromBitIndex, Long.numberOfTrailingZeros(movesBitboard)));
                movesBitboard &= movesBitboard - 1;
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Nonsliding pieces (Pawn)
    //-------------------------------------------------

    /**
     * The method adds moves for the white and black pawns.
     *
     * @param piece Specifies for which color of pawn the moves are to be added (WHITE_PAWN or BLACK_PAWN).
     * @param piecesBitboard A bitboard with all the white or black pawns.
     * @param enemyPiecesBitboard A bitboard with all enemy pieces.
     * @param allPiecesBitboard A bitboard with all pieces.
     */
    public void addPawnMoves(Piece piece, long piecesBitboard, long enemyPiecesBitboard, long allPiecesBitboard) {
        while (piecesBitboard != 0) {
            var fromBitIndex = Long.numberOfTrailingZeros(piecesBitboard);

            var movesBitboard = 0L;

            switch (piece) {
                case WHITE_PAWN:
                    movesBitboard = getWhitePawnMoves(fromBitIndex, enemyPiecesBitboard, allPiecesBitboard);
                    break;
                case BLACK_PAWN:
                    movesBitboard = getBlackPawnMoves(fromBitIndex, enemyPiecesBitboard, allPiecesBitboard);
                    break;
                default:
            }

            while (movesBitboard != 0) {
                var move = new Move(piece, fromBitIndex, Long.numberOfTrailingZeros(movesBitboard));
                moves.add(move);

                movesBitboard &= movesBitboard - 1;
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }

    /**
     * Computing white pawn movements bitboard.
     *
     * @see <a href="http://pages.cs.wisc.edu/~psilord/blog/data/chess-pages/nonsliding.html">Nonsliding Pieces</a>
     *
     * @param fromBitIndex The bit index of the white pawn.
     * @param blackPiecesBitboard The bitboard with all black pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     *
     * @return White pawns valid movements bitboard.
     */
    private static long getWhitePawnMoves(int fromBitIndex, long blackPiecesBitboard, long allPiecesBitboard) {
        long whitePawnBitboard = 1L << fromBitIndex;

        // check the single space infront of the white pawn
        long firstStepBitboard = (whitePawnBitboard << 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_3) << 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.whitePawnAttacks[fromBitIndex] & blackPiecesBitboard;

        // return possible moves
        return (firstStepBitboard | twoStepsBitboard) | attacksBitboard;
    }

    /**
     * Computing black pawn movements bitboard.
     *
     * @param fromBitIndex The bit index of the black pawn.
     * @param whitePiecesBitboard The bitboard with all white pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     *
     * @return Black pawns valid movements bitboard.
     */
    private static long getBlackPawnMoves(int fromBitIndex, long whitePiecesBitboard, long allPiecesBitboard) {
        long blackPawnBitboard = 1L << fromBitIndex;

        // check the single space infront of the black pawn
        long firstStepBitboard = (blackPawnBitboard >>> 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_6) >>> 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.blackPawnAttacks[fromBitIndex] & whitePiecesBitboard;

        // return possible moves
        return (firstStepBitboard | twoStepsBitboard) | attacksBitboard;
    }

    //-------------------------------------------------
    // Sliding pieces (Rook, Bishop, Queen)
    //-------------------------------------------------

    /**
     * The method adds moves for the sliding pieces (Rook, Bishop or Queen).
     *
     * @param piece Specifies for which type of piece the moves are to be added.
     * @param piecesBitboard A bitboard with all the rooks, bishops or queens.
     * @param allPiecesBitboard A bitboard with all pieces.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    public void addSlidingPiecesMoves(Piece piece, long piecesBitboard, long allPiecesBitboard, long possiblePositionsBitboard) {
        while (piecesBitboard != 0) {
            var fromBitIndex = Long.numberOfTrailingZeros(piecesBitboard);

            var movesBitboard = 0L;

            switch (piece) {
                case WHITE_ROOK:
                case BLACK_ROOK:
                    movesBitboard = Attack.getRookMoves(fromBitIndex, allPiecesBitboard) & possiblePositionsBitboard;
                    break;
                case WHITE_BISHOP:
                case BLACK_BISHOP:
                    movesBitboard = Attack.getBishopMoves(fromBitIndex, allPiecesBitboard) & possiblePositionsBitboard;
                    break;
                case WHITE_QUEEN:
                case BLACK_QUEEN:
                    movesBitboard = Attack.getQueenMoves(fromBitIndex, allPiecesBitboard) & possiblePositionsBitboard;
                    break;
                default:
            }

            while (movesBitboard != 0) {
                moves.add(new Move(piece, fromBitIndex, Long.numberOfTrailingZeros(movesBitboard)));
                movesBitboard &= movesBitboard - 1;
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }
}
