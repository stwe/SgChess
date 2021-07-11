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
        this.board = Objects.requireNonNull(board, "board must not be null");
        evaluation = new Evaluation(board);
    }

    //-------------------------------------------------
    // Minimax
    //-------------------------------------------------

    /*
    Weiss zieht

    Depth 0: ungültig

    Depth 1: macht keinen Sinn; bestScore ist das beste Evaluierungsergebnis der eigenen Züge
             evaluation.evaluate() für jeden Zug

    Depth 2: alle schwarzen Gegenzüge werden bewertet
    */

    public SearchResult minimaxRoot(int depth) {
        if (depth <= 0) {
            System.out.println("Depth must be greater than 0.");
            return null;
        }

        // generate pseudo legal moves
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        // init
        var bestScore = -99999;
        Move bestMove = null;
        var legalMovesMaked = 0;

        // start timer
        var startTime = System.currentTimeMillis();

        // loop over pseudo legal moves list
        for (var move : moves) {
            // execute move
            if (!board.makeMove(move)) {
                // continue if move not legal
                continue;
            }

            // inc legal moves counter
            legalMovesMaked++;

            // evaluate move
            evaluation.evaluateMove(move);

            // determine score
            var score = minimax(depth - 1);
            if (score >= bestScore) {
                bestScore = score;
                bestMove = move;
            }

            // undo move
            board.undoMove(move);

            // undo evaluation
            evaluation.undoMove(move);
        }

        // stop timer
        var endTime = System.currentTimeMillis() - startTime;

        // no legal moves were found; return MATE or STALEMATE
        if (legalMovesMaked == 0) {
            if (Attack.isCheck(board.getColorToMove(), board)) {
                return new SearchResult(bestMove, SearchResult.MATE_SCORE, 0, endTime);
            } else {
                return new SearchResult(bestMove, SearchResult.STALEMATE_SCORE, 0, endTime);
            }
        }

        // return results
        return new SearchResult(bestMove, bestScore, 0, endTime);
    }

    private int minimax(int depth) {
        // return evaluation if depth 0
        if (depth == 0) {
            return evaluation.evaluate();
        }

        // generate pseudo legal moves (color to move was already changed)
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        var moves = mg.getPseudoLegalMoves();

        // init
        var legalMovesMaked = 0;

        // white or black is on the move
        if (board.getColorToMove() == Board.Color.WHITE) {
            int score = -99999;

            // loop over pseudo legal moves list
            for (var move : moves) {
                // execute move
                if (!board.makeMove(move)) {
                    // continue if move not legal
                    continue;
                }

                // inc legal moves counter
                legalMovesMaked++;

                // evaluate move
                evaluation.evaluateMove(move);

                score = Math.max(score, minimax(depth - 1));

                // undo move
                board.undoMove(move);

                // undo evaluation
                evaluation.undoMove(move);
            }

            // no legal moves were found; return MATE or STALEMATE score
            if (legalMovesMaked == 0) {
                if (Attack.isCheck(Board.Color.WHITE, board)) {
                    return SearchResult.MATE_SCORE;
                } else {
                    return SearchResult.STALEMATE_SCORE;
                }
            }

            return score;
        } else {
            int score = 99999;

            // loop over pseudo legal moves list
            for (var move : moves) {
                // execute move
                if (!board.makeMove(move)) {
                    // continue if move not legal
                    continue;
                }

                // inc legal moves counter
                legalMovesMaked++;

                // evaluate move
                evaluation.evaluateMove(move);

                score = Math.min(score, minimax(depth - 1));

                // undo move
                board.undoMove(move);

                // undo evaluation
                evaluation.undoMove(move);
            }

            // no legal moves were found; return MATE or STALEMATE score
            if (legalMovesMaked == 0) {
                if (Attack.isCheck(Board.Color.BLACK, board)) {
                    return SearchResult.MATE_SCORE;
                } else {
                    return SearchResult.STALEMATE_SCORE;
                }
            }

            return score;
        }
    }
}
