/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.util.List;
import java.util.Objects;

/**
 * Represents a Board object.
 */
public class Board {

    //-------------------------------------------------
    // Color
    //-------------------------------------------------

    public enum Color {
        WHITE(0, 1), BLACK(1, -1), NONE(2, 0);

        /**
         * The ordinal value.
         */
        public int value;

        /**
         * Used for evaluation calculations.
         * A positive number means that white's position is better.
         * A negative number means things look better for black.
         */
        public int sign;

        public Color getEnemyColor() {
            if (this.value == 0) {
                return BLACK;
            }

            if (this.value == 1) {
                return WHITE;
            }

            return NONE;
        }

        Color(int value, int sign) {
            this.value = value;
            this.sign = sign;
        }
    }

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    /**
     * All attributes off.
     */
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * The FEN for the starting position.
     */
    private static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The bitboards to represent the positions of each kind and color of piece on a chessboard.
     */
    private final long[] bitboards = new long[Bitboard.NUMBER_OF_BITBOARDS];

    /**
     * A bitboard with all the pieces.
     */
    private long allPiecesBitboard = 0L;

    /**
     * Denoting who is on the move.
     */
    private Color colorToMove = Color.WHITE;

    /**
     * A packed integer containing castling rights.
     * The order is 1111 = qkQK
     */
    private int castlingRights;

    /**
     * A packed integer containing the old {@link #castlingRights}.
     */
    private int oldCastlingRights;

    /**
     * The {@link Bitboard.BitIndex} of the En Passant target square.
     */
    private Bitboard.BitIndex epIndex = Bitboard.BitIndex.NO_SQUARE;

    /**
     * The {@link Bitboard.BitIndex} of the old {@link #epIndex}.
     */
    private Bitboard.BitIndex oldEpIndex = Bitboard.BitIndex.NO_SQUARE;

    /**
     * The current Zobrist key.
     */
    private long zkey;

    /**
     * The old Zobrist key.
     */
    private long oldZkey;

    /**
     * The number of halfmoves since the last capture or pawn advance, used for the fifty-move rule.
     */
    private int halfMovesCounter;

    /**
     * The old {@link #halfMovesCounter} value.
     */
    private int oldHalfMovesCounter;

    /**
     * The number of the full move. It starts at 1, and is incremented after Black's move.
     */
    private int movesCounter;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Board} object.
     */
    public Board() {
        this(FEN_START);
    }

    /**
     * Constructs a new {@link Board} object.
     *
     * @param fen A particular FEN board position.
     */
    public Board(String fen) {
        initWithFen(Objects.requireNonNull(fen, "fen must not be null"));
        updateCommonBitboards();
    }

    //-------------------------------------------------
    // Getter - white pieces
    //-------------------------------------------------

    public long getWhitePawns() {
        return bitboards[Bitboard.WHITE_PAWNS_BITBOARD];
    }

    public long getWhiteKnights() {
        return bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD];
    }

    public long getWhiteBishops() {
        return bitboards[Bitboard.WHITE_BISHOPS_BITBOARD];
    }

    public long getWhiteRooks() {
        return bitboards[Bitboard.WHITE_ROOKS_BITBOARD];
    }

    public long getWhiteQueens() {
        return bitboards[Bitboard.WHITE_QUEENS_BITBOARD];
    }

    public long getWhiteKing() {
        return bitboards[Bitboard.WHITE_KING_BITBOARD];
    }

    //-------------------------------------------------
    // Getter - black pieces
    //-------------------------------------------------

    public long getBlackPawns() {
        return bitboards[Bitboard.BLACK_PAWNS_BITBOARD];
    }

    public long getBlackKnights() {
        return bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD];
    }

    public long getBlackBishops() {
        return bitboards[Bitboard.BLACK_BISHOPS_BITBOARD];
    }

    public long getBlackRooks() {
        return bitboards[Bitboard.BLACK_ROOKS_BITBOARD];
    }

    public long getBlackQueens() {
        return bitboards[Bitboard.BLACK_QUEENS_BITBOARD];
    }

    public long getBlackKing() {
        return bitboards[Bitboard.BLACK_KING_BITBOARD];
    }

    //-------------------------------------------------
    // Getter - all pieces
    //-------------------------------------------------

    public long getWhitePieces() {
        return bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD];
    }

    public long getBlackPieces() {
        return bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD];
    }

    public long getAllPawns() {
        return bitboards[Bitboard.ALL_PAWNS_BITBOARD];
    }

    public long getAllKnights() {
        return bitboards[Bitboard.ALL_KNIGHTS_BITBOARD];
    }

    public long getAllBishops() {
        return bitboards[Bitboard.ALL_BISHOPS_BITBOARD];
    }

    public long getAllRooks() {
        return bitboards[Bitboard.ALL_ROOKS_BITBOARD];
    }

    public long getAllQueens() {
        return bitboards[Bitboard.ALL_QUEENS_BITBOARD];
    }

    public long getAllKings() {
        return bitboards[Bitboard.ALL_KINGS_BITBOARD];
    }

    public long getAllPieces() {
        return allPiecesBitboard;
    }

    //-------------------------------------------------
    // Getter - pieces by color
    //-------------------------------------------------

    public long getPawns(Color color) {
        return color == Color.WHITE ? getWhitePawns() : getBlackPawns();
    }

    public long getKnights(Color color) {
        return color == Color.WHITE ? getWhiteKnights() : getBlackKnights();
    }

    public long getBishops(Color color) {
        return color == Color.WHITE ? getWhiteBishops() : getBlackBishops();
    }

    public long getRooks(Color color) {
        return color == Color.WHITE ? getWhiteRooks() : getBlackRooks();
    }

    public long getQueens(Color color) {
        return color == Color.WHITE ? getWhiteQueens() : getBlackQueens();
    }

    public long getKing(Color color) {
        return color == Color.WHITE ? getWhiteKing() : getBlackKing();
    }

    //-------------------------------------------------
    // Getter piece
    //-------------------------------------------------

    /**
     * Checks whether there is an opponent's pawn on the left or right.
     *
     * @param bitIndexValue The position bit index value of the own pawn.
     * @param color {@link Color} of te own pawn.
     *
     * @return boolean
     */
    public boolean isNeighborAnEnemyPawn(int bitIndexValue, Color color) {
        var pawnsBitboard = color == Color.WHITE ? getBlackPawns() : getWhitePawns();

        var leftBitboard = Bitboard.SQUARES[bitIndexValue - 1];
        var rightBitboard = Bitboard.SQUARES[bitIndexValue + 1];

        return (leftBitboard & pawnsBitboard) != 0 || (rightBitboard & pawnsBitboard) != 0;
    }

    /**
     * Get the {@link Piece} from a given {@link Bitboard.BitIndex}.
     *
     * @param bitIndex {@link Bitboard.BitIndex}
     *
     * @return {@link Piece}
     */
    public Piece getPieceFrom(Bitboard.BitIndex bitIndex) {
        var toBitboard = Bitboard.getSquareBitboardFromBitIndex(bitIndex);

        if ((toBitboard & getWhitePawns()) != 0) {
            return Piece.WHITE_PAWN;
        }
        if ((toBitboard & getWhiteKnights()) != 0) {
            return Piece.WHITE_KNIGHT;
        }
        if ((toBitboard & getWhiteBishops()) != 0) {
            return Piece.WHITE_BISHOP;
        }
        if ((toBitboard & getWhiteRooks()) != 0) {
            return Piece.WHITE_ROOK;
        }
        if ((toBitboard & getWhiteQueens()) != 0) {
            return Piece.WHITE_QUEEN;
        }
        if ((toBitboard & getWhiteKing()) != 0) {
            return Piece.WHITE_KING;
        }

        if ((toBitboard & getBlackPawns()) != 0) {
            return Piece.BLACK_PAWN;
        }
        if ((toBitboard & getBlackKnights()) != 0) {
            return Piece.BLACK_KNIGHT;
        }
        if ((toBitboard & getBlackBishops()) != 0) {
            return Piece.BLACK_BISHOP;
        }
        if ((toBitboard & getBlackRooks()) != 0) {
            return Piece.BLACK_ROOK;
        }
        if ((toBitboard & getBlackQueens()) != 0) {
            return Piece.BLACK_QUEEN;
        }
        if ((toBitboard & getBlackKing()) != 0) {
            return Piece.BLACK_KING;
        }

        return Piece.NO_PIECE;
    }

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    /**
     * Get {@link #bitboards}.
     *
     * @return {@link #bitboards}
     */
    public long[] getBitboards() {
        return bitboards;
    }

    /**
     * Get {@link #colorToMove}.
     *
     * @return {@link #colorToMove}
     */
    public Color getColorToMove() {
        return colorToMove;
    }

    /**
     * Get {@link #castlingRights}.
     *
     * @return {@link #castlingRights}
     */
    public int getCastlingRights() {
        return castlingRights;
    }

    /**
     * Get {@link #epIndex}.
     *
     * @return {@link #epIndex}
     */
    public Bitboard.BitIndex getEpIndex() {
        return epIndex;
    }

    /**
     * Get {@link #oldEpIndex}.
     *
     * @return {@link #oldEpIndex}
     */
    public Bitboard.BitIndex getOldEpIndex() {
        return oldEpIndex;
    }

    /**
     * Get {@link #zkey}.
     *
     * @return {@link #zkey}
     */
    public long getZkey() {
        return zkey;
    }

    //-------------------------------------------------
    // Setter
    //-------------------------------------------------

    /**
     * Set {@link #colorToMove}
     *
     * @param colorToMove {@link Color}
     */
    public void setColorToMove(Color colorToMove) {
        this.colorToMove = colorToMove;
    }

    /**
     * Set {@link #zkey}.
     *
     * @param zkey The new Zobrist key.
     */
    public void setZkey(long zkey) {
        this.zkey = zkey;
    }

    //-------------------------------------------------
    // Castling
    //-------------------------------------------------

    /**
     * Check if queen side castling is allowed.
     *
     * @param color The {@link Color} for which to check.
     *
     * @return boolean
     */
    public boolean isQueenSideCastlingAllowed(Color color) {
        // bit pos 1 and 3
        return ((castlingRights >>> (1 + 2 * color.value)) & 1) != 0;
    }

    /**
     * Check if king side castling is allowed.
     *
     * @param color The {@link Color} for which to check.
     *
     * @return boolean
     */
    public boolean isKingSideCastlingAllowed(Color color) {
        // bis pos 0 and 2
        return ((castlingRights >>> (2 * color.value)) & 1) != 0;
    }

    //-------------------------------------------------
    // Zobrist
    //-------------------------------------------------

    private void xorWhiteColorToMove() {
        zkey ^= Zkey.whiteColorToMove;
    }

    private void xorCastlingRights(int castlingRights) {
        zkey ^= Zkey.castlingRights[castlingRights];
    }

    private void xorEpIndex(Bitboard.BitIndex epIndex) {
        zkey ^= Zkey.epIndex[epIndex.ordinal()];
    }

    private void xorPiece(int color, int pieceType, int square) {
        zkey ^= Zkey.piece[color][pieceType][square];
    }

    //-------------------------------------------------
    // Make / undo
    //-------------------------------------------------

    /**
     * Executes a given {@link Move}.
     *
     * @param move {@link Move}
     *
     * @return Returns false if the {@link Move} is illegal; otherwise true.
     */
    public boolean makeMove(Move move) {
        // store zkey
        oldZkey = zkey;

        if (move.getMoveFlag() == Move.MoveFlag.NORMAL) {
            movePiece(move.getFrom(), move.getTo(), move.getPiece().pieceType, colorToMove);

            var colorToMoveValue = colorToMove.value;
            var pieceTypeValue = move.getPiece().pieceType.value;

            xorPiece(colorToMoveValue, pieceTypeValue, move.getFrom());
            xorPiece(colorToMoveValue, pieceTypeValue, move.getTo());

            oldEpIndex = epIndex;
            epIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var colorToMoveValue = colorToMove.value;

            // remove captured piece
            if (move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
                removePiece(move.getTo(), PieceType.getBitboardNumber(move.getCapturedPieceType(), colorToMove.getEnemyColor()));
                xorPiece(colorToMove.getEnemyColor().value, move.getCapturedPieceType().value, move.getTo());
            }

            // remove own pawn
            removePiece(move.getFrom(), PieceType.getBitboardNumber(PieceType.PAWN, colorToMove));
            xorPiece(colorToMoveValue, PieceType.PAWN.value, move.getFrom());

            // add own promoted piece
            addPiece(move.getTo(), PieceType.getBitboardNumber(move.getPromotedPieceType(), colorToMove));
            xorPiece(colorToMoveValue, move.getPromotedPieceType().value, move.getTo());

            oldEpIndex = epIndex;
            epIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.EN_PASSANT) {
            var colorToMoveValue = colorToMove.value;
            var pieceTypeValue = move.getPiece().pieceType.value;

            var removeFrom = move.getTo();
            if (colorToMove == Color.WHITE) {
                removeFrom -= 8;
            } else {
                removeFrom += 8;
            }

            // remove captured pawn
            removePiece(removeFrom, PieceType.getBitboardNumber(PieceType.PAWN, colorToMove.getEnemyColor()));
            xorPiece(colorToMove.getEnemyColor().value, PieceType.PAWN.value, removeFrom);

            // move own pawn to epIndex/move.getTo()
            movePiece(move.getFrom(), move.getTo(), move.getPiece().pieceType, colorToMove);
            xorPiece(colorToMoveValue, pieceTypeValue, move.getFrom());
            xorPiece(colorToMoveValue, pieceTypeValue, move.getTo());

            oldEpIndex = epIndex;
            epIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.CASTLING) {
            var colorToMoveValue = colorToMove.value;
            var pieceTypeValue = move.getPiece().pieceType.value;

            // king
            movePiece(move.getFrom(), move.getTo(), move.getPiece().pieceType, colorToMove);
            xorPiece(colorToMoveValue, pieceTypeValue, move.getFrom());
            xorPiece(colorToMoveValue, pieceTypeValue, move.getTo());

            // determine rook from and to squares
            var rookOrigin = Bitboard.BitIndex.NO_SQUARE;
            var rookDestination = Bitboard.BitIndex.NO_SQUARE;

            if (colorToMove == Color.WHITE) {
                // short
                if (move.getTo() == Bitboard.BitIndex.G1_IDX.ordinal()) {
                    rookOrigin = Bitboard.BitIndex.H1_IDX;
                    rookDestination = Bitboard.BitIndex.F1_IDX;
                } else {
                    // long
                    rookOrigin = Bitboard.BitIndex.A1_IDX;
                    rookDestination = Bitboard.BitIndex.D1_IDX;
                }
            }

            if (colorToMove == Color.BLACK) {
                // short
                if (move.getTo() == Bitboard.BitIndex.G8_IDX.ordinal()) {
                    rookOrigin = Bitboard.BitIndex.H8_IDX;
                    rookDestination = Bitboard.BitIndex.F8_IDX;
                } else {
                    // long
                    rookOrigin = Bitboard.BitIndex.A8_IDX;
                    rookDestination = Bitboard.BitIndex.D8_IDX;
                }
            }

            // rook
            movePiece(rookOrigin.ordinal(), rookDestination.ordinal(), PieceType.ROOK, colorToMove);
            xorPiece(colorToMoveValue, PieceType.ROOK.value, rookOrigin.ordinal());
            xorPiece(colorToMoveValue, PieceType.ROOK.value, rookDestination.ordinal());

            oldEpIndex = epIndex;
            epIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.PAWN_START) {
            var colorToMoveValue = colorToMove.value;
            var pieceTypeValue = move.getPiece().pieceType.value;

            movePiece(move.getFrom(), move.getTo(), move.getPiece().pieceType, colorToMove);
            xorPiece(colorToMoveValue, pieceTypeValue, move.getFrom());
            xorPiece(colorToMoveValue, pieceTypeValue, move.getTo());

            // check if there is a pawn on the left or on the right
            if (isNeighborAnEnemyPawn(move.getTo(), colorToMove)) {
                // epIndex must be updated
                oldEpIndex = epIndex;
                if (colorToMove == Color.WHITE) {
                    epIndex = Bitboard.BitIndex.values()[move.getTo() - 8];
                } else {
                    epIndex = Bitboard.BitIndex.values()[move.getTo() + 8];
                }
            }
        }

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE) {
            var colorToMoveValue = colorToMove.value;
            var pieceTypeValue = move.getPiece().pieceType.value;

            removePiece(move.getTo(), PieceType.getBitboardNumber(move.getCapturedPieceType(), colorToMove.getEnemyColor()));
            xorPiece(colorToMove.getEnemyColor().value, move.getCapturedPieceType().value, move.getTo());

            movePiece(move.getFrom(), move.getTo(), move.getPiece().pieceType, colorToMove);
            xorPiece(colorToMoveValue, pieceTypeValue, move.getFrom());
            xorPiece(colorToMoveValue, pieceTypeValue, move.getTo());

            oldEpIndex = epIndex;
            epIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        // update castling rights
        oldCastlingRights = castlingRights;
        castlingRights &= Bitboard.CASTLING_RIGHTS[move.getFrom()];
        castlingRights &= Bitboard.CASTLING_RIGHTS[move.getTo()];

        // store current color
        var oldColor = colorToMove;

        // change color to move && update bitboards
        colorToMove = colorToMove.getEnemyColor();
        updateCommonBitboards();

        // update zkey (color, castling, epIndex)
        xorWhiteColorToMove();

        if (oldCastlingRights != castlingRights) {
            xorCastlingRights(oldCastlingRights);
            xorCastlingRights(castlingRights);
        }

        if (oldEpIndex != epIndex) {
            if (oldEpIndex != Bitboard.BitIndex.NO_SQUARE) {
                xorEpIndex(oldEpIndex);
            }

            if (epIndex != Bitboard.BitIndex.NO_SQUARE) {
                xorEpIndex(epIndex);
            }
        }

        // todo add the zkey to a map

        // update move counter
        movesCounter += oldColor.value; // inc only it was a black move

        if (move.getPiece().pieceType == PieceType.PAWN ||
                move.getMoveFlag() == Move.MoveFlag.CAPTURE ||
                move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE
        ) {
            oldHalfMovesCounter = halfMovesCounter;
            halfMovesCounter = 0;
        } else {
            oldHalfMovesCounter = halfMovesCounter;
            halfMovesCounter++;
        }

        // check if it was legal
        if (Attack.isCheck(oldColor, this)) {
            undoMove(move);
            return false; // return illegal move
        }

        return true; // return legal move
    }

    /**
     * Restores a {@link Move}.
     *
     * @param move {@link Move}
     */
    public void undoMove(Move move) {
        // undo Zobrist key
        zkey = oldZkey;

        // switch side to move
        colorToMove = colorToMove.getEnemyColor();

        // undo moves counter
        if (colorToMove == Color.BLACK) {
            movesCounter--;
        }

        // undo half moves counter
        halfMovesCounter = oldHalfMovesCounter;

        if (move.getMoveFlag() == Move.MoveFlag.NORMAL) {
            movePiece(move.getTo(), move.getFrom(), move.getPiece().pieceType, colorToMove);

            epIndex = oldEpIndex;
            oldEpIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            if (move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
                addPiece(move.getTo(), PieceType.getBitboardNumber(move.getCapturedPieceType(), colorToMove.getEnemyColor()));
            }

            removePiece(move.getTo(), PieceType.getBitboardNumber(move.getPromotedPieceType(), colorToMove));
            addPiece(move.getFrom(), PieceType.getBitboardNumber(PieceType.PAWN, colorToMove));

            epIndex = oldEpIndex;
            oldEpIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.EN_PASSANT) {
            var addTo = move.getTo();
            if (colorToMove == Color.WHITE) {
                addTo -= 8;
            } else {
                addTo += 8;
            }

            // add captured pawn
            addPiece(addTo, PieceType.getBitboardNumber(PieceType.PAWN, colorToMove.getEnemyColor()));

            // move own pawn back
            movePiece(move.getTo(), move.getFrom(), move.getPiece().pieceType, colorToMove);

            epIndex = oldEpIndex;
            oldEpIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.CASTLING) {
            // king
            movePiece(move.getTo(), move.getFrom(), move.getPiece().pieceType, colorToMove);

            var rookOrigin = Bitboard.BitIndex.NO_SQUARE;
            var rookDestination = Bitboard.BitIndex.NO_SQUARE;

            if (colorToMove == Color.WHITE) {
                // short
                if (move.getTo() == Bitboard.BitIndex.G1_IDX.ordinal()) {
                    rookOrigin = Bitboard.BitIndex.H1_IDX;
                    rookDestination = Bitboard.BitIndex.F1_IDX;
                } else {
                    // long
                    rookOrigin = Bitboard.BitIndex.A1_IDX;
                    rookDestination = Bitboard.BitIndex.D1_IDX;
                }
            }

            if (colorToMove == Color.BLACK) {
                // short
                if (move.getTo() == Bitboard.BitIndex.G8_IDX.ordinal()) {
                    rookOrigin = Bitboard.BitIndex.H8_IDX;
                    rookDestination = Bitboard.BitIndex.F8_IDX;
                } else {
                    // long
                    rookOrigin = Bitboard.BitIndex.A8_IDX;
                    rookDestination = Bitboard.BitIndex.D8_IDX;
                }
            }

            // rook
            movePiece(rookDestination.ordinal(), rookOrigin.ordinal(), PieceType.ROOK, colorToMove);

            epIndex = oldEpIndex;
            oldEpIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        if (move.getMoveFlag() == Move.MoveFlag.PAWN_START) {
            movePiece(move.getTo(), move.getFrom(), move.getPiece().pieceType, colorToMove);

            epIndex = oldEpIndex;
        }

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE) {
            movePiece(move.getTo(), move.getFrom(), move.getPiece().pieceType, colorToMove);
            addPiece(move.getTo(), PieceType.getBitboardNumber(move.getCapturedPieceType(), colorToMove.getEnemyColor()));

            epIndex = oldEpIndex;
            oldEpIndex = Bitboard.BitIndex.NO_SQUARE;
        }

        // undo castling rights
        castlingRights = oldCastlingRights;

        // update bitboards
        updateCommonBitboards();
    }

    //-------------------------------------------------
    // Move / add / remove
    //-------------------------------------------------

    /**
     * Convenience method for normal moves.
     *
     * @param fromBitIndex The origin {@link Bitboard.BitIndex}.
     * @param toBitIndex The destination {@link Bitboard.BitIndex}.
     * @param pieceType {@link PieceType}
     * @param color {@link Color}
     */
    public void movePiece(int fromBitIndex, int toBitIndex, PieceType pieceType, Color color) {
        var pieceBitboard = PieceType.getBitboardNumber(pieceType, color);

        removePiece(fromBitIndex, pieceBitboard);
        addPiece(toBitIndex, pieceBitboard);
    }

    /**
     * Add piece to destination.
     *
     * @param bitIndex The destination {@link Bitboard.BitIndex}.
     * @param bitboardNr The piece's bitboard index.
     */
    private void addPiece(int bitIndex, int bitboardNr) {
        bitboards[bitboardNr] |= Bitboard.SQUARES[bitIndex];
    }

    /**
     * Remove piece from origin position.
     *
     * @param bitIndex The origin {@link Bitboard.BitIndex}.
     * @param bitboardNr The piece's bitboard index.
     */
    private void removePiece(int bitIndex, int bitboardNr) {
        bitboards[bitboardNr] &= ~(Bitboard.SQUARES[bitIndex]);
    }

    //-------------------------------------------------
    // Parse move
    //-------------------------------------------------

    /**
     * Takes a string and returns the corresponding {@link Move} object or null.
     *
     * @param userInput The move as a string (e2e4 or e7e8q).
     *
     * @return A {@link Move} or null.
     */
    public Move parseMove(String userInput) {
        /*
        [0] get vertical file (a-h)
        [1] get horizontal rank (1-8)

        [2] get vertical file (a-h)
        [3] get horizontal rank (1-8)

        [4] get promoted piece
        */

        // ASCI a = 97, h = 104
        // ASCI 1 = 49, 8 = 56

        // file from
        if (userInput.charAt(0) > 'h' || userInput.charAt(0) < 'a') {
            return null;
        }

        // rank from
        if (userInput.charAt(1) > '8' || userInput.charAt(1) < '1') {
            return null;
        }

        // to from
        if (userInput.charAt(2) > 'h' || userInput.charAt(2) < 'a') {
            return null;
        }

        // rank to
        if (userInput.charAt(3) > '8' || userInput.charAt(3) < '1') {
            return null;
        }

        var fromFile = userInput.charAt(0) - 'a';
        var fromRank = userInput.charAt(1) - '1';
        var toFile = userInput.charAt(2) - 'a';
        var toRank = userInput.charAt(3) - '1';

        var fromValue = Bitboard.getBitIndexByFileAndRank(
                Bitboard.File.values()[fromFile],
                Bitboard.Rank.values()[fromRank]
        ).ordinal();

        var toValue = Bitboard.getBitIndexByFileAndRank(
                Bitboard.File.values()[toFile],
                Bitboard.Rank.values()[toRank]
        ).ordinal();

        // generate moves list
        var moveGenerator = new MoveGenerator(this);
        moveGenerator.generatePseudoLegalMoves();

        // filter moves list
        List<Move> moves = null;
        if (userInput.length() == 4) {
            moves = moveGenerator.filterPseudoLegalMovesBy(fromValue, toValue);
        } else if (userInput.length() == 5) {
            var promotedPiece = userInput.charAt(4);
            switch (promotedPiece) {
                case 'q':
                    moves = moveGenerator.filterPseudoLegalMovesBy(fromValue, toValue, PieceType.QUEEN);
                    break;
                case 'n':
                    moves = moveGenerator.filterPseudoLegalMovesBy(fromValue, toValue, PieceType.KNIGHT);
                    break;
                case 'b':
                    moves = moveGenerator.filterPseudoLegalMovesBy(fromValue, toValue, PieceType.BISHOP);
                    break;
                case 'r':
                    moves = moveGenerator.filterPseudoLegalMovesBy(fromValue, toValue, PieceType.ROOK);
                    break;
                default:
                    System.out.println("Invalid char for promoted piece given.");
                    return null;
            }
        }

        if (moves != null && !moves.isEmpty()) {
            return moves.get(0);
        }

        return null;
    }

    //-------------------------------------------------
    // Perft
    //-------------------------------------------------

    public int[] captures;
    public int[] checks;
    public int[] castles;
    public int[] enPassants;
    public long nodes = 0;

    /**
     * A function to walk the move generation tree of strictly
     * legal moves to count all the leaf nodes of a certain depth.
     * @see <a href="https://www.chessprogramming.org/Perft_Results">Some results</a>
     *
     * @param depth The search depth.
     * @param quiet True for no text outputs
     */
    private void perftDriver(int depth, boolean quiet) {
        if (depth == 0) {
            nodes++;
            return;
        }

        var moveGenerator = new MoveGenerator(this);
        moveGenerator.generatePseudoLegalMoves();

        for (var move : moveGenerator.getPseudoLegalMoves()) {
            if (!makeMove(move)) {
                continue;
            }

            if (!quiet) {
                if (move.getMoveFlag() == Move.MoveFlag.CAPTURE) {
                    captures[depth - 1]++;
                }

                if (Attack.getAttackersToSquare(colorToMove, Bitboard.getLsb(getKing(colorToMove)), this) != 0) {
                    checks[depth - 1]++;
                }

                if (move.getMoveFlag() == Move.MoveFlag.EN_PASSANT) {
                    enPassants[depth - 1]++;
                    captures[depth - 1]++;
                }

                if (move.getMoveFlag() == Move.MoveFlag.CASTLING) {
                    castles[depth - 1]++;
                }
            }

            perftDriver(depth - 1, quiet);

            undoMove(move);
        }
    }

    /**
     * Perft test main method.
     *
     * @param depth The search depth.
     * @param quiet True for no text outputs.
     */
    public void perftTest(int depth, boolean quiet) {
        System.out.println();
        System.out.println();
        System.out.println("--------------------------------");
        System.out.println("           Perft test           ");
        System.out.println("--------------------------------");

        var moveGenerator = new MoveGenerator(this);
        moveGenerator.generatePseudoLegalMoves();

        captures = new int[depth];
        checks = new int[depth];
        castles = new int[depth];
        enPassants = new int[depth];

        var startTime = System.currentTimeMillis();

        for (var move : moveGenerator.getPseudoLegalMoves()) {
            if (!makeMove(move)) {
                continue;
            }

            if (!quiet) {
                if (move.getMoveFlag() == Move.MoveFlag.CAPTURE) {
                    captures[depth - 1]++;
                }

                if (Attack.getAttackersToSquare(colorToMove, Bitboard.getLsb(getKing(colorToMove)), this) != 0) {
                    checks[depth - 1]++;
                }

                if (move.getMoveFlag() == Move.MoveFlag.EN_PASSANT) {
                    enPassants[depth - 1]++;
                    captures[depth - 1]++;
                }

                if (move.getMoveFlag() == Move.MoveFlag.CASTLING) {
                    castles[depth - 1]++;
                }
            }

            var cumNodes = nodes;

            perftDriver(depth - 1, quiet);

            var oldNodes = nodes - cumNodes;

            undoMove(move);

            if (!quiet) {
                System.out.println(move + " nodes: " + oldNodes);
            }
        }

        var endTime = System.currentTimeMillis() - startTime;

        System.out.println("---------------------------------");
        System.out.println("Depth: " + depth);
        System.out.println("Nodes: " + nodes);
        if (!quiet) {
            System.out.println("Captures: " + captures[0]);
            System.out.println("En passants: " + enPassants[0]);
            System.out.println("Castles: " + castles[0]);
            System.out.println("Checks: " + checks[0]);
        }
        System.out.println("Total execution time: " + endTime + "ms");
        System.out.println("---------------------------------");
    }

    /**
     * Perft test main method.
     *
     * @param depth The search depth.
     */
    public void perftTest(int depth) {
        perftTest(depth, true);
    }

    //-------------------------------------------------
    // Print
    //-------------------------------------------------

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(" +---+---+---+---+---+---+---+---+\n");

        for (var rank = Bitboard.Rank.RANK_8.ordinal(); rank >= Bitboard.Rank.RANK_1.ordinal(); rank--) {
            s.append(rank + 1).append("|");

            for (var file : Bitboard.File.values()) {
                var piece = getPieceString(file, Bitboard.Rank.values()[rank]);
                if (piece.equals("")) {
                    s.append("   ");
                } else {
                    s.append(" ");
                    s.append(piece);
                }

                s.append("|");
            }

            if (rank == 7) {
                s.append("    Next move: ").append(colorToMove);
            }

            if (rank == 6) {
                s.append("    Castling rights: ").append(castlingRightsToString());
            }

            if (rank == 5) {
                s.append("    En passant: ").append(epIndex);
            }

            if (rank == 4) {
                s.append("    Zobrist key: ").append(zkey);
            }

            if (rank == 3) {
                s.append("    half moves: ").append(halfMovesCounter).append(", moves: ").append(movesCounter);
            }

            s.append("\n");
            s.append(" +---+---+---+---+---+---+---+---+\n");
        }

        s.append("   a   b   c   d   e   f   g   h");

        return s.toString();
    }

    /**
     * Returns a string with the castling rights.
     *
     * @return String with castling rights.
     */
    public String castlingRightsToString() {
        StringBuilder s = new StringBuilder();

        if (isKingSideCastlingAllowed(Color.WHITE)) {
            s.append("K");
        }
        if (isQueenSideCastlingAllowed(Color.WHITE)) {
            s.append("Q");
        }
        if (isKingSideCastlingAllowed(Color.BLACK)) {
            s.append("k");
        }
        if (isQueenSideCastlingAllowed(Color.BLACK)) {
            s.append("q");
        }

        return s.toString();
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    /**
     * The main init method. Splits the FEN and calls further methods to set the bitboards.
     *
     * @param fen A particular FEN board position.
     */
    private void initWithFen(String fen) {
        Objects.requireNonNull(fen, "fen must not be null");

        var fenFields = fen.split(" ");

        // a FEN without movecounters can be passed
        if (fenFields.length < 4) {
            throw new RuntimeException("Invalid FEN record given.");
        }

        // pieces
        var piecePlacement = fenFields[0].split("/");
        if (piecePlacement.length != 8) {
            throw new RuntimeException("Invalid piece placement.");
        }

        var currentRank = 7;
        for (var pieces : piecePlacement) {
            setBitboards(pieces, currentRank);
            currentRank--;
        }

        // color to move
        if (fenFields[1].equals("w")) {
            colorToMove = Color.WHITE;
        } else if (fenFields[1].equals("b")) {
            colorToMove = Color.BLACK;
        } else {
            throw new RuntimeException("Invalid color given.");
        }

        // castling rights
        setCastlingRights(fenFields[2]);

        // en passant
        if (!fenFields[3].equals("-")) {
            var file = (104 - fenFields[3].charAt(0)) + 1;
            var rank = Integer.parseInt(fenFields[3].substring(1)) - 1;
            epIndex = Bitboard.getBitIndexByFileAndRank(
                    Bitboard.File.values()[file],
                    Bitboard.Rank.values()[rank]
            );
        }

        // move counter
        halfMovesCounter = 0;
        oldHalfMovesCounter = 0;

        movesCounter = 1;

        if (fenFields.length == 6) {
            halfMovesCounter = Integer.parseInt(fenFields[4]);
            oldHalfMovesCounter = halfMovesCounter;

            movesCounter = Integer.parseInt(fenFields[5]);
        }

        // create Zobrist key
        Zkey.createKey(this);
        oldZkey = zkey;
    }

    /**
     * Adds all pieces of a rank to the {@link #bitboards}.
     *
     * @param pieces All pieces of a rank.
     * @param currentRank The number of the rank.
     */
    private void setBitboards(String pieces, int currentRank) {
        Objects.requireNonNull(pieces, "pieces must not be null");

        var x = -1;
        for (var ch : pieces.toCharArray()) {
            switch (ch) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    x += Character.digit(ch, 10);
                    break;

                case 'P' :
                    bitboards[Bitboard.WHITE_PAWNS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'N' :
                    bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'B' :
                    bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'R' :
                    bitboards[Bitboard.WHITE_ROOKS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'Q' :
                    bitboards[Bitboard.WHITE_QUEENS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'K' :
                    bitboards[Bitboard.WHITE_KING_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;

                case 'p' :
                    bitboards[Bitboard.BLACK_PAWNS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'n' :
                    bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'b' :
                    bitboards[Bitboard.BLACK_BISHOPS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'r' :
                    bitboards[Bitboard.BLACK_ROOKS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'q' :
                    bitboards[Bitboard.BLACK_QUEENS_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                case 'k' :
                    bitboards[Bitboard.BLACK_KING_BITBOARD] |= Bitboard.SQUARES[Bitboard.getBitIndexByFileAndRank(Bitboard.File.values()[++x], Bitboard.Rank.values()[currentRank]).ordinal()];
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ch);
            }
        }
    }

    /**
     * Set {@link #castlingRights} from a given string.
     *
     * @param castleString The castling rights.
     */
    private void setCastlingRights(String castleString) {
        castlingRights = 0;

        // The symbol "-" designates that neither side may castle.
        if (castleString.equals("-")) {
            return;
        }

        // Uppercase letters come first to indicate White's castling availability, followed by lowercase letters for Black's.
        // The letter "k" indicates that kingside castling is available, while "q" means that a player may castle queenside.
        for (var ch : castleString.toCharArray()) {
            switch (ch) {
                case 'K':
                    castlingRights |= Bitboard.WHITE_KING_CASTLE_KING_SIDE;
                    break;
                case 'Q':
                    castlingRights |= Bitboard.WHITE_KING_CASTLE_QUEEN_SIDE;
                    break;
                case 'k':
                    castlingRights |= Bitboard.BLACK_KING_CASTLE_KING_SIDE;
                    break;
                case 'q':
                    castlingRights |= Bitboard.BLACK_KING_CASTLE_QUEEN_SIDE;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ch);
            }
        }
    }

    //-------------------------------------------------
    // Update
    //-------------------------------------------------

    /**
     * Updates all bitboards that consist of several other bitboards.
     */
    private void updateCommonBitboards() {
        bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD] =
                bitboards[Bitboard.WHITE_PAWNS_BITBOARD] |
                bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] |
                bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] |
                bitboards[Bitboard.WHITE_ROOKS_BITBOARD] |
                bitboards[Bitboard.WHITE_QUEENS_BITBOARD] |
                bitboards[Bitboard.WHITE_KING_BITBOARD];

        bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD] =
                bitboards[Bitboard.BLACK_PAWNS_BITBOARD] |
                bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD] |
                bitboards[Bitboard.BLACK_BISHOPS_BITBOARD] |
                bitboards[Bitboard.BLACK_ROOKS_BITBOARD] |
                bitboards[Bitboard.BLACK_QUEENS_BITBOARD] |
                bitboards[Bitboard.BLACK_KING_BITBOARD];

        bitboards[Bitboard.ALL_PAWNS_BITBOARD] = bitboards[Bitboard.WHITE_PAWNS_BITBOARD] | bitboards[Bitboard.BLACK_PAWNS_BITBOARD];
        bitboards[Bitboard.ALL_KNIGHTS_BITBOARD] = bitboards[Bitboard.WHITE_KNIGHTS_BITBOARD] | bitboards[Bitboard.BLACK_KNIGHTS_BITBOARD];
        bitboards[Bitboard.ALL_BISHOPS_BITBOARD] = bitboards[Bitboard.WHITE_BISHOPS_BITBOARD] | bitboards[Bitboard.BLACK_BISHOPS_BITBOARD];
        bitboards[Bitboard.ALL_ROOKS_BITBOARD] = bitboards[Bitboard.WHITE_ROOKS_BITBOARD] | bitboards[Bitboard.BLACK_ROOKS_BITBOARD];
        bitboards[Bitboard.ALL_QUEENS_BITBOARD] = bitboards[Bitboard.WHITE_QUEENS_BITBOARD] | bitboards[Bitboard.BLACK_QUEENS_BITBOARD];
        bitboards[Bitboard.ALL_KINGS_BITBOARD] = bitboards[Bitboard.WHITE_KING_BITBOARD] | bitboards[Bitboard.BLACK_KING_BITBOARD];

        allPiecesBitboard = bitboards[Bitboard.ALL_WHITE_PIECES_BITBOARD] | bitboards[Bitboard.ALL_BLACK_PIECES_BITBOARD];
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    /**
     * Returns a String for the specified {@link Piece}.
     *
     * @param piece A {@link Piece}.
     * @param attacked True, if the {@link Piece} is attacked.
     *
     * @return The String for the {@link Piece}.
     */
    private String createPieceString(Piece piece, boolean attacked) {
        var pieceColor = piece.color == Color.WHITE ? Config.WHITE_PIECE_COLOR : Config.BLACK_PIECE_COLOR;

        if (Config.UNICODE_SYMBOLS) {
            if (Config.COLORED) {
                if (attacked) {
                    return Config.SQUARE_ATTACKED_BG_COLOR + pieceColor + piece.symbol + ANSI_RESET;
                } else {
                    return pieceColor + piece.symbol + ANSI_RESET;
                }
            } else {
                return piece.symbol;
            }
        } else {
            if (Config.COLORED) {
                if (attacked) {
                    return Config.SQUARE_ATTACKED_BG_COLOR + pieceColor + piece.letter + ANSI_RESET;
                } else {
                    return pieceColor + piece.letter + ANSI_RESET;
                }
            } else {
                return piece.letter;
            }
        }
    }

    /**
     * Returns a String for the specified file/rank.
     *
     * @param file A vertical line on the chessboard.
     * @param rank A horizontal line on the chessboard.
     *
     * @return The String for the square.
     */
    private String getPieceString(Bitboard.File file, Bitboard.Rank rank) {
        var attacked = false;

        if (Bitboard.isBitSet(getWhitePawns(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_PAWN, attacked);
        }

        if (Bitboard.isBitSet(getWhiteKnights(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_KNIGHT, attacked);
        }

        if (Bitboard.isBitSet(getWhiteBishops(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_BISHOP, attacked);
        }

        if (Bitboard.isBitSet(getWhiteRooks(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_ROOK, attacked);
        }

        if (Bitboard.isBitSet(getWhiteQueens(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_QUEEN, attacked);
        }

        if (Bitboard.isBitSet(getWhiteKing(), file, rank)) {
            attacked = Attack.isWhiteSquareAttacked(file, rank, this);
            return createPieceString(Piece.WHITE_KING, attacked);
        }

        if (Bitboard.isBitSet(getBlackPawns(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_PAWN, attacked);
        }

        if (Bitboard.isBitSet(getBlackKnights(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_KNIGHT, attacked);
        }

        if (Bitboard.isBitSet(getBlackBishops(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_BISHOP, attacked);
        }

        if (Bitboard.isBitSet(getBlackRooks(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_ROOK, attacked);
        }

        if (Bitboard.isBitSet(getBlackQueens(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_QUEEN, attacked);
        }

        if (Bitboard.isBitSet(getBlackKing(), file, rank)) {
            attacked = Attack.isBlackSquareAttacked(file, rank, this);
            return createPieceString(Piece.BLACK_KING, attacked);
        }

        return "";
    }
}
