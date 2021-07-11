/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

public class SearchResult {

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    public static final int MATE_SCORE = 29000;

    public static final int STALEMATE_SCORE = 0;

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    public Move bestMove = null;
    public int bestScore = 0;
    public int nodes = 0;
    public long time = 0;

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    public SearchResult() {
    }

    public SearchResult(Move bestMove, int bestScore, int nodes, long time) {
        this.bestMove = bestMove;
        this.bestScore = bestScore;
        this.nodes = nodes;
        this.time = time;
    }
}
