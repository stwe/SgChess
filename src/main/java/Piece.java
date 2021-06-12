/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Represents a Piece.
 */
public enum Piece {

    //-------------------------------------------------
    // Pieces
    //-------------------------------------------------

    WHITE_PAWN("♟ ", "P ", 'P', PieceType.PAWN, Board.Color.WHITE, EvaluationTables.PAWN_EVAL_TABLE[0], 0),
    WHITE_KNIGHT("♞ ", "N ", 'N', PieceType.KNIGHT, Board.Color.WHITE, EvaluationTables.KNIGHT_EVAL_TABLE[0], 1),
    WHITE_BISHOP("♝ ", "B ", 'B', PieceType.BISHOP, Board.Color.WHITE, EvaluationTables.BISHOP_EVAL_TABLE[0], 2),
    WHITE_ROOK("♜ ", "R ", 'R', PieceType.ROOK, Board.Color.WHITE, EvaluationTables.ROOK_EVAL_TABLE[0], 3),
    WHITE_QUEEN("♛ ", "Q ", 'Q', PieceType.QUEEN, Board.Color.WHITE, EvaluationTables.QUEEN_EVAL_TABLE[0], 4),
    WHITE_KING("♚ ", "K ", 'K', PieceType.KING, Board.Color.WHITE, EvaluationTables.KING_EVAL_TABLE[0], 5),

    BLACK_PAWN("♙ ", "p ", 'p', PieceType.PAWN, Board.Color.BLACK, EvaluationTables.PAWN_EVAL_TABLE[1], 6),
    BLACK_KNIGHT("♘ ", "n ", 'n', PieceType.KNIGHT, Board.Color.BLACK, EvaluationTables.KNIGHT_EVAL_TABLE[1], 7),
    BLACK_BISHOP("♗ ", "b ", 'b', PieceType.BISHOP, Board.Color.BLACK, EvaluationTables.BISHOP_EVAL_TABLE[1], 8),
    BLACK_ROOK("♖ ", "r ", 'r', PieceType.ROOK, Board.Color.BLACK, EvaluationTables.ROOK_EVAL_TABLE[1], 9),
    BLACK_QUEEN("♕ ", "q ", 'q', PieceType.QUEEN, Board.Color.BLACK, EvaluationTables.QUEEN_EVAL_TABLE[1], 10),
    BLACK_KING("♔ ", "k ", 'k', PieceType.KING, Board.Color.BLACK, EvaluationTables.KING_EVAL_TABLE[1], 11),

    NO_PIECE("x ", "x ", 'x', PieceType.NO_PIECE, Board.Color.NONE, new int[64], 12);

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    /**
     * UTF8 Chess symbol.
     * Used for a pretty readable string representation of this piece in a chessboard.
     */
    public final String symbol;

    /**
     * Used for a readable string representation of this piece in a chessboard.
     */
    public final String letter;

    /**
     * Used for the chess notation.
     * White pieces are represented as upper case letters and black pieces are
     * represented as lower case letters.
     */
    public final char moveLetter;

    /**
     * The {@link PieceType} of the piece.
     */
    public final PieceType pieceType;

    /**
     * The {@link Board.Color} of the piece.
     */
    public final Board.Color color;

    /**
     * An {@link EvaluationTables}.
     */
    public final int[] evaluationTable;

    /**
     * The ordinal value.
     */
    public final int value;

    /**
     * To get the {@link Piece} by ordinal value.
     */
    public final Piece[] values = new Piece[13];

    //-------------------------------------------------
    // Ctors.
    //-------------------------------------------------

    /**
     * Constructs a new {@link Piece} enum.
     *
     * @param symbol {@link #symbol}.
     * @param letter {@link #letter}.
     * @param moveLetter {@link #moveLetter}.
     * @param pieceType {@link #pieceType}.
     * @param color {@link #color}
     * @param evaluationTable {@link #evaluationTable}
     * @param value {@link #value}.
     */
    Piece(String symbol, String letter, char moveLetter, PieceType pieceType, Board.Color color, int[] evaluationTable, int value) {
        this.symbol = symbol;
        this.letter = letter;
        this.moveLetter = moveLetter;
        this.pieceType = pieceType;
        this.value = value;
        this.color = color;
        this.evaluationTable = evaluationTable;
        this.values[value] = this;
    }
}
