/*
 * This file is part of the SgChess project.
 * Copyright (c) 2021 stwe <https://github.com/stwe/SgChess>
 * License: GNU GPLv2
 */

/**
 * Represents a PieceType.
 */
public enum PieceType {
    NO_PIECE(0),
    PAWN(1),
    KNIGHT(2),
    BISHOP(3),
    ROOK(4),
    QUEEN(5),
    KING(6);

    /**
     * The ordinal value.
     */
    public int value;

    /**
     * To get the {@link PieceType} by ordinal value.
     */
    public final PieceType[] values = new PieceType[7];

    /**
     * Returns the bitboard index of a white or black {@link PieceType}.
     * If an invalid color is passed, -1 is returned.
     *
     * @param pieceType The {@link PieceType}.
     * @param color {@link Board.Color}.
     *
     * @return An index or -1 if an invalid color was passed.
     */
    public static int getBitboardNumber(PieceType pieceType, Board.Color color) {
        if (color == Board.Color.NONE) {
            return -1;
        }

        return (pieceType.value + color.value * 6) - 1;
    }

    /**
     * Constructs a new {@link PieceType} enum.
     *
     * @param value {@link #value}.
     */
    PieceType(int value) {
        this.value = value;
        this.values[value] = this;
    }
}
