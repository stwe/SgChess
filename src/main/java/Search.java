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
    private final Evaluation eval;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Search} object.
     */
    public Search(Board board) {
        Objects.requireNonNull(board, "board must not be null");

        this.board = board;
        eval = new Evaluation(board);
    }

    //-------------------------------------------------
    // Minimax
    //-------------------------------------------------

    public Move minimaxRoot(int depth) {
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        var bestMoveScore = -9999;
        Move bestMove = null;

        for (var move : moves) {
            board.makeMove(move);
            eval.update(move);

            var score = minimax(depth - 1);
            move.setScore(score);
            System.out.println(move);

            board.undoMove(move);
            eval.undo(move);

            if (score >= bestMoveScore) {
                bestMoveScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(int depth) {
        if (depth == 0) {
            return eval.evaluate();
        }

        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        if (board.getColorToMove() == Board.Color.WHITE) {
            var bestMove = -9999;
            for (var move : moves) {
                board.makeMove(move);
                eval.update(move);

                bestMove = Math.max(bestMove, minimax(depth - 1));

                board.undoMove(move);
                eval.undo(move);
            }

            return bestMove;
        } else {
            var bestMove = 9999;
            for (var move : moves) {
                board.makeMove(move);
                eval.update(move);

                bestMove = Math.min(bestMove, minimax(depth - 1));

                board.undoMove(move);
                eval.undo(move);
            }

            return bestMove;
        }
    }
}
