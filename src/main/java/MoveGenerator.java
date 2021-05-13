import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.abs;

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
     * The parent {@link Board} object.
     */
    private final Board board;

    /**
     * A list with all generated moves.
     */
    private final ArrayList<Move> moves = new ArrayList<>();

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link MoveGenerator} object.
     *
     * @param board The parent {@link Board} object.
     */
    public MoveGenerator(Board board) {
        this.board = Objects.requireNonNull(board, "board must not be null");
        addMoves();
    }

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
     * @param moveFlag The {@link Move.MoveFlag}. <b>Normal</b> (for the quiet moves) and <b>Capture</b> are accepted flags.
     * @param piecesBitboard A bitboard with all the knights or the king.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    public void addNonslidingPiecesMoves(Piece piece, Move.MoveFlag moveFlag, long piecesBitboard, long possiblePositionsBitboard) {
        if (moveFlag != Move.MoveFlag.NORMAL && moveFlag != Move.MoveFlag.CAPTURE) {
            return;
        }

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

            if (moveFlag == Move.MoveFlag.NORMAL) {
                addQuietMoves(piece, fromBitIndex, movesBitboard);
            } else {
                addCaptureMoves(piece, fromBitIndex, movesBitboard);
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Nonsliding pieces (Pawn)
    //-------------------------------------------------

    /**
     * Add moves for the white and black pawns.
     *
     * @param piece Specifies for which color of pawn the moves are to be added (WHITE_PAWN or BLACK_PAWN).
     * @param piecesBitboard A bitboard with all the white or black pawns.
     * @param enemyPiecesBitboard A bitboard with all enemy pieces.
     * @param allPiecesBitboard A bitboard with all pieces.
     */
    public void addPawnMoves(Piece piece, long piecesBitboard, long enemyPiecesBitboard, long allPiecesBitboard) {
        // en passant
        switch (piece) {
            case WHITE_PAWN:
                addWhiteEnPassantMoves();
                break;
            case BLACK_PAWN:
                addBlackEnPassantMoves();
                break;
            default:
        }

        // other pawn moves
        while (piecesBitboard != 0) {
            var fromBitIndex = Long.numberOfTrailingZeros(piecesBitboard);

            switch (piece) {
                case WHITE_PAWN:
                    addWhitePawnMoves(fromBitIndex, enemyPiecesBitboard, allPiecesBitboard);
                    break;
                case BLACK_PAWN:
                    addBlackPawnMoves(fromBitIndex, enemyPiecesBitboard, allPiecesBitboard);
                    break;
                default:
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }

    /**
     * Computing white pawn en passant movements.
     */
    private void addWhiteEnPassantMoves() {
        var whitePawnsBitboard = board.getWhitePawns() & Bitboard.MASK_RANK_5;

        if (board.getEpIndex() != 0) {
            while (whitePawnsBitboard != 0) {
                var enemyDestination = board.getEpIndex() - 8;
                var fromBitIndex = Long.numberOfTrailingZeros(whitePawnsBitboard);
                if (abs(fromBitIndex - enemyDestination) == 1) {
                    var move = new Move(Piece.WHITE_PAWN, fromBitIndex, board.getEpIndex());
                    move.setMoveFlag(Move.MoveFlag.ENPASSANT);
                    moves.add(move);
                }

                whitePawnsBitboard &= whitePawnsBitboard - 1;
            }
        }
    }

    /**
     * Computing black pawn en passant movements.
     */
    private void addBlackEnPassantMoves() {
        var blackPawnsBitboard = board.getBlackPawns() & Bitboard.MASK_RANK_4;

        if (board.getEpIndex() != 0) {
            while (blackPawnsBitboard != 0) {
                var enemyDestination = board.getEpIndex() + 8;
                var fromBitIndex = Long.numberOfTrailingZeros(blackPawnsBitboard);
                if (abs(fromBitIndex - enemyDestination) == 1) {
                    var move = new Move(Piece.BLACK_PAWN, fromBitIndex, board.getEpIndex());
                    move.setMoveFlag(Move.MoveFlag.ENPASSANT);
                    moves.add(move);
                }

                blackPawnsBitboard &= blackPawnsBitboard - 1;
            }
        }
    }

    /**
     * Computing white pawn movements.
     *
     * @see <a href="http://pages.cs.wisc.edu/~psilord/blog/data/chess-pages/nonsliding.html">Nonsliding Pieces</a>
     *
     * @param fromBitIndex The bit index of the white pawn.
     * @param blackPiecesBitboard The bitboard with all black pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     */
    private void addWhitePawnMoves(int fromBitIndex, long blackPiecesBitboard, long allPiecesBitboard) {
        long whitePawnBitboard = 1L << fromBitIndex;

        // check the single space infront of the white pawn
        long firstStepBitboard = (whitePawnBitboard << 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_3) << 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.whitePawnAttacks[fromBitIndex] & blackPiecesBitboard;

        // add moves
        addQuietMoves(Piece.WHITE_PAWN, fromBitIndex, firstStepBitboard & Bitboard.CLEAR_RANK_8);
        addPawnStartMoves(Piece.WHITE_PAWN, fromBitIndex, twoStepsBitboard);
        addPromotionMoves(Piece.WHITE_PAWN, fromBitIndex, firstStepBitboard & Bitboard.MASK_RANK_8);
        addCaptureMoves(Piece.WHITE_PAWN, fromBitIndex, attacksBitboard & Bitboard.CLEAR_RANK_8);
        addPromotionMoves(Piece.WHITE_PAWN, fromBitIndex, attacksBitboard & Bitboard.MASK_RANK_8);
    }

    /**
     * Computing black pawn movements.
     *
     * @see <a href="http://pages.cs.wisc.edu/~psilord/blog/data/chess-pages/nonsliding.html">Nonsliding Pieces</a>
     *
     * @param fromBitIndex The bit index of the black pawn.
     * @param whitePiecesBitboard The bitboard with all white pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     */
    private void addBlackPawnMoves(int fromBitIndex, long whitePiecesBitboard, long allPiecesBitboard) {
        long blackPawnBitboard = 1L << fromBitIndex;

        // check the single space infront of the black pawn
        long firstStepBitboard = (blackPawnBitboard >>> 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_6) >>> 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.blackPawnAttacks[fromBitIndex] & whitePiecesBitboard;

        // add moves
        addQuietMoves(Piece.BLACK_PAWN, fromBitIndex, firstStepBitboard & Bitboard.CLEAR_RANK_1);
        addPawnStartMoves(Piece.BLACK_PAWN, fromBitIndex, twoStepsBitboard);
        addPromotionMoves(Piece.BLACK_PAWN, fromBitIndex, firstStepBitboard & Bitboard.MASK_RANK_1);
        addCaptureMoves(Piece.BLACK_PAWN, fromBitIndex, attacksBitboard & Bitboard.CLEAR_RANK_1);
        addPromotionMoves(Piece.BLACK_PAWN, fromBitIndex, attacksBitboard & Bitboard.MASK_RANK_1);
    }

    /**
     * Add pawn start move.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given fromBitIndex</p>
     * <p><b>to: </b> read from the given movesBitboard</p>
     * <p><b>captured piece type: </b> NO_PIECE(0)</p>
     * <p><b>promoted piece type: </b> NO_PIECE(0)</p>
     * <p><b>special move flag: </b> PAWN_START(4)</p>
     * <p><b>piece: </b> the given piece</p>
     * <p></p>
     *
     * @param piece A pawn of any color.
     * @param fromBitIndex The bit index of the given pawn.
     * @param movesBitboard A bitboard with all the pawn start moves to be added.
     */
    private void addPawnStartMoves(Piece piece, int fromBitIndex, long movesBitboard) {
        if (piece != Piece.WHITE_PAWN && piece != Piece.BLACK_PAWN) {
            return;
        }

        while (movesBitboard != 0) {
            var move = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            move.setMoveFlag(Move.MoveFlag.PAWN_START);

            moves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    /**
     * Add promotion and promotion capture moves.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given fromBitIndex</p>
     * <p><b>to: </b> read from the given movesBitboard</p>
     * <p><b>captured piece type: </b> NO_PIECE(0)</p>
     * <p><b>promoted piece type: </b> KNIGHT(2) - QUEEN(5)</p>
     * <p><b>special move flag: </b> PROMOTION(1)</p>
     * <p><b>piece: </b> the given piece</p>
     * <p></p>
     *
     * @param piece A pawn of any color.
     * @param fromBitIndex The bit index of the given pawn.
     * @param movesBitboard A bitboard with all moves to be added.
     */
    private void addPromotionMoves(Piece piece, int fromBitIndex, long movesBitboard) {
        if (piece != Piece.WHITE_PAWN && piece != Piece.BLACK_PAWN) {
            return;
        }

        while (movesBitboard != 0) {
            // knight
            var promoteKnight = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            promoteKnight.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteKnight.setPromotedPieceType(PieceType.KNIGHT);

            moves.add(promoteKnight);

            // bishop
            var promoteBishop = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            promoteBishop.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteBishop.setPromotedPieceType(PieceType.BISHOP);

            moves.add(promoteBishop);

            // rook
            var promoteRook = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            promoteRook.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteRook.setPromotedPieceType(PieceType.ROOK);

            moves.add(promoteRook);

            // queen
            var promoteQueen = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            promoteQueen.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteQueen.setPromotedPieceType(PieceType.QUEEN);

            moves.add(promoteQueen);

            movesBitboard &= movesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Sliding pieces (Rook, Bishop, Queen)
    //-------------------------------------------------

    /**
     * The method adds moves for the sliding pieces (Rook, Bishop or Queen).
     *
     * @param piece Specifies for which type of piece the moves are to be added.
     * @param moveFlag The {@link Move.MoveFlag}. <b>Normal</b> (for the quiet moves) and <b>Capture</b> are accepted flags.
     * @param piecesBitboard A bitboard with all the rooks, bishops or queens.
     * @param allPiecesBitboard A bitboard with all pieces.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    public void addSlidingPiecesMoves(Piece piece, Move.MoveFlag moveFlag, long piecesBitboard, long allPiecesBitboard, long possiblePositionsBitboard) {
        if (moveFlag != Move.MoveFlag.NORMAL && moveFlag != Move.MoveFlag.CAPTURE) {
            return;
        }

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

            if (moveFlag == Move.MoveFlag.NORMAL) {
                addQuietMoves(piece, fromBitIndex, movesBitboard);
            } else {
                addCaptureMoves(piece, fromBitIndex, movesBitboard);
            }

            piecesBitboard &= piecesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Add move
    //-------------------------------------------------

    /**
     * Add a move that does not capture an enemy piece.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given fromBitIndex</p>
     * <p><b>to: </b> read from the given movesBitboard</p>
     * <p><b>captured piece type: </b> NO_PIECE(0)</p>
     * <p><b>promoted piece type: </b> NO_PIECE(0)</p>
     * <p><b>special move flag: </b> NORMAL(0)</p>
     * <p><b>piece: </b> the given piece</p>
     * <p></p>
     *
     * @param piece A {@link Piece} of any color.
     * @param fromBitIndex The bit index of the given {@link Piece}.
     * @param movesBitboard A bitboard with all the moves to be added.
     */
    private void addQuietMoves(Piece piece, int fromBitIndex, long movesBitboard) {
        while (movesBitboard != 0) {
            var move = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            moves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    /**
     * Add a move that does capture an enemy piece.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given fromBitIndex</p>
     * <p><b>to: </b> read from the given movesBitboard</p>
     * <p><b>captured piece type: </b> NO_PIECE(0)</p> todo: determine
     * <p><b>promoted piece type: </b> NO_PIECE(0)</p>
     * <p><b>special move flag: </b> CAPTURE(5)</p>
     * <p><b>piece: </b> the given piece</p>
     * <p></p>
     *
     * @param piece A {@link Piece} of any color.
     * @param fromBitIndex The bit index of the given {@link Piece}.
     * @param movesBitboard A bitboard with all the moves to be added.
     */
    private void addCaptureMoves(Piece piece, int fromBitIndex, long movesBitboard) {
        while (movesBitboard != 0) {

            // todo: determine captured piece type

            var move = new Move(
                    piece,
                    fromBitIndex,
                    Long.numberOfTrailingZeros(movesBitboard)
            );

            move.setMoveFlag(Move.MoveFlag.CAPTURE);

            moves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    private void addMoves() {
        if (board.getColorToMove() == Board.Color.WHITE) {

            // pawns

            addPawnMoves(Piece.WHITE_PAWN, board.getWhitePawns(), board.getBlackPieces(), board.getAllPieces());

            // knights

            addNonslidingPiecesMoves(Piece.WHITE_KNIGHT, Move.MoveFlag.NORMAL, board.getWhiteKnights(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.WHITE_KNIGHT, Move.MoveFlag.CAPTURE, board.getWhiteKnights(), board.getBlackPieces());

            // king

            addNonslidingPiecesMoves(Piece.WHITE_KING, Move.MoveFlag.NORMAL, board.getWhiteKing(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.WHITE_KING, Move.MoveFlag.CAPTURE, board.getWhiteKing(), board.getBlackPieces());

            // rooks

            addSlidingPiecesMoves(Piece.WHITE_ROOK, Move.MoveFlag.NORMAL, board.getWhiteRooks(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.WHITE_ROOK, Move.MoveFlag.CAPTURE, board.getWhiteRooks(), board.getAllPieces(), board.getBlackPieces());

        } else {

            // pawns

            addPawnMoves(Piece.BLACK_PAWN, board.getBlackPawns(), board.getWhitePieces(), board.getAllPieces());

            // knights

            addNonslidingPiecesMoves(Piece.BLACK_KNIGHT, Move.MoveFlag.NORMAL, board.getBlackKnights(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.BLACK_KNIGHT, Move.MoveFlag.CAPTURE, board.getBlackKnights(), board.getWhitePieces());

            // king

            addNonslidingPiecesMoves(Piece.BLACK_KING, Move.MoveFlag.NORMAL, board.getBlackKing(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.BLACK_KING, Move.MoveFlag.CAPTURE, board.getBlackKing(), board.getWhitePieces());

            // rooks

            addSlidingPiecesMoves(Piece.BLACK_ROOK, Move.MoveFlag.NORMAL, board.getBlackRooks(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.BLACK_ROOK, Move.MoveFlag.CAPTURE, board.getBlackRooks(), board.getAllPieces(), board.getWhitePieces());
        }
    }
}
