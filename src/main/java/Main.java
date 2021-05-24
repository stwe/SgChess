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

        var board = new Board();
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();

        var nodes = 0;
        var allNodes = 0;
        for (var move : mg.getPseudoLegalMoves()) {
            board.makeMove(move);

            nodes = board.perft(1);
            allNodes += nodes;

            board.undoMove(move);

            System.out.println(move + " " + nodes);
        }

        System.out.println("All nodes: " + allNodes);
    }
}
