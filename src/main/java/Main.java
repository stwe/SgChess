/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, IllegalAccessException {
        /*
        int i = 0;

        Scanner keyboard = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("Enter command (q to exit):");
            String input = keyboard.nextLine();

            if(input != null) {
                System.out.println("Your input is : " + input);
                if ("q".equals(input)) {
                    System.out.println("Exit programm");
                    exit = true;
                } else if ("n".equals(input)) {
                    System.out.println("current i: " + i);
                    Bitboard.printBitboard(Moves.KING_MOVES[i++]);
                }
            }
        }

        keyboard.close();
        */

        ConfigLoader.load(Config.class, "/config.properties");

        /*
        stockfish
        position fen ...
        go perft x
        */

        var board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");
        System.out.println(board);
        board.perftTest(2);

        /*
        Piece: P from: e2 to: e3 flag: NORMAL (score: 0) nodes:              15   15
        Piece: P from: e2 to: e4 flag: PAWN_START (score: 0) nodes:          15   16  -1
        Piece: P from: g2 to: g3 flag: NORMAL (score: 0) nodes:               4    4
        Piece: P from: g2 to: g4 flag: PAWN_START (score: 0) nodes:          15   17  -2
        Piece: K from: a5 to: a4 flag: NORMAL (score: 0) nodes:              15   15
        Piece: K from: a5 to: a6 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: b1 flag: NORMAL (score: 0) nodes:              16   16
        Piece: R from: b4 to: b2 flag: NORMAL (score: 0) nodes:              16   16
        Piece: R from: b4 to: b3 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: a4 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: c4 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: d4 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: e4 flag: NORMAL (score: 0) nodes:              15   15
        Piece: R from: b4 to: f4 flag: CAPTURE captured piece type: PAWN      2    2
                                                                                       188 statt 191
        */
    }
}
