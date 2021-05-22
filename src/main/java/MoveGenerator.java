/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

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
     * A list with all generated pseudo legal {@link Move} objects.
     */
    private final ArrayList<Move> pseudoLegalMoves = new ArrayList<>();

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
        generatePseudoLegalMoves();
    }

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    /**
     * Get {@link #pseudoLegalMoves}.
     *
     * @return {@link #pseudoLegalMoves}
     */
    public ArrayList<Move> getPseudoLegalMoves() {
        return pseudoLegalMoves;
    }

    //-------------------------------------------------
    // Filter
    //-------------------------------------------------

    /**
     * Filter the pseudo legal moves list by {@link Piece}.
     *
     * @param piece The {@link Piece} to filter by.
     *
     * @return The filtered list.
     */
    public List<Move> filterPseudoLegalMovesBy(Piece piece) {
        return pseudoLegalMoves
                .stream()
                .filter(e -> e.getPiece() == piece)
                .collect(Collectors.toList());
    }

    //-------------------------------------------------
    // Nonsliding pieces (knight, king)
    //-------------------------------------------------

    /**
     * The method adds pseudo legal moves for the nonsliding pieces (knight, king) except pawns.
     *
     * @param piece Specifies for which {@link Piece} the moves are to be added.
     * @param moveFlag The {@link Move.MoveFlag}. <b>Normal</b> (for the quiet moves) and <b>Capture</b> are accepted flags.
     * @param piecesBitboard A knights or king bitboard.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    private void addNonslidingPiecesMoves(Piece piece, Move.MoveFlag moveFlag, long piecesBitboard, long possiblePositionsBitboard) {
        if (moveFlag != Move.MoveFlag.NORMAL && moveFlag != Move.MoveFlag.CAPTURE) {
            return;
        }

        while (piecesBitboard != 0) {
            var fromBitIndex = Bitboard.getLsb(piecesBitboard);

            var movesBitboard = 0L;

            switch (piece) {
                case WHITE_KNIGHT:
                case BLACK_KNIGHT:
                    movesBitboard = Attack.getKnightMoves(fromBitIndex) & possiblePositionsBitboard;
                    break;
                case WHITE_KING:
                case BLACK_KING:
                    movesBitboard = Attack.getKingMoves(fromBitIndex) & possiblePositionsBitboard;
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

    /**
     * Add a king side castling move if is allowed and possible.
     *
     * @param piece Specifies for which color of king {@link Piece} the move are to be added (WHITE_KING or BLACK_KING).
     * @param kingBitboard A king bitboard.
     */
    private void addKingSideCastlingMove(Piece piece, long kingBitboard) {
        switch (piece) {
            case WHITE_KING:
                if (board.isKingSideCastlingAllowed(Board.Color.WHITE)) {
                    if (isKingSideCastlingPossible(piece)) {
                        var move = new Move(piece, Bitboard.getLsb(kingBitboard), Bitboard.BitIndex.G1_IDX);
                        move.setMoveFlag(Move.MoveFlag.CASTLING);
                        pseudoLegalMoves.add(move);
                    }
                }
                break;
            case BLACK_KING:
                if (board.isKingSideCastlingAllowed(Board.Color.BLACK)) {
                    if (isKingSideCastlingPossible(piece)) {
                        var move = new Move(piece, Bitboard.getLsb(kingBitboard), Bitboard.BitIndex.G8_IDX);
                        move.setMoveFlag(Move.MoveFlag.CASTLING);
                        pseudoLegalMoves.add(move);
                    }
                }
                break;
            default:
        }
    }

    /**
     * Check if king side castling is possible.
     *
     * @param piece A white or black king {@link Piece}.
     *
     * @return boolean
     */
    private boolean isKingSideCastlingPossible(Piece piece) {
        var bitboardToBeFree = 0L;

        switch (piece) {
            case WHITE_KING:
                // check if squares (F1, G1) between the rook and the king are free
                bitboardToBeFree = Bitboard.F1 | Bitboard.G1;
                if ((bitboardToBeFree & board.getAllPieces()) != 0) {
                    // if not, skip attack check and return false
                    return false;
                }

                // check if E1 and the squares F1, G1 are not attacked
                if (Attack.areOneOrMoreSquaresAttacked(
                        Board.Color.WHITE, board,
                        Bitboard.BitIndex.E1_IDX, Bitboard.BitIndex.F1_IDX, Bitboard.BitIndex.G1_IDX)
                ) {
                    return false;
                }

                break;
            case BLACK_KING:
                bitboardToBeFree = Bitboard.F8 | Bitboard.G8;
                if ((bitboardToBeFree & board.getAllPieces()) != 0) {
                    return false;
                }

                if (Attack.areOneOrMoreSquaresAttacked(
                        Board.Color.WHITE, board,
                        Bitboard.BitIndex.E8_IDX, Bitboard.BitIndex.F8_IDX, Bitboard.BitIndex.G8_IDX)
                ) {
                    return false;
                }

                break;
            default:
        }

        return true;
    }

    /**
     * Add queen side castling move if is allowed and possible.
     *
     * @param piece Specifies for which color of king {@link Piece} the move are to be added (WHITE_KING or BLACK_KING).
     * @param kingBitboard A king bitboard.
     */
    private void addQueenSideCastlingMove(Piece piece, long kingBitboard) {
        switch (piece) {
            case WHITE_KING:
                if (board.isQueenSideCastlingAllowed(Board.Color.WHITE)) {
                    if (isQueenSideCastlingPossible(piece)) {
                        var move = new Move(piece, Bitboard.getLsb(kingBitboard), Bitboard.BitIndex.C1_IDX);
                        move.setMoveFlag(Move.MoveFlag.CASTLING);
                        pseudoLegalMoves.add(move);
                    }
                }
                break;
            case BLACK_KING:
                if (board.isQueenSideCastlingAllowed(Board.Color.BLACK)) {
                    if (isQueenSideCastlingPossible(piece)) {
                        var move = new Move(piece, Bitboard.getLsb(kingBitboard), Bitboard.BitIndex.C8_IDX);
                        move.setMoveFlag(Move.MoveFlag.CASTLING);
                        pseudoLegalMoves.add(move);
                    }
                }
                break;
            default:
        }
    }

    /**
     * Check if queen side castling is possible.
     *
     * @param piece A white or black king {@link Piece}.
     *
     * @return boolean
     */
    private boolean isQueenSideCastlingPossible(Piece piece) {
        var bitboardToBeFree = 0L;

        switch (piece) {
            case WHITE_KING:
                // check if squares (B1, C1, D1) between the rook and the king are free
                bitboardToBeFree = Bitboard.B1 | Bitboard.C1 | Bitboard.D1;
                if ((bitboardToBeFree & board.getAllPieces()) != 0) {
                    // if not, skip attack check and return false
                    return false;
                }

                // check if E1 and the squares D1, C1 are not attacked
                if (Attack.areOneOrMoreSquaresAttacked(
                        Board.Color.WHITE, board,
                        Bitboard.BitIndex.E1_IDX, Bitboard.BitIndex.D1_IDX, Bitboard.BitIndex.C1_IDX)
                ) {
                    return false;
                }

                break;
            case BLACK_KING:
                bitboardToBeFree = Bitboard.B8 | Bitboard.C8 | Bitboard.D8;
                if ((bitboardToBeFree & board.getAllPieces()) != 0) {
                    return false;
                }

                if (Attack.areOneOrMoreSquaresAttacked(
                        Board.Color.WHITE, board,
                        Bitboard.BitIndex.E8_IDX, Bitboard.BitIndex.D8_IDX, Bitboard.BitIndex.C8_IDX)
                ) {
                    return false;
                }

                break;
            default:
        }

        return true;
    }

    //-------------------------------------------------
    // Nonsliding pieces (pawn)
    //-------------------------------------------------

    /**
     * Add pseudo legal moves for the white and black pawns.
     *
     * @param piece Specifies for which color of pawn {@link Piece} the moves are to be added (WHITE_PAWN or BLACK_PAWN).
     * @param piecesBitboard A bitboard with all the white or black pawns.
     * @param enemyPiecesBitboard A bitboard with all enemy pieces.
     * @param allPiecesBitboard A bitboard with all pieces.
     */
    private void addPawnMoves(Piece piece, long piecesBitboard, long enemyPiecesBitboard, long allPiecesBitboard) {
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
            var fromBitIndex = Bitboard.getLsb(piecesBitboard);

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

        if (board.getEpIndex() != Bitboard.BitIndex.NO_SQUARE) {
            while (whitePawnsBitboard != 0) {
                var enemyDestination = board.getEpIndex().ordinal() - 8;
                var fromBitIndex = Bitboard.getLsb(whitePawnsBitboard);

                if (abs(fromBitIndex.ordinal() - enemyDestination) == 1) {
                    var move = new Move(Piece.WHITE_PAWN, fromBitIndex, board.getEpIndex());
                    move.setMoveFlag(Move.MoveFlag.ENPASSANT);
                    pseudoLegalMoves.add(move);
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

        if (board.getEpIndex() != Bitboard.BitIndex.NO_SQUARE) {
            while (blackPawnsBitboard != 0) {
                var enemyDestination = board.getEpIndex().ordinal() + 8;
                var fromBitIndex = Bitboard.getLsb(blackPawnsBitboard);

                if (abs(fromBitIndex.ordinal() - enemyDestination) == 1) {
                    var move = new Move(Piece.BLACK_PAWN, fromBitIndex, board.getEpIndex());
                    move.setMoveFlag(Move.MoveFlag.ENPASSANT);
                    pseudoLegalMoves.add(move);
                }

                blackPawnsBitboard &= blackPawnsBitboard - 1;
            }
        }
    }

    /**
     * Computing white pawn pseudo legal movements.
     *
     * @see <a href="http://pages.cs.wisc.edu/~psilord/blog/data/chess-pages/nonsliding.html">Nonsliding Pieces</a>
     *
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the white pawn.
     * @param blackPiecesBitboard The bitboard with all black pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     */
    private void addWhitePawnMoves(Bitboard.BitIndex fromBitIndex, long blackPiecesBitboard, long allPiecesBitboard) {
        long whitePawnBitboard = Bitboard.SQUARES[fromBitIndex.ordinal()];

        // check the single space infront of the white pawn
        long firstStepBitboard = (whitePawnBitboard << 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_3) << 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.getWhitePawnAttacks(fromBitIndex) & blackPiecesBitboard;

        // add moves
        addQuietMoves(Piece.WHITE_PAWN, fromBitIndex, firstStepBitboard & Bitboard.CLEAR_RANK_8);
        addPawnStartMoves(Piece.WHITE_PAWN, fromBitIndex, twoStepsBitboard);
        addPromotionMoves(Piece.WHITE_PAWN, fromBitIndex, firstStepBitboard & Bitboard.MASK_RANK_8);
        addCaptureMoves(Piece.WHITE_PAWN, fromBitIndex, attacksBitboard & Bitboard.CLEAR_RANK_8);
        addPromotionMoves(Piece.WHITE_PAWN, fromBitIndex, attacksBitboard & Bitboard.MASK_RANK_8);
    }

    /**
     * Computing black pawn pseudo legal movements.
     *
     * @see <a href="http://pages.cs.wisc.edu/~psilord/blog/data/chess-pages/nonsliding.html">Nonsliding Pieces</a>
     *
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the black pawn.
     * @param whitePiecesBitboard The bitboard with all white pieces (enemies).
     * @param allPiecesBitboard A bitboard with all pieces. All pieces can block.
     */
    private void addBlackPawnMoves(Bitboard.BitIndex fromBitIndex, long whitePiecesBitboard, long allPiecesBitboard) {
        long blackPawnBitboard = Bitboard.SQUARES[fromBitIndex.ordinal()];

        // check the single space infront of the black pawn
        long firstStepBitboard = (blackPawnBitboard >>> 8) & ~allPiecesBitboard;

        // check and see if the pawn can move forward one more
        long twoStepsBitboard = ((firstStepBitboard & Bitboard.MASK_RANK_6) >>> 8) & ~allPiecesBitboard;

        // calc attacks
        long attacksBitboard = Attack.getBlackPawnAttacks(fromBitIndex) & whitePiecesBitboard;

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
     * <p><b>from: </b> the given {@link Bitboard.BitIndex}</p>
     * <p><b>to: </b> read {@link Bitboard.BitIndex} from the given movesBitboard</p>
     * <p><b>captured {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>promoted {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>special {@link Move.MoveFlag}: </b> PAWN_START(4)</p>
     * <p><b>piece: </b> the given {@link Piece}</p>
     * <p></p>
     *
     * @param piece A pawn of any color.
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the given pawn.
     * @param movesBitboard A bitboard with all the pawn start moves to be added.
     */
    private void addPawnStartMoves(Piece piece, Bitboard.BitIndex fromBitIndex, long movesBitboard) {
        if (piece != Piece.WHITE_PAWN && piece != Piece.BLACK_PAWN) {
            return;
        }

        while (movesBitboard != 0) {
            var move = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            move.setMoveFlag(Move.MoveFlag.PAWN_START);

            pseudoLegalMoves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    /**
     * Add promotion and promotion capture moves.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given {@link Bitboard.BitIndex}</p>
     * <p><b>to: </b> read {@link Bitboard.BitIndex} from the given movesBitboard</p>
     * <p><b>captured {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>promoted {@link PieceType}: </b> KNIGHT(2) - QUEEN(5)</p>
     * <p><b>special {@link Move.MoveFlag}: </b> PROMOTION(1)</p>
     * <p><b>piece: </b> the given {@link Piece}</p>
     * <p></p>
     *
     * @param piece A pawn of any color.
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the given pawn.
     * @param movesBitboard A bitboard with all moves to be added.
     */
    private void addPromotionMoves(Piece piece, Bitboard.BitIndex fromBitIndex, long movesBitboard) {
        if (piece != Piece.WHITE_PAWN && piece != Piece.BLACK_PAWN) {
            return;
        }

        while (movesBitboard != 0) {
            // knight
            var promoteKnight = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            promoteKnight.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteKnight.setPromotedPieceType(PieceType.KNIGHT);
            pseudoLegalMoves.add(promoteKnight);

            // bishop
            var promoteBishop = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            promoteBishop.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteBishop.setPromotedPieceType(PieceType.BISHOP);
            pseudoLegalMoves.add(promoteBishop);

            // rook
            var promoteRook = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            promoteRook.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteRook.setPromotedPieceType(PieceType.ROOK);
            pseudoLegalMoves.add(promoteRook);

            // queen
            var promoteQueen = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            promoteQueen.setMoveFlag(Move.MoveFlag.PROMOTION);
            promoteQueen.setPromotedPieceType(PieceType.QUEEN);
            pseudoLegalMoves.add(promoteQueen);

            movesBitboard &= movesBitboard - 1;
        }
    }

    //-------------------------------------------------
    // Sliding pieces (rook, bishop, queen)
    //-------------------------------------------------

    /**
     * The method adds pseudo legal moves for the sliding pieces (rook, bishop or queen).
     *
     * @param piece Specifies for which type of {@link Piece} the moves are to be added.
     * @param moveFlag The {@link Move.MoveFlag}. <b>Normal</b> (for the quiet moves) and <b>Capture</b> are accepted flags.
     * @param piecesBitboard A bitboard with all the rooks, bishops or queens.
     * @param allPiecesBitboard A bitboard with all pieces.
     * @param possiblePositionsBitboard This prevents own pieces from being captured.
     */
    private void addSlidingPiecesMoves(Piece piece, Move.MoveFlag moveFlag, long piecesBitboard, long allPiecesBitboard, long possiblePositionsBitboard) {
        if (moveFlag != Move.MoveFlag.NORMAL && moveFlag != Move.MoveFlag.CAPTURE) {
            return;
        }

        while (piecesBitboard != 0) {
            var fromBitIndex = Bitboard.getLsb(piecesBitboard);

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
    // Legal moves
    //-------------------------------------------------

    public ArrayList<Move> generateLegalMoves() {
        var moves = new ArrayList<Move>();

        // alle king attackers holen
        board.updateKingAttackers();

        // is check?
        if (board.kingAttackers != 0) {
            System.out.println("Is check");

            Bitboard.printBitboard(board.kingAttackers);

            // generateEvasionMoves

            // slider herausarbeiten
            var sliders = board.kingAttackers & ~board.getBlackPawns() & ~board.getBlackKnights();
            Bitboard.printBitboard(sliders);

            var t = 0;
        }

        // generatePseudoLegalMoves

        return moves;
    }

    //-------------------------------------------------
    // Pseudo legal moves
    //-------------------------------------------------

    /**
     * Add a pseudo legal move that does not capture an enemy piece.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given {@link Bitboard.BitIndex}</p>
     * <p><b>to: </b> read {@link Bitboard.BitIndex} from the given movesBitboard</p>
     * <p><b>captured {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>promoted {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>special {@link Move.MoveFlag}: </b> NORMAL(0)</p>
     * <p><b>piece: </b> the given {@link Piece}</p>
     * <p></p>
     *
     * @param piece A {@link Piece} of any color.
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the given {@link Piece}.
     * @param movesBitboard A bitboard with all the moves to be added.
     */
    private void addQuietMoves(Piece piece, Bitboard.BitIndex fromBitIndex, long movesBitboard) {
        while (movesBitboard != 0) {
            var move = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            pseudoLegalMoves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    /**
     * Add a pseudo legal move that does capture an enemy piece.
     *
     * Stores move information as follows:
     * <p></p>
     * <p><b>from: </b> the given {@link Bitboard.BitIndex}</p>
     * <p><b>to: </b> read {@link Bitboard.BitIndex} from the given movesBitboard</p>
     * <p><b>captured {@link PieceType}: </b> NO_PIECE(0)</p> todo: determine
     * <p><b>promoted {@link PieceType}: </b> NO_PIECE(0)</p>
     * <p><b>special {@link Move.MoveFlag}: </b> CAPTURE(5)</p>
     * <p><b>piece: </b> the given {@link Piece}</p>
     * <p></p>
     *
     * @param piece A {@link Piece} of any color.
     * @param fromBitIndex The {@link Bitboard.BitIndex} of the given {@link Piece}.
     * @param movesBitboard A bitboard with all the moves to be added.
     */
    private void addCaptureMoves(Piece piece, Bitboard.BitIndex fromBitIndex, long movesBitboard) {
        while (movesBitboard != 0) {

            // todo: determine captured piece type

            var move = new Move(
                    piece,
                    fromBitIndex,
                    Bitboard.getLsb(movesBitboard)
            );

            move.setMoveFlag(Move.MoveFlag.CAPTURE);

            pseudoLegalMoves.add(move);

            movesBitboard &= movesBitboard - 1;
        }
    }

    /**
     * Generate and add all pseudo legal moves.
     */
    private void generatePseudoLegalMoves() {
        if (board.getColorToMove() == Board.Color.WHITE) {
            // pawns

            addPawnMoves(Piece.WHITE_PAWN, board.getWhitePawns(), board.getBlackPieces(), board.getAllPieces());

            // knights

            addNonslidingPiecesMoves(Piece.WHITE_KNIGHT, Move.MoveFlag.NORMAL, board.getWhiteKnights(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.WHITE_KNIGHT, Move.MoveFlag.CAPTURE, board.getWhiteKnights(), board.getBlackPieces());

            // king

            addNonslidingPiecesMoves(Piece.WHITE_KING, Move.MoveFlag.NORMAL, board.getWhiteKing(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.WHITE_KING, Move.MoveFlag.CAPTURE, board.getWhiteKing(), board.getBlackPieces());
            addKingSideCastlingMove(Piece.WHITE_KING, board.getWhiteKing());
            addQueenSideCastlingMove(Piece.WHITE_KING, board.getWhiteKing());

            // rooks

            addSlidingPiecesMoves(Piece.WHITE_ROOK, Move.MoveFlag.NORMAL, board.getWhiteRooks(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.WHITE_ROOK, Move.MoveFlag.CAPTURE, board.getWhiteRooks(), board.getAllPieces(), board.getBlackPieces());

            // queens

            addSlidingPiecesMoves(Piece.WHITE_QUEEN, Move.MoveFlag.NORMAL, board.getWhiteQueens(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.WHITE_QUEEN, Move.MoveFlag.CAPTURE, board.getWhiteQueens(), board.getAllPieces(), board.getBlackPieces());

            // bishops

            addSlidingPiecesMoves(Piece.WHITE_BISHOP, Move.MoveFlag.NORMAL, board.getWhiteBishops(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.WHITE_BISHOP, Move.MoveFlag.CAPTURE, board.getWhiteBishops(), board.getAllPieces(), board.getBlackPieces());
        } else {
            // pawns

            addPawnMoves(Piece.BLACK_PAWN, board.getBlackPawns(), board.getWhitePieces(), board.getAllPieces());

            // knights

            addNonslidingPiecesMoves(Piece.BLACK_KNIGHT, Move.MoveFlag.NORMAL, board.getBlackKnights(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.BLACK_KNIGHT, Move.MoveFlag.CAPTURE, board.getBlackKnights(), board.getWhitePieces());

            // king

            addNonslidingPiecesMoves(Piece.BLACK_KING, Move.MoveFlag.NORMAL, board.getBlackKing(), ~board.getAllPieces());
            addNonslidingPiecesMoves(Piece.BLACK_KING, Move.MoveFlag.CAPTURE, board.getBlackKing(), board.getWhitePieces());
            addKingSideCastlingMove(Piece.BLACK_KING, board.getBlackKing());
            addQueenSideCastlingMove(Piece.BLACK_KING, board.getBlackKing());

            // rooks

            addSlidingPiecesMoves(Piece.BLACK_ROOK, Move.MoveFlag.NORMAL, board.getBlackRooks(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.BLACK_ROOK, Move.MoveFlag.CAPTURE, board.getBlackRooks(), board.getAllPieces(), board.getWhitePieces());

            // queens

            addSlidingPiecesMoves(Piece.BLACK_QUEEN, Move.MoveFlag.NORMAL, board.getBlackQueens(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.BLACK_QUEEN, Move.MoveFlag.CAPTURE, board.getBlackQueens(), board.getAllPieces(), board.getWhitePieces());

            // bishops

            addSlidingPiecesMoves(Piece.BLACK_BISHOP, Move.MoveFlag.NORMAL, board.getBlackBishops(), board.getAllPieces(), ~board.getAllPieces());
            addSlidingPiecesMoves(Piece.BLACK_BISHOP, Move.MoveFlag.CAPTURE, board.getBlackBishops(), board.getAllPieces(), board.getWhitePieces());
        }
    }
}
