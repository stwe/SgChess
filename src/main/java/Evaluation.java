/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

public class Evaluation {

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    /**
     * The material value of all the white and black pieces on the board.
     */
    public static final int START_POSITION_TOTAL_MATERIAL_SCORE =
            2 * PieceType.QUEEN.materialScore +
            4 * PieceType.ROOK.materialScore +
            4 * PieceType.BISHOP.materialScore +
            4 * PieceType.KNIGHT.materialScore +
            16 * PieceType.PAWN.materialScore;

    private static final int BISHOP_PAIR = 40;
    private static final int KNIGHT_VALUE = 32;
    private static final int BISHOP_VALUE = 16;

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The parent {@link Board} object.
     */
    private final Board board;

    /**
     * The material value of all white pieces minus the material value of all black pieces.
     * The higher the score the better it is for White. The lower the score the better it is for black.
     */
    private int materialScore;

    /**
     * The summed material value of all the current white and black pieces on the board.
     */
    private int currentTotalMaterialScore;

    /**
     * The piece-square table score of white minus the piece-square table score of black.
     * @see <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">Piece-square tables</a>
     * For each sort of pieces, different values are calculated depending on the squares the pieces are located.
     * The higher the score the better it is for White. The lower the score the better it is for black.
     */
    private int pieceSquareTableScore;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Evaluation} object.
     *
     * @param board The parent {@link Board} object.
     */
    public Evaluation(Board board) {
        this.board = board;

        init();
    }

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    public int getMaterialScore() {
        return materialScore;
    }

    public int getPieceSquareTableScore() {
        return pieceSquareTableScore;
    }

    public int getCurrentTotalMaterialScore() {
        return currentTotalMaterialScore;
    }

    //-------------------------------------------------
    // Update / undo
    //-------------------------------------------------

    public void update(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();

        // the update() method is called after makeMove(), which has already changed the color
        // so we need to restore the previous color as the right color
        var previousColor = board.getColorToMove().getEnemyColor();
        var previousColorValue = previousColor.value;

        // example: white pawn e2e4
        var tableFrom = piece.pieceType.evaluationTables[previousColorValue][from]; //  -40
        var tableTo = piece.pieceType.evaluationTables[previousColorValue][to];     //   20
        pieceSquareTableScore += previousColor.sign * tableTo - tableFrom;               // = 60

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();

            // todo: temp code
            if (capturedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            currentTotalMaterialScore -= capturedPieceType.materialScore;
            materialScore += previousColor.sign * capturedPieceType.materialScore;
            pieceSquareTableScore += previousColor.sign * capturedPieceType.evaluationTables[previousColorValue][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();

            // todo: temp code
            if (promotedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            currentTotalMaterialScore += previousColor.sign * promotedPieceType.materialScore - PieceType.PAWN.materialScore;
            pieceSquareTableScore += previousColor.sign * promotedPieceType.evaluationTables[previousColorValue][to];
        }
    }

    public void undo(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();

        // the undo() method is called after undoMove(), which has already changed the color
        // so we need to restore the previous color as the right color
        // todo: test
        var previousColor = board.getColorToMove().getEnemyColor();
        var previousColorValue = previousColor.value;

        var tableFrom = piece.pieceType.evaluationTables[previousColorValue][from];
        var tableTo = piece.pieceType.evaluationTables[previousColorValue][to];
        pieceSquareTableScore -= previousColor.sign * tableTo - tableFrom;

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();

            // todo: temp code
            if (capturedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            currentTotalMaterialScore += capturedPieceType.materialScore;
            materialScore -= previousColor.sign * capturedPieceType.materialScore;
            pieceSquareTableScore -= previousColor.sign * capturedPieceType.evaluationTables[previousColorValue][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();

            // todo: temp code
            if (promotedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            currentTotalMaterialScore -= previousColor.sign * promotedPieceType.materialScore - PieceType.PAWN.materialScore;
            pieceSquareTableScore -= previousColor.sign * promotedPieceType.evaluationTables[previousColorValue][to];
        }
    }

    //-------------------------------------------------
    // Evaluate
    //-------------------------------------------------

    public int evaluate() {
        // position score
        var openingValue = pieceSquareTableScore * currentTotalMaterialScore;
        var positionScore = openingValue / START_POSITION_TOTAL_MATERIAL_SCORE;

        // material adjustment score
        var whitePawns = Long.bitCount(board.getWhitePawns());
        var blackPawns = Long.bitCount(board.getBlackPawns());

        var whiteKnights = Long.bitCount(board.getWhiteKnights());
        var blackKnights = Long.bitCount(board.getBlackKnights());

        var whiteBishops = Long.bitCount(board.getWhiteBishops());
        var blackBishops = Long.bitCount(board.getBlackBishops());

        int knightBonus = KNIGHT_VALUE * (whiteKnights * whitePawns - blackKnights * blackPawns) / 8;
        int bishopBonus = BISHOP_VALUE * (whiteBishops * whitePawns - blackBishops * blackPawns) / 8;

        int pairCount = ((whiteBishops > 1) ? 1 : 0) - ((blackBishops > 1) ? 1 : 0);
        int pairBonus = pairCount * (BISHOP_PAIR * currentTotalMaterialScore) / START_POSITION_TOTAL_MATERIAL_SCORE;

        var materialAdjustmentsScore = knightBonus + bishopBonus + pairBonus;

        // result
        return materialAdjustmentsScore + materialScore + (int)(1.16 * positionScore);
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    private void init() {
        materialScore = calcWhitePiecesMaterialScore() - calcBlackPiecesMaterialScore();
        currentTotalMaterialScore = calcWhitePiecesMaterialScore() + calcBlackPiecesMaterialScore();

        int whitePiecesSquareTableScore = 0;
        whitePiecesSquareTableScore += calcPieceSquareTableScore(board.getWhitePawns(), Piece.WHITE_PAWN);
        whitePiecesSquareTableScore += calcPieceSquareTableScore(board.getWhiteKnights(), Piece.WHITE_KNIGHT);
        whitePiecesSquareTableScore += calcPieceSquareTableScore(board.getWhiteBishops(), Piece.WHITE_BISHOP);
        whitePiecesSquareTableScore += calcPieceSquareTableScore(board.getWhiteRooks(), Piece.WHITE_ROOK);
        whitePiecesSquareTableScore += calcPieceSquareTableScore(board.getWhiteQueens(), Piece.WHITE_QUEEN);

        int blackPiecesSquareTableScore = 0;
        blackPiecesSquareTableScore += calcPieceSquareTableScore(board.getBlackPawns(), Piece.BLACK_PAWN);
        blackPiecesSquareTableScore += calcPieceSquareTableScore(board.getBlackKnights(), Piece.BLACK_KNIGHT);
        blackPiecesSquareTableScore += calcPieceSquareTableScore(board.getBlackBishops(), Piece.BLACK_BISHOP);
        blackPiecesSquareTableScore += calcPieceSquareTableScore(board.getBlackRooks(), Piece.BLACK_ROOK);
        blackPiecesSquareTableScore += calcPieceSquareTableScore(board.getBlackQueens(), Piece.BLACK_QUEEN);

        pieceSquareTableScore = whitePiecesSquareTableScore - blackPiecesSquareTableScore;
    }

    //-------------------------------------------------
    // Calc score
    //-------------------------------------------------

    private int calcWhitePiecesMaterialScore() {
        var result = 0;

        result += Long.bitCount(board.getWhitePawns()) * PieceType.PAWN.materialScore;
        result += Long.bitCount(board.getWhiteKnights()) * PieceType.KNIGHT.materialScore;
        result += Long.bitCount(board.getWhiteBishops()) * PieceType.BISHOP.materialScore;
        result += Long.bitCount(board.getWhiteRooks()) * PieceType.ROOK.materialScore;
        result += Long.bitCount(board.getWhiteQueens()) * PieceType.QUEEN.materialScore;

        return result;
    }

    private int calcBlackPiecesMaterialScore() {
        var result = 0;

        result += Long.bitCount(board.getBlackPawns()) * PieceType.PAWN.materialScore;
        result += Long.bitCount(board.getBlackKnights()) * PieceType.KNIGHT.materialScore;
        result += Long.bitCount(board.getBlackBishops()) * PieceType.BISHOP.materialScore;
        result += Long.bitCount(board.getBlackRooks()) * PieceType.ROOK.materialScore;
        result += Long.bitCount(board.getBlackQueens()) * PieceType.QUEEN.materialScore;

        return result;
    }

    private int calcPieceSquareTableScore(long bitboard, Piece piece) {
        var result = 0;
        while (bitboard != 0) {
            result += piece.evaluationTable[Bitboard.getLsb(bitboard).ordinal()];
            bitboard &= bitboard - 1;
        }

        return result;
    }
}
