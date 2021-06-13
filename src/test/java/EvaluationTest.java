/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {

    @Test
    void evaluate() {
        var board = new Board();
        var eval = new Evaluation(board);
        System.out.println(eval.evaluate());

        var m = board.parseMove("e2e4");
        board.makeMove(m);
        eval.update(m);
        System.out.println(eval.evaluate());

        eval.undo(m);
        System.out.println(eval.evaluate());
    }
}
