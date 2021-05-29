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

        var board = new Board("k7/8/8/8/7N/5p1B/6p1/7K w - - 0 1");
        System.out.println(board);

        var depth = 4;
        var nodes = board.perft(depth);
        System.out.println("Depth: " + depth + " all Nodes: " + nodes);
    }
}
