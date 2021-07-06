/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.util.Objects;

public class Evaluation {

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
     * The piece-square table score of white minus the piece-square table score of black.
     * @see <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">Piece-square tables</a>
     * For each sort of pieces, different values are calculated depending on the squares the pieces are located.
     * The higher the score the better it is for White. The lower the score the better it is for black.
     */
    private int pstScore;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Evaluation} object.
     *
     * @param board The parent {@link Board} object.
     */
    public Evaluation(Board board) {
        this.board = Objects.requireNonNull(board, "board must not be null");

        init();
    }

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    public int getMaterialScore() {
        return materialScore;
    }

    public int getPstScore() {
        return pstScore;
    }

    //-------------------------------------------------
    // Evaluate
    //-------------------------------------------------

    public void evaluateMove(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();

        // the update() method is called after makeMove(), which has already changed the color
        // so we need to restore the previous color as the right color
        var previousColor = board.getColorToMove().getEnemyColor();
        var previousColorValue = previousColor.value;

        // set pst
        var tableFrom = piece.pieceType.evaluationTables[previousColorValue][from]; //  -20
        var tableTo = piece.pieceType.evaluationTables[previousColorValue][to];     //   20
        pstScore += /*previousColor.sign */ tableTo - tableFrom;                         // = 40

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();

            // todo: temp code
            if (capturedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            materialScore += previousColor.sign * capturedPieceType.materialScore;
            pstScore += previousColor.sign * capturedPieceType.evaluationTables[previousColorValue][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();

            // todo: temp code
            if (promotedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            pstScore += previousColor.sign * promotedPieceType.evaluationTables[previousColorValue][to];
        }
    }

    public void undoMove(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();

        // the undo() method is called after undoMove(), which has already restored the color
        var color = board.getColorToMove();
        var colorValue = color.value;

        // undo pst
        var tableFrom = piece.pieceType.evaluationTables[colorValue][from];
        var tableTo = piece.pieceType.evaluationTables[colorValue][to];
        pstScore -= /*color.sign*/ tableTo - tableFrom;

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();

            // todo: temp code
            if (capturedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            materialScore -= color.sign * capturedPieceType.materialScore;
            pstScore -= color.sign * capturedPieceType.evaluationTables[colorValue][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();

            // todo: temp code
            if (promotedPieceType == PieceType.NO_PIECE) {
                throw new RuntimeException("unexpected error.");
            }

            pstScore -= color.sign * promotedPieceType.evaluationTables[colorValue][to];
        }
    }

    public int evaluate() {
        return materialScore + pstScore;
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    private void init() {
        materialScore = calcWhitePiecesMaterialScore() - calcBlackPiecesMaterialScore();

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

        pstScore = whitePiecesSquareTableScore - blackPiecesSquareTableScore;
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
