/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.IOException;

public class Main {

    private static final boolean USE_UCI = false;

    //-------------------------------------------------
    // SgChess
    //-------------------------------------------------

    public static void main(String[] args) throws IOException, IllegalAccessException {

        var board = new Board();
        var eval = new Evaluation(board);
        System.out.println(eval.evaluate());

        var m = board.parseMove("e2e4");
        board.makeMove(m);
        eval.update(m);
        System.out.println(eval.evaluate());

        eval.undo(m);
        System.out.println(eval.evaluate());

        var i = 0;

        /*
        if (USE_UCI) {
            UCIClient.run();
        } else {
            Client.run();
        }
        */
    }
}
