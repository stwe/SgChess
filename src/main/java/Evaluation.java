/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

public class Evaluation {

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    public static final int TOTAL_MATERIAL =
            2 * PieceType.QUEEN.materialScore +
            4 * PieceType.ROOK.materialScore +
            4 * PieceType.BISHOP.materialScore +
            4 * PieceType.KNIGHT.materialScore +
            16 * PieceType.PAWN.materialScore;

    private final int[] BISHOP_PAIR = {
        40, 60
    };

    private static final int KNIGHT_VALUE = 32;
    private static final int BISHOP_VALUE = 16;

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The parent {@link Board} object.
     */
    private final Board board;

    private int materialScore;

    private int positionScore;

    private int gameStage;

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

    public int getPositionScore() {
        return positionScore;
    }

    public int getGameStage() {
        return gameStage;
    }

    //-------------------------------------------------
    // Update / undo
    //-------------------------------------------------

    public void update(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();
        var nextColor = board.getColorToMove().getEnemyColor().value;

        positionScore += (-2 * nextColor + 1) *
                piece.pieceType.evaluationTables[nextColor][to] -
                piece.pieceType.evaluationTables[nextColor][from];

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();
            gameStage -= capturedPieceType.materialScore;
            materialScore += (-2 * nextColor + 1) * capturedPieceType.materialScore;
            positionScore += (-2 * nextColor + 1) * capturedPieceType.evaluationTables[nextColor][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();
            gameStage += (-2 * nextColor + 1) * promotedPieceType.materialScore - PieceType.PAWN.materialScore;
            positionScore += (-2 * nextColor + 1) * promotedPieceType.evaluationTables[nextColor][to];
        }
    }

    public void undo(Move move) {
        var piece = move.getPiece();
        var from = move.getFrom();
        var to = move.getTo();
        var nextColor = board.getColorToMove().getEnemyColor().value;

        positionScore -= (-2 * nextColor + 1) *
                piece.pieceType.evaluationTables[nextColor][to] -
                piece.pieceType.evaluationTables[nextColor][from];

        if (move.getMoveFlag() == Move.MoveFlag.CAPTURE || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var capturedPieceType = move.getCapturedPieceType();
            gameStage += capturedPieceType.materialScore;
            materialScore -= (-2 * nextColor + 1) * capturedPieceType.materialScore;
            positionScore -= (-2 * nextColor + 1) * capturedPieceType.evaluationTables[nextColor][to];
        }

        if (move.getMoveFlag() == Move.MoveFlag.PROMOTION || move.getMoveFlag() == Move.MoveFlag.PROMOTION_CAPTURE) {
            var promotedPieceType = move.getPromotedPieceType();
            gameStage -= (-2 * nextColor + 1) * promotedPieceType.materialScore - PieceType.PAWN.materialScore;
            positionScore -= (-2 * nextColor + 1) * promotedPieceType.evaluationTables[nextColor][to];
        }
    }

    //-------------------------------------------------
    // Evaluate
    //-------------------------------------------------

    public int evaluate() {
        // position
        //var alpha = TOTAL_MATERIAL - gameStage;
        var openingValue = positionScore * gameStage;
        var positionResult = openingValue / TOTAL_MATERIAL;

        // material adjustments
        var whitePawns = Long.bitCount(board.getWhitePawns());
        var blackPawns = Long.bitCount(board.getBlackPawns());

        var whiteKnights = Long.bitCount(board.getWhiteKnights());
        var blackKnights = Long.bitCount(board.getBlackKnights());

        var whiteBishops = Long.bitCount(board.getWhiteBishops());
        var blackBishops = Long.bitCount(board.getBlackBishops());

        int knightBonus = KNIGHT_VALUE * (whiteKnights * whitePawns - blackKnights * blackPawns) / 8;
        int bishopBonus = BISHOP_VALUE * (whiteBishops * whitePawns - blackBishops * blackPawns) / 8;

        int pairCount = ((whiteBishops > 1) ? 1 : 0) - ((blackBishops > 1) ? 1 : 0);

        int pairBonus = pairCount * (BISHOP_PAIR[0] * gameStage) / TOTAL_MATERIAL;

        var materialAdjustmentsResult = knightBonus + bishopBonus + pairBonus;

        // result
        return materialAdjustmentsResult + materialScore + (int)(1.16 * positionResult);
    }

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    private void init() {
        materialScore = getWhitePiecesMaterialScore() - getBlackPiecesMaterialScore();
        gameStage = getWhitePiecesMaterialScore() + getBlackPiecesMaterialScore();

        int whitePositionScore = 0;
        whitePositionScore += calcPositionScore(board.getWhitePawns(), Piece.WHITE_PAWN);
        whitePositionScore += calcPositionScore(board.getWhiteKnights(), Piece.WHITE_KNIGHT);
        whitePositionScore += calcPositionScore(board.getWhiteBishops(), Piece.WHITE_BISHOP);
        whitePositionScore += calcPositionScore(board.getWhiteRooks(), Piece.WHITE_ROOK);
        whitePositionScore += calcPositionScore(board.getWhiteQueens(), Piece.WHITE_QUEEN);

        int blackPositionScore = 0;
        blackPositionScore += calcPositionScore(board.getBlackPawns(), Piece.BLACK_PAWN);
        blackPositionScore += calcPositionScore(board.getBlackKnights(), Piece.BLACK_KNIGHT);
        blackPositionScore += calcPositionScore(board.getBlackBishops(), Piece.BLACK_BISHOP);
        blackPositionScore += calcPositionScore(board.getBlackRooks(), Piece.BLACK_ROOK);
        blackPositionScore += calcPositionScore(board.getBlackQueens(), Piece.BLACK_QUEEN);

        positionScore = whitePositionScore - blackPositionScore;
    }

    //-------------------------------------------------
    // Material score
    //-------------------------------------------------

    private int getWhitePiecesMaterialScore() {
        int result = 0;

        result += Long.bitCount(board.getWhitePawns()) * PieceType.PAWN.materialScore;
        result += Long.bitCount(board.getWhiteKnights()) * PieceType.KNIGHT.materialScore;
        result += Long.bitCount(board.getWhiteBishops()) * PieceType.BISHOP.materialScore;
        result += Long.bitCount(board.getWhiteRooks()) * PieceType.ROOK.materialScore;
        result += Long.bitCount(board.getWhiteQueens()) * PieceType.QUEEN.materialScore;

        return result;
    }

    private int getBlackPiecesMaterialScore() {
        int result = 0;

        result += Long.bitCount(board.getBlackPawns()) * PieceType.PAWN.materialScore;
        result += Long.bitCount(board.getBlackKnights()) * PieceType.KNIGHT.materialScore;
        result += Long.bitCount(board.getBlackBishops()) * PieceType.BISHOP.materialScore;
        result += Long.bitCount(board.getBlackRooks()) * PieceType.ROOK.materialScore;
        result += Long.bitCount(board.getBlackQueens()) * PieceType.QUEEN.materialScore;

        return result;
    }

    //-------------------------------------------------
    // Position score
    //-------------------------------------------------

    private int calcPositionScore(long bitboard, Piece piece) {
        int result = 0;
        while (bitboard != 0) {
            result += piece.evaluationTable[Bitboard.getLsb(bitboard).ordinal()];
            bitboard &= bitboard - 1;
        }

        return result;
    }
}
