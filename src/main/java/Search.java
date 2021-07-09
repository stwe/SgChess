/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.util.Objects;

/**
 * Chess AI
 */
public class Search {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * The parent {@link Board} object.
     */
    private final Board board;

    /**
     * An {@link Evaluation} object.
     */
    private final Evaluation evaluation;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Search} object.
     */
    public Search(Board board) {
        this.board = Objects.requireNonNull(board, "board must not be null");;
        evaluation = new Evaluation(board);
    }

    //-------------------------------------------------
    // Minimax
    //-------------------------------------------------

    public Move minimaxRoot(int depth) {
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        var bestScore = -99999;
        Move bestMove = null;
        var legalMovesMaked = 0;

        var startTime = System.currentTimeMillis();

        for (var move : moves) {
            if (!board.makeMove(move)) {
                continue;
            }

            legalMovesMaked++;

            evaluation.evaluateMove(move);

            var score = minimax(depth - 1);
            if (score >= bestScore) {
                bestScore = score;
                bestMove = move;
            }

            board.undoMove(move);
            evaluation.undoMove(move);
        }

        var endTime = System.currentTimeMillis() - startTime;

        if (legalMovesMaked == 0) {
            noLegalMovesFound();
        }

        if (bestMove != null) {
            System.out.println(bestMove);
            System.out.println("Best score: " + bestScore);
            System.out.println("Total execution time: " + endTime + "ms");
        }

        return bestMove;
    }

    private int minimax(int depth) {
        if (depth == 0) {
            return evaluation.evaluate();
        }

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        var legalMovesMaked = 0;

        if (board.getColorToMove() == Board.Color.WHITE) {
            var bestMove = -99999;
            for (var move : moves) {
                if (!board.makeMove(move)) {
                    continue;
                }

                legalMovesMaked++;

                evaluation.evaluateMove(move);

                bestMove = Math.max(bestMove, minimax(depth - 1));

                board.undoMove(move);
                evaluation.undoMove(move);
            }

            if (legalMovesMaked == 0) {
                noLegalMovesFound();
            }

            return bestMove;
        } else {
            var bestMove = 99999;
            for (var move : moves) {
                if (!board.makeMove(move)) {
                    continue;
                }

                legalMovesMaked++;

                evaluation.evaluateMove(move);

                bestMove = Math.min(bestMove, minimax(depth - 1));

                board.undoMove(move);
                evaluation.undoMove(move);
            }

            if (legalMovesMaked == 0) {
                noLegalMovesFound();
            }

            return bestMove;
        }
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    private void noLegalMovesFound() {
        // king is in check
        if (Attack.isCheck(board.getColorToMove(), board)) {
            System.out.println(board.getColorToMove() + " is checkmate.");
        } else {
            // king is not in check
            System.out.println("stalemate");
        }

        System.out.println(board);
    }
}
