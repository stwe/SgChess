/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.IOException;
import java.util.Scanner;

public class UCIClient {

    private final static String AUTHOR = "stwe";
    public static Board board;

    //-------------------------------------------------
    // Run
    //-------------------------------------------------

    public static void run() throws IOException, IllegalAccessException {

        // load config
        ConfigLoader.load(Config.class, "/config.properties");

        // title
        System.out.println(Config.TITLE);

        // read commands
        var keyboard = new Scanner(System.in);

        // game loop
        var exit = false;
        while (!exit) {
            var input = keyboard.nextLine();

            if (input.startsWith("position")) {
                parsePosition(input);
            }

            switch (input) {
                case "uci":
                    System.out.println("id name " + Config.TITLE);
                    System.out.println("id author " + AUTHOR);
                    System.out.println("uciok");
                    break;
                case "isready":
                    System.out.println("readyok");
                    break;
                    /*
                case "ucinewgame":
                    parsePosition("todo");
                    break;
                    */
                case "go":
                    parseGo(input);
                    break;
                case "quit":
                    System.out.println("Good bye.");
                    exit = true;
                    break;
            }
        }
    }

    /**
     * Parse UCI "position" command.
     *
     * position fen
     * position startpos
     * ... moves e2e4 b7b8q
     *
     * @param command
     */
    private static void parsePosition(String command) {
        var commands = command.split(" ");

        if (commands[1].equals("startpos")) {
            board = new Board();
        }

        /*
2021-06-09 20:44:08,062-->1:position fen k7/8/3P4/8/3p4/8/8/K7 w - - 0 1 moves d6d7 d4d3
2021-06-09 20:44:08,063<--1:Custom Fen k7/8/3P4/8/3p4/8/8/K7
         */

        if (commands[1].equals("fen")) {
            if (commands[2] != null) {
                System.out.println("Custom Fen " + commands[2]);
                board = new Board(commands[2]);
            } else {
                System.out.println("Default Fen");
                board = new Board();
            }
        }

        // todo foreach moves -> execute move
    }

    private static void parseGo(String go) {
        System.out.println("parseGo " + go);
    }
}
