/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.io.IOException;
import java.util.Scanner;

public class Main {

    //-------------------------------------------------
    // SgChess
    //-------------------------------------------------

    public static void main(String[] args) throws IOException, IllegalAccessException {
        clearConsole();

        System.out.println("========================================================");
        System.out.println();
        System.out.println("_______ _______ _______ _     _ _______ _______ ________");
        System.out.println("|______ |  ____ |       |_____| |______ |______ |_______");
        System.out.println("______| |_____| |______ |     | |______ ______| _______|");
        System.out.println();
        System.out.println("========================================================");

        // load config
        ConfigLoader.load(Config.class, "/config.properties");

        // setup board to start position
        var board = new Board();

        // show board
        System.out.println(board);

        // read commands
        var keyboard = new Scanner(System.in);

        Move move = null;

        // game loop
        var exit = false;
        while (!exit) {
            var input = keyboard.nextLine();

            if(input != null) {
                // command given
                if (input.length() == 1) {
                    switch (input) {
                        case "h" :
                            helpMenu();
                            break;
                        case "q" :
                            System.out.println("Good bye.");
                            exit = true;
                            break;
                        case "l" :
                            listPseudoLegalMoves(board);
                            break;
                        case "b" :
                            System.out.println(board);
                            break;
                        case "c" :
                            clearConsole();
                            break;
                        case "u" :
                            if (move != null) {
                                board.undoMove(move);
                                System.out.println("Success: press b to show the new board.");
                            } else {
                                System.out.println("Error: there is no move to undo.");
                            }
                            break;
                        default:
                            System.out.println("Error: invalid command given. Press h for help or q for quit.");
                    }
                } else if (input.length() == 4 || input.length() == 5) {
                    move = board.parseMove(input);
                    if (move != null) {
                        board.makeMove(move);
                        System.out.println("Success: press b to show the new board.");
                    } else {
                        System.out.println("Error: there was no move found.");
                    }
                } else {
                    System.out.println("Error: invalid move or command given. Press h for help or q for quit.");
                }
            }
        }

        keyboard.close();
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    private static void helpMenu() {
        System.out.println("h help");
        System.out.println("q exit");
        System.out.println("l list pseudo legal moves");
        System.out.println("b show board");
        System.out.println("c clear screen");
        System.out.println("u undo last move");
    }

    private static void listPseudoLegalMoves(Board board) {
        var mg = new MoveGenerator(board);
        mg.generatePseudoLegalMoves();
        System.out.println("Moves list (" + mg.getPseudoLegalMoves().size() + "):");
        for (var pseudoLegalMove : mg.getPseudoLegalMoves()) {
            System.out.println(pseudoLegalMove);
        }
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();;
    }
}
