/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
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
            var command = input.split(" ")[0];

            switch (command) {
                case "uci":
                    System.out.println("id name " + Config.TITLE);
                    System.out.println("id author " + AUTHOR);
                    System.out.println("uciok");
                    break;
                case "isready":
                    System.out.println("readyok");
                    break;
                case "position":
                    parsePosition(input);
                    break;
                case "ucinewgame":
                    parsePosition("position startpos");
                    break;
                case "go":
                    parseGo(input);
                    break;
                case "quit":
                    System.out.println("Good bye.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid command given.");
            }
        }
    }

    /**
     * Parse an UCI "position" command.
     *
     * position fen
     * position startpos
     * ... moves e2e4 b7b8q
     *
     * @param command An UCI "position" command string.
     */
    private static void parsePosition(String command) {
        var commands = command.split(" ");

        if (commands.length == 1) {
            System.out.println("Invalid position command given.");
            return;
        }

        if (commands[1].equals("startpos")) {
            // default Fen (startpos), no moves
            board = new Board();

            // default Fen (startpos) with moves to execute
            if (commands.length > 2) {
                doMoves(Arrays.copyOfRange(commands, 3, commands.length));
            }
        }

        if (commands[1].equals("fen")) {
            // default Fen, no moves
            if (commands.length == 2) {
                board = new Board();
            }

            // custom full Fen, no moves
            if (commands.length == 8) {
                var sb = new StringBuilder();
                for(int i = 2; i < commands.length; i++) {
                    if (i != 2) {
                        sb.append(" ");
                    }

                    sb.append(commands[i]);
                }

                board = new Board(sb.toString());
            }

            // custom full Fen with moves to execute
            if (commands.length > 8) {
                var sb = new StringBuilder();
                for(int i = 2; i < 8; i++) {
                    if (i != 2) {
                        sb.append(" ");
                    }

                    sb.append(commands[i]);
                }

                board = new Board(sb.toString());

                doMoves(Arrays.copyOfRange(commands, 9, commands.length));
            }
        }

        System.out.println(board);
    }

    private static void doMoves(String[] moves) {
        Objects.requireNonNull(board, "board must not be null");
        for (String input : moves) {
            var move = board.parseMove(input);
            if (move != null) {
                board.makeMove(move);
            } else {
                System.out.println("Error while parsing move.");
            }
        }
    }

    private static void parseGo(String go) {
        System.out.println("parseGo " + go);
    }
}
