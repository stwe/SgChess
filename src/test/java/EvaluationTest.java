/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {

    @Test
    void whiteOpeningMove() {
        var board = new Board();
        var eval = new Evaluation(board);
        assertEquals(Board.Color.WHITE, board.getColorToMove());

        var m = board.parseMove("e2e4");
        assertTrue(board.makeMove(m));
        eval.update(m);
        assertEquals(60, eval.getPieceSquareTableScore());
        assertEquals(Board.Color.BLACK, board.getColorToMove());

        board.undoMove(m);
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        eval.undo(m);
        assertEquals(0, eval.getPieceSquareTableScore());
        assertEquals(Board.Color.WHITE, board.getColorToMove());
    }

    @Test
    void whitePromotionMove() {
        var board = new Board("k7/4P3/1p6/8/8/8/8/K7 w - - 0 1");
        var eval = new Evaluation(board);
        assertEquals(25, eval.getPieceSquareTableScore());
        assertEquals(Board.Color.WHITE, board.getColorToMove());

        var m = board.parseMove("e7e8q");
        assertTrue(board.makeMove(m));
        eval.update(m);
        assertEquals(Board.Color.BLACK, board.getColorToMove());

        board.undoMove(m);
        assertEquals(Board.Color.WHITE, board.getColorToMove());
        eval.undo(m);

        assertEquals(25, eval.getPieceSquareTableScore());
        assertEquals(Board.Color.WHITE, board.getColorToMove());
    }
}
