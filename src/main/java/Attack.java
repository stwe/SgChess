/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

import java.util.HashMap;

/**
 * Represents a Attack object.
 * Some precomputed stuff for the move generator.
 */
public class Attack {

    //-------------------------------------------------
    // Constants
    //-------------------------------------------------

    private static final long[] ROOK_MAGIC_NUMBERS = {
            0x0A8002C000108020L, 0x06C00049B0002001L, 0x0100200010090040L, 0x2480041000800801L,
            0x0280028004000800L, 0x0900410008040022L, 0x0280020001001080L, 0x2880002041000080L,
            0xA000800080400034L, 0x0004808020004000L, 0x2290802004801000L, 0x0411000D00100020L,
            0x0402800800040080L, 0x000B000401004208L, 0x2409000100040200L, 0x0001002100004082L,
            0x0022878001E24000L, 0x1090810021004010L, 0x0801030040200012L, 0x0500808008001000L,
            0x0A08018014000880L, 0x8000808004000200L, 0x0201008080010200L, 0x0801020000441091L,
            0x0000800080204005L, 0x1040200040100048L, 0x0000120200402082L, 0x0D14880480100080L,
            0x0012040280080080L, 0x0100040080020080L, 0x9020010080800200L, 0x0813241200148449L,
            0x0491604001800080L, 0x0100401000402001L, 0x4820010021001040L, 0x0400402202000812L,
            0x0209009005000802L, 0x0810800601800400L, 0x4301083214000150L, 0x204026458E001401L,
            0x0040204000808000L, 0x8001008040010020L, 0x8410820820420010L, 0x1003001000090020L,
            0x0804040008008080L, 0x0012000810020004L, 0x1000100200040208L, 0x430000A044020001L,
            0x0280009023410300L, 0x00E0100040002240L, 0x0000200100401700L, 0x2244100408008080L,
            0x0008000400801980L, 0x0002000810040200L, 0x8010100228810400L, 0x2000009044210200L,
            0x4080008040102101L, 0x0040002080411D01L, 0x2005524060000901L, 0x0502001008400422L,
            0x489A000810200402L, 0x0001004400080A13L, 0x4000011008020084L, 0x0026002114058042L,
    };

    private static final long[] BISHOP_MAGIC_NUMBERS = {
            0x89a1121896040240L, 0x2004844802002010L, 0x2068080051921000L, 0x62880a0220200808L,
            0x0004042004000000L, 0x0100822020200011L, 0xc00444222012000aL, 0x0028808801216001L,
            0x0400492088408100L, 0x0201c401040c0084L, 0x00840800910a0010L, 0x0000082080240060L,
            0x2000840504006000L, 0x30010c4108405004L, 0x1008005410080802L, 0x8144042209100900L,
            0x0208081020014400L, 0x004800201208ca00L, 0x0F18140408012008L, 0x1004002802102001L,
            0x0841000820080811L, 0x0040200200a42008L, 0x0000800054042000L, 0x88010400410c9000L,
            0x0520040470104290L, 0x1004040051500081L, 0x2002081833080021L, 0x000400c00c010142L,
            0x941408200c002000L, 0x0658810000806011L, 0x0188071040440a00L, 0x4800404002011c00L,
            0x0104442040404200L, 0x0511080202091021L, 0x0004022401120400L, 0x80c0040400080120L,
            0x8040010040820802L, 0x0480810700020090L, 0x0102008e00040242L, 0x0809005202050100L,
            0x8002024220104080L, 0x0431008804142000L, 0x0019001802081400L, 0x0200014208040080L,
            0x3308082008200100L, 0x041010500040c020L, 0x4012020c04210308L, 0x208220a202004080L,
            0x0111040120082000L, 0x6803040141280a00L, 0x2101004202410000L, 0x8200000041108022L,
            0x0000021082088000L, 0x0002410204010040L, 0x0040100400809000L, 0x0822088220820214L,
            0x0040808090012004L, 0x00910224040218c9L, 0x0402814422015008L, 0x0090014004842410L,
            0x0001000042304105L, 0x0010008830412a00L, 0x2520081090008908L, 0x40102000a0a60140L,
    };

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * Possible positions of movement for any color king.
     */
    private static final long[] kingMoveBitboards;

    /**
     * Possible positions of movement for any color knight.
     */
    private static final long[] knightMoveBitboards;

    /**
     * Possible positions of attack for any white pawn.
     */
    private static final long[] whitePawnAttackBitboards;

    /**
     * Possible positions of attack for any black pawn.
     */
    private static final long[] blackPawnAttackBitboards;

    /**
     * Rook {@link Magic} objects.
     */
    private static final Magic[] rookMagics;

    /**
     * Bishop {@link Magic} objects.
     */
    private static final Magic[] bishopMagics;

    //-------------------------------------------------
    // Init
    //-------------------------------------------------

    static {
        kingMoveBitboards = calcKingMoveBitboards();
        knightMoveBitboards = calcKnightMoveBitboards();
        whitePawnAttackBitboards = calcWhitePawnAttackBitboards();
        blackPawnAttackBitboards = calcBlackPawnAttackBitboards();

        /*
            Rook on e4:
            -----------

            The blocker mask        A blocker board         The move board
            ----------------        ---------------         --------------
            0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0
            0 1 1 1 0 1 1 0         0 1 1 0 0 0 0 0         0 0 1 1 0 1 1 1
            0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 1 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0
            0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0

            The blocker mask is all of the squares that can be occupied and block your piece.
            A blocker board is a subset of the blocker mask.
            There are 2^bits blocker boards, where bits is the number of 1's in the blocker mask.
        */

        rookMagics = new Magic[64];
        bishopMagics = new Magic[64];

        calcRookBlockerMasks();
        calcBishopBlockerMasks();

        var rookBlockerBoards = calcBlockerBoards(rookMagics);
        var bishopBlockerBoards = calcBlockerBoards(bishopMagics);

        calcShifts();

        calcRookMoveBoards(rookBlockerBoards);
        calcBishopMoveBoards(bishopBlockerBoards);
    }

    //-------------------------------------------------
    // Nonsliding pieces
    //-------------------------------------------------

    /**
     * Get moves for any color king.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     *
     * @return A bitboard with all king moves.
     */
    public static long getKingMoves(Bitboard.BitIndex bitIndex) {
        return kingMoveBitboards[bitIndex.ordinal()];
    }

    /**
     * Get moves for any color knight.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     *
     * @return A bitboard with all knight moves.
     */
    public static long getKnightMoves(Bitboard.BitIndex bitIndex) {
        return knightMoveBitboards[bitIndex.ordinal()];
    }

    /**
     * Get white pawn attacks.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     *
     * @return A bitboard with all white pawn attacks.
     */
    public static long getWhitePawnAttacks(Bitboard.BitIndex bitIndex) {
        return whitePawnAttackBitboards[bitIndex.ordinal()];
    }

    /**
     * Get black pawn attacks.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     *
     * @return A bitboard with all black pawn attacks.
     */
    public static long getBlackPawnAttacks(Bitboard.BitIndex bitIndex) {
        return blackPawnAttackBitboards[bitIndex.ordinal()];
    }

    /**
     * Get pawn attacks by {@link Board.Color} and {@link Bitboard.BitIndex}.
     *
     * @param color {@link Board.Color}.
     *
     * @return A bitboard with all pawn attacks.
     */
    public static long getPawnAttacks(Board.Color color, Bitboard.BitIndex bitIndex) {
        var attacksBitboard = 0L;

        switch (color) {
            case WHITE:
                attacksBitboard = getWhitePawnAttacks(bitIndex);
                break;
            case BLACK:
                attacksBitboard = getBlackPawnAttacks(bitIndex);
                break;
            default:
        }

        return attacksBitboard;
    }

    //-------------------------------------------------
    // Sliding pieces
    //-------------------------------------------------

    /**
     * Get moves for any color rook.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     * @param allPieces The bitboard with all pieces.
     *
     * @return A bitboard with all rook moves.
     */
    public static long getRookMoves(Bitboard.BitIndex bitIndex, long allPieces) {
        var magic = rookMagics[bitIndex.ordinal()];
        return magic.moveBoards[(int) ((allPieces & magic.blockerMask) * ROOK_MAGIC_NUMBERS[bitIndex.ordinal()] >>> magic.shift)];
    }

    /**
     * Get moves for any color bishop.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     * @param allPieces The bitboard with all pieces.
     *
     * @return A bitboard with all bishop moves.
     */
    public static long getBishopMoves(Bitboard.BitIndex bitIndex, long allPieces) {
        var magic = bishopMagics[bitIndex.ordinal()];
        return magic.moveBoards[(int) ((allPieces & magic.blockerMask) * BISHOP_MAGIC_NUMBERS[bitIndex.ordinal()] >>> magic.shift)];
    }

    /**
     * Get moves for any color queen.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the from square.
     * @param allPieces The bitboard with all pieces.
     *
     * @return A bitboard with all queen moves.
     */
    public static long getQueenMoves(Bitboard.BitIndex bitIndex, long allPieces) {
        return getRookMoves(bitIndex, allPieces) | getBishopMoves(bitIndex, allPieces);
    }

    //-------------------------------------------------
    // Square attacks
    //-------------------------------------------------

    /**
     * Get a bitboard with all attackers to a given square.
     *
     * @param color Which {@link Board.Color} is under attack.
     * @param bitIndex The {@link Bitboard.BitIndex} of the square which is under attack.
     * @param board A {@link Board} object.
     *
     * @return The bitboard with all attackers.
     */
    public static long getAttackersToSquare(Board.Color color, Bitboard.BitIndex bitIndex, Board board) {
        var attackersBitboard = 0L;

        if (color == Board.Color.NONE) {
            return attackersBitboard;
        }

        var enemyColor = color.getEnemyColor();

        attackersBitboard |= getKingMoves(bitIndex) & board.getKing(enemyColor);
        attackersBitboard |= getKnightMoves(bitIndex) & board.getKnights(enemyColor);
        attackersBitboard |= getPawnAttacks(color, bitIndex) & board.getPawns(enemyColor);
        attackersBitboard |= getRookMoves(bitIndex, board.getAllPieces()) & board.getRooks(enemyColor);
        attackersBitboard |= getBishopMoves(bitIndex, board.getAllPieces()) & board.getBishops(enemyColor);
        attackersBitboard |= getQueenMoves(bitIndex, board.getAllPieces()) & board.getQueens(enemyColor);

        return attackersBitboard;
    }

    /**
     * Convenience method to determine if a king in check.
     *
     * @param color The {@link Board.Color} of the king.
     * @param board The {@link Board}.
     *
     * @return boolean
     */
    public static boolean isCheck(Board.Color color, Board board) {
        if (color == Board.Color.NONE) {
            return false;
        }

        // get the position of the king (white or black)
        var kingBitIndex = Bitboard.getLsb(board.getKing(color));

        // determine if the position is under attack
        if (color == Board.Color.WHITE) {
            return isWhiteSquareAttacked(kingBitIndex, board);
        }

        return isBlackSquareAttacked(kingBitIndex, board);
    }

    /**
     * Checks whether a white square is under attack.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the square which is under attack.
     * @param board A {@link Board} object.
     *
     * @return boolean
     */
    public static boolean isWhiteSquareAttacked(Bitboard.BitIndex bitIndex, Board board) {
        if ((getPawnAttacks(Board.Color.WHITE, bitIndex) & board.getBlackPawns()) != 0) {
            return true;
        }

        if ((getKnightMoves(bitIndex) & board.getBlackKnights()) != 0) {
            return true;
        }

        if ((getKingMoves(bitIndex) & board.getBlackKing()) != 0) {
            return true;
        }

        if ((getRookMoves(bitIndex, board.getAllPieces()) & board.getBlackRooks()) != 0) {
            return true;
        }

        if ((getBishopMoves(bitIndex, board.getAllPieces()) & board.getBlackBishops()) != 0) {
            return true;
        }

        return (getQueenMoves(bitIndex, board.getAllPieces()) & board.getBlackQueens()) != 0;
    }

    /**
     * Checks whether a white square is under attack.
     *
     * @param file A {@link Bitboard.File}.
     * @param rank A {@link Bitboard.Rank}.
     * @param board A {@link Board} object.
     *
     * @return boolean
     */
    public static boolean isWhiteSquareAttacked(Bitboard.File file, Bitboard.Rank rank, Board board) {
        return isWhiteSquareAttacked(Bitboard.getBitIndexByFileAndRank(file, rank), board);
    }

    /**
     * Checks whether a black square is under attack.
     *
     * @param bitIndex The {@link Bitboard.BitIndex} of the square which is under attack.
     * @param board A {@link Board} object.
     *
     * @return boolean
     */
    public static boolean isBlackSquareAttacked(Bitboard.BitIndex bitIndex, Board board) {
        if ((getPawnAttacks(Board.Color.BLACK, bitIndex) & board.getWhitePawns()) != 0) {
            return true;
        }

        if ((getKnightMoves(bitIndex) & board.getWhiteKnights()) != 0) {
            return true;
        }

        if ((getKingMoves(bitIndex) & board.getWhiteKing()) != 0) {
            return true;
        }

        if ((getRookMoves(bitIndex, board.getAllPieces()) & board.getWhiteRooks()) != 0) {
            return true;
        }

        if ((getBishopMoves(bitIndex, board.getAllPieces()) & board.getWhiteBishops()) != 0) {
            return true;
        }

        return (getQueenMoves(bitIndex, board.getAllPieces()) & board.getWhiteQueens()) != 0;
    }

    /**
     * Checks whether a black square is under attack.
     *
     * @param file A {@link Bitboard.File}.
     * @param rank A {@link Bitboard.Rank}.
     * @param board A {@link Board} object.
     *
     * @return boolean
     */
    public static boolean isBlackSquareAttacked(Bitboard.File file, Bitboard.Rank rank, Board board) {
        return isBlackSquareAttacked(Bitboard.getBitIndexByFileAndRank(file, rank), board);
    }

    /**
     * Checks if one or more squares are attacked.
     *
     * @param color Which {@link Board.Color} is under attack.
     * @param board A {@link Board} object.
     * @param bitIndices The {@link Bitboard.BitIndex} of the squares which are under attack.
     *
     * @return boolean
     */
    public static boolean areOneOrMoreSquaresAttacked(Board.Color color, Board board, Bitboard.BitIndex ... bitIndices) {
        if (color == Board.Color.NONE) {
            return false;
        }

        for (var bitIndex : bitIndices) {
            if (color == Board.Color.WHITE) {
                if (isWhiteSquareAttacked(bitIndex, board)) {
                    return true;
                }
            } else {
                if (isBlackSquareAttacked(bitIndex, board)) {
                    return true;
                }
            }
        }

        return false;
    }

    //-------------------------------------------------
    // Blocker masks
    //-------------------------------------------------

    /**
     * Precompute the blocker masks for any color rook.
     */
    private static void calcRookBlockerMasks() {
        for (var square = 0; square < 64; square++) {
            rookMagics[square] = new Magic();

            for (var i = square + 8; i < 64 - 8; i += 8) {
                rookMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (var i = square - 8; i >= 8; i -= 8) {
                rookMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (var i = square + 1; i % 8 != 0 && i % 8 != 7; i++) {
                rookMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (var i = square - 1; i % 8 != 7 && i % 8 != 0 && i > 0; i--) {
                rookMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }
        }
    }

    /**
     * Precompute the blocker masks for any color bishop.
     */
    private static void calcBishopBlockerMasks() {
        for (var square = 0; square < 64; square++) {
            bishopMagics[square] = new Magic();

            for (int i = square + 7; i < 64 - 7 && i % 8 != 7 && i % 8 != 0; i += 7) {
                bishopMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (int i = square + 9; i < 64 - 9 && i % 8 != 7 && i % 8 != 0; i += 9) {
                bishopMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (int i = square - 9; i >= 9 && i % 8 != 7 && i % 8 != 0; i -= 9) {
                bishopMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }

            for (int i = square - 7; i >= 7 && i % 8 != 7 && i % 8 != 0; i -= 7) {
                bishopMagics[square].blockerMask |= Bitboard.SQUARES[i];
            }
        }
    }

    //-------------------------------------------------
    // Blocker boards
    //-------------------------------------------------

    /**
     * Precompute the blocker boards for any color rook or bishop.
     * @see <a href="https://stackoverflow.com/questions/30680559/how-to-find-magic-bitboards">how-to-find-magic-bitboards</a>
     *
     * @param magics An {@link Magic} array object.
     *
     * @return The precomputed blocker boards.
     */
    private static long[][] calcBlockerBoards(Magic[] magics) {
        long[][] blockerBoards = new long[64][];

        var square = 0;
        for (var magic : magics) {
            var bitCount = Long.bitCount(magic.blockerMask);

            // there are 2^bitCount blocker boards
            var blockerBoardCount = 1 << bitCount;
            blockerBoards[square] = new long[blockerBoardCount];

            for (int i = 0; i < blockerBoardCount; i++) {
                blockerBoards[square][i] = generateBlockerboard(i, magic.blockerMask);
            }

            square++;
        }

        return blockerBoards;
    }

    /*
       Example:
                      52      44      36         25   20      12              i       (Square)
                      |       |       |          |    |       |
                      9       8       7     65 432    1       0               counter (i'th bit is set)
       Rook on E4:    10000000100000001000001101110000100000001000000000000

       i = 1000:      00000000000000000000000000000000000000000001111101000
       1 << 0:        00000000000000000000000000000000000000000000000000001   0 delete 12
       1 << 1:        00000000000000000000000000000000000000000000000000010   0 delete 20
       1 << 2:        00000000000000000000000000000000000000000000000000100   0 delete 25
       1 << 3:        00000000000000000000000000000000000000000000000001000   1
       1 << 4:        00000000000000000000000000000000000000000000000010000   0 delete 27
       1 << 5:        00000000000000000000000000000000000000000000000100000   1
       1 << 6:        00000000000000000000000000000000000000000000001000000   1
       1 << 7:        00000000000000000000000000000000000000000000010000000   1
       1 << 8:        00000000000000000000000000000000000000000000100000000   1
       1 << 9:        00000000000000000000000000000000000000000001000000000   1
    */

    /**
     * Generate a unique blocker board, given an index (0..2^bits) and the blocker mask.
     * @see <a href="https://stackoverflow.com/questions/30680559/how-to-find-magic-bitboards">how-to-find-magic-bitboards</a>
     *
     * @param index An index (0..2^bits).
     * @param blockerMask Containing all squares that can block a piece.
     *
     * @return A unique blocker board.
     */
    private static long generateBlockerboard(int index, long blockerMask) {
        // start with a blocker board identical to the mask
        long blockerBoard = blockerMask;

        int counter = 0;
        for (int i = 0; i < 64; i++) {
            // check if the i'th bit is set in the mask (and thus a potential blocker)
            if ((blockerMask & (1L << i)) != 0) {

                if ((index & (1 << counter)) == 0) {
                    // clear the i'th bit in the blockerboard if it's clear in the index at bitindex
                    blockerBoard &= ~(1L << i);
                }

                counter++;
            }
        }

        return blockerBoard;
    }

    //-------------------------------------------------
    // Move boards
    //-------------------------------------------------

    private static void calcRookMoveBoards(long[][] rookBlockerBoards) {
        /*

        Example: Rook on A1 with second blocker board (rookBlockerBoards[0][1])

        A1 blocker mask
        ***************
        -
        x
        x
        x
        x
        x
        x
        R x x x x x x -

        blocker board - rookBlockerBoards[0][1]
        ***************************************
        -
        -
        -
        -
        -
        -
        -
        R x - - - - - -

        move board for the above blocker board
        **************************************
        x
        x
        x
        x
        x
        x
        x
        R x - - - - - -

        Nun gibt es für das Feld A1 4096 verschiedene move boards,
        wenn der Turm auf A1 steht, welches move board ist dann das richtige?

        1) hole moveMask für den Turm auf A1
        2) allPieces Bitboard & moveMask verknüpfen; man erhält ein blockBoard
           Steht z.B. eine Figur auf B1:
            -
            -
            -
            -
            -
            -
            -
            - x - - - - - -

            oder: 0000000000000000000000000000000000000000000000000000000000000010 (dec = 2)

        3) blocker board von 2) mit einer magic number multiplizieren

        */

        for (var square = 0; square < 64; square++) {
            // a move board for each blocker board
            var blockerBoardCount = rookBlockerBoards[square].length;
            rookMagics[square].moveBoards = new long[blockerBoardCount];

            var controlHashMap = new HashMap<Integer, Integer>();

            // for each blocker board ...
            for (var i = 0; i < blockerBoardCount; i++) {
                var moves = 0L;

                // generate the moves
                for (var j = square + 8; j < 64; j += 8) {
                    moves |= Bitboard.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (var j = square - 8; j >= 0; j -= 8) {
                    moves |= Bitboard.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (var j = square + 1; j % 8 != 0; j++) {
                    moves |= Bitboard.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (var j = square - 1; j % 8 != 7 && j >= 0; j--) {
                    moves |= Bitboard.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                // generate the hash key
                var key = (int) ((rookBlockerBoards[square][i] * ROOK_MAGIC_NUMBERS[square]) >>> rookMagics[square].shift);
                // todo: temp code
                if (controlHashMap.containsKey(key)) {
                    throw new RuntimeException("Invalid magic number!");
                }
                controlHashMap.put(key, key);

                rookMagics[square].moveBoards[key] = moves;
            }

            // todo: temp code
            if (controlHashMap.size() != blockerBoardCount) {
                throw new RuntimeException("Unexpected error.");
            }
        }
    }

    private static void calcBishopMoveBoards(long[][] bishopBlockerBoards) {
        for (var square = 0; square < 64; square++) {
            // a move board for each blocker board
            var blockerBoardCount = bishopBlockerBoards[square].length;
            bishopMagics[square].moveBoards = new long[blockerBoardCount];

            var controlHashMap = new HashMap<Integer, Integer>();

            // for each blocker board ...
            for (var i = 0; i < blockerBoardCount; i++) {
                var moves = 0L;

                // generate the moves
                for (int j = square + 7; j % 8 != 7 && j < 64; j += 7) {
                    moves |= Bitboard.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square + 9; j % 8 != 0 && j < 64; j += 9) {
                    moves |= Bitboard.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 9; j % 8 != 7 && j >= 0; j -= 9) {
                    moves |= Bitboard.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 7; j % 8 != 0 && j >= 0; j -= 7) {
                    moves |= Bitboard.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & Bitboard.SQUARES[j]) != 0) {
                        break;
                    }
                }

                // generate the hash key
                var key = (int) ((bishopBlockerBoards[square][i] * BISHOP_MAGIC_NUMBERS[square]) >>> bishopMagics[square].shift);
                // todo: temp code
                if (controlHashMap.containsKey(key)) {
                    throw new RuntimeException("Invalid magic number!");
                }
                controlHashMap.put(key, key);

                bishopMagics[square].moveBoards[key] = moves;
            }

            // todo: temp code
            if (controlHashMap.size() != blockerBoardCount) {
                throw new RuntimeException("Unexpected error.");
            }
        }
    }

    //-------------------------------------------------
    // Calculate king moves
    //-------------------------------------------------

    /**
     * Calculate every possible position of movement for any color king.
     *
     * @return A precomputed lookup table.
     */
    private static long[] calcKingMoveBitboards() {
        var kingMoveBitboards = new long[64];

        for (var square = 0; square < 64; square++) {
            long squareBitboard = Bitboard.SQUARES[square];
            long mask =
                    (((squareBitboard >>> 1) | (squareBitboard << 7) | (squareBitboard >>> 9)) & Bitboard.CLEAR_FILE_H) |
                    (((squareBitboard << 1) | (squareBitboard << 9) | (squareBitboard >>> 7)) & Bitboard.CLEAR_FILE_A) |
                    (squareBitboard << 8) | (squareBitboard >>> 8);

            kingMoveBitboards[square] = mask;
        }

        return kingMoveBitboards;
    }

    //-------------------------------------------------
    // Calculate knight moves
    //-------------------------------------------------

    /**
     * Calculate every possible position of movement for any color knight.
     *
     * @return A precomputed lookup table.
     */
    private static long[] calcKnightMoveBitboards() {
        var knightMoveBitboards = new long[64];

        for (var square = 0; square < 64; square++) {
            long squareBitboard = Bitboard.SQUARES[square];
            long mask =
                    (((squareBitboard <<  6) | (squareBitboard >>> 10)) & Bitboard.CLEAR_FILE_GH) |
                    (((squareBitboard << 15) | (squareBitboard >>> 17)) & Bitboard.CLEAR_FILE_H) |
                    (((squareBitboard << 17) | (squareBitboard >>> 15)) & Bitboard.CLEAR_FILE_A) |
                    (((squareBitboard << 10) | (squareBitboard >>>  6)) & Bitboard.CLEAR_FILE_AB);

            knightMoveBitboards[square] = mask;
        }

        return knightMoveBitboards;
    }

    //-------------------------------------------------
    // Calculate white pawn attacks
    //-------------------------------------------------

    /**
     * Calculate every possible position of attack for any white pawn.
     *
     * @return A precomputed lookup table.
     */
    private static long[] calcWhitePawnAttackBitboards() {
        var pawnAttackBitboards = new long[64];

        for (var square = 0; square < 64; square++) {
            long squareBitboard = Bitboard.SQUARES[square];
            long rightBitboard = (squareBitboard << 9) & Bitboard.CLEAR_FILE_A;
            long leftBitboard = (squareBitboard << 7) & Bitboard.CLEAR_FILE_H;

            pawnAttackBitboards[square] = rightBitboard | leftBitboard;
        }

        return pawnAttackBitboards;
    }

    //-------------------------------------------------
    // Calculate black pawn attacks
    //-------------------------------------------------

    /**
     * Calculate every possible position of attack for any black pawn.
     *
     * @return A precomputed lookup table.
     */
    private static long[] calcBlackPawnAttackBitboards() {
        var pawnAttackBitboards = new long[64];

        for (var square = 0; square < 64; square++) {
            long squareBitboard = Bitboard.SQUARES[square];
            long rightBitboard = (squareBitboard >>> 9) & Bitboard.CLEAR_FILE_H;
            long leftBitboard = (squareBitboard >>> 7) & Bitboard.CLEAR_FILE_A;

            pawnAttackBitboards[square] = rightBitboard | leftBitboard;
        }

        return pawnAttackBitboards;
    }

    //-------------------------------------------------
    // Helper
    //-------------------------------------------------

    /**
     * Precompute how many bits are shifted to the right.
     *
     * For example, a rook on a1 requires a 12-bit database and we therefore have to shift right with 52 bits,
     * leaving an index in the range [0 - 4095].
     */
    private static void calcShifts() {
        for (var i = 0; i < 64; i++) {
            rookMagics[i].shift = 64 - Long.bitCount(rookMagics[i].blockerMask);
            bishopMagics[i].shift = 64 - Long.bitCount(bishopMagics[i].blockerMask);
        }
    }
}
