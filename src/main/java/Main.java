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
        if (USE_UCI) {
            UCIClient.run();
        } else {
            Client.run();
        }
    }
}
