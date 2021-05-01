import java.util.ArrayList;

    /*
         8 | 56 57 58 59 60 61 62 63
         7 | 48 49 50 51 52 53 54 55
         6 | 40 41 42 43 44 45 46 47
         5 | 32 33 34 35 36 37 38 39
         4 | 24 25 26 27 28 29 30 31
         3 | 16 17 18 19 20 21 22 23
         2 |  8  9 10 11 12 13 14 15
         1 |  0  1  2  3  4  5  6  7
           -------------------------
              A  B  C  D  E  F  G  H

      Left shift '<<' means +1 on the chessboard
      Right shift '>>>' means -1 on the chessboard

      +7  +8  +9
      -1   0  +1
      -9  -8  -7
    */

public class MoveGenerator {

    //-------------------------------------------------
    // Member
    //-------------------------------------------------

    private final ArrayList<Move> moves = new ArrayList<>();

    //-------------------------------------------------
    // Getter
    //-------------------------------------------------

    public ArrayList<Move> getMoves() {
        return moves;
    }

    //-------------------------------------------------
    // White
    //-------------------------------------------------

    public void addWhiteKnightMoves(long whiteKnights, long whitePieces) {
        while (whiteKnights != 0) {
            var fromSquare = Long.numberOfTrailingZeros(whiteKnights);
            // only move to squares that are not occupied by a white piece
            var knightMoves = Attack.knightAttacks[fromSquare] & ~whitePieces;

            while(knightMoves != 0) {
                var move = new Move('N', fromSquare, Long.numberOfTrailingZeros(knightMoves));
                moves.add(move);

                knightMoves &= knightMoves - 1;
            }

            whiteKnights &= whiteKnights - 1;
        }
    }

    public void addWhiteKingMoves(long whiteKing, long whitePieces) {
        var fromSquare = Long.numberOfTrailingZeros(whiteKing);
        var kingMoves = Attack.kingAttacks[fromSquare] & ~whitePieces;

        while (kingMoves != 0) {
            var move = new Move('K', fromSquare, Long.numberOfTrailingZeros(kingMoves));
            moves.add(move);

            kingMoves &= kingMoves - 1;
        }
    }

    public void addWhitePawnMoves(long whitePawns, long allPieces) {
        while (whitePawns != 0) {
            var fromSquare = Long.numberOfTrailingZeros(whitePawns);
            long whitePawn = 1L << fromSquare;
            long pawnMoves = getWhitePawnMoves(whitePawn, allPieces);

            while (pawnMoves != 0) {
                var move = new Move('P', fromSquare, Long.numberOfTrailingZeros(pawnMoves));
                moves.add(move);

                pawnMoves &= pawnMoves - 1;
            }

            whitePawns &= whitePawns - 1;
        }
    }

    private static long getWhitePawnMoves(long whitePawn, long allPieces) {
        long firstStep = (whitePawn << 8) & ~allPieces;
        long twoSteps = ((firstStep & Bitboard.MASK_RANK_3) << 8) & ~allPieces;

        return firstStep | twoSteps;
    }

    //-------------------------------------------------
    // Black
    //-------------------------------------------------


    //-------------------------------------------------
    // Sliding pieces
    //-------------------------------------------------

    public void addRookMoves(long piece, long allPieces, long possiblePositions, char pieceChar) {
        while (piece != 0) {
            var fromSquare = Long.numberOfTrailingZeros(piece);
            long rookMoves = Attack.getRookMoves(fromSquare, allPieces) & possiblePositions;

            while (rookMoves != 0) {
                var move = new Move(pieceChar, fromSquare, Long.numberOfTrailingZeros(rookMoves));
                moves.add(move);

                rookMoves &= rookMoves - 1;
            }

            piece &= piece - 1;
        }
    }

    public void addBishopMoves(long piece, long allPieces, long possiblePositions, char pieceChar) {
        while (piece != 0) {
            var fromSquare = Long.numberOfTrailingZeros(piece);
            long bishopMoves = Attack.getBishopMoves(fromSquare, allPieces) & possiblePositions;

            while (bishopMoves != 0) {
                var move = new Move(pieceChar, fromSquare, Long.numberOfTrailingZeros(bishopMoves));
                moves.add(move);

                bishopMoves &= bishopMoves - 1;
            }

            piece &= piece - 1;
        }
    }
}
