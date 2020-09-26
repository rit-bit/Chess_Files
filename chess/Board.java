package rps.chess;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Board {

    /**
     * @return the whiteTurnNext
     */
    public boolean isWhiteTurnNext() {
        return whiteTurnNext;
    }

    private final Piece[][] board = new Piece[8][8];
    private final boolean[][] possibleMoves = new boolean[8][8];
    private boolean hasWhiteKingMoved = false;
    private boolean hasBlacKingMoved = false;
    private boolean hasBlackRook0Moved = false;
    private boolean hasBlackRook7Moved = false;
    private boolean hasWhiteRook0Moved = false;
    private boolean hasWhiteRook7Moved = false;
    private boolean whiteTurnNext = true;
    private Piece pawnMovedActivatesEnPassant = null;

    public Board() {
        // Create white pawn line
        createPawnLine(true, 6);

        // Create black pawn line
        createPawnLine(false, 1);

        // Create white pieces line
        createPiecesLine(true, 7);

        // Create black pieces line
        createPiecesLine(false, 0);

        // initialise possibleMoves values to all be false
        for (boolean[] bools : possibleMoves) {
            for (boolean b : bools) {
                b = false;
            }
        }
    }

    /**
     * Copy constructor to create a new Board object the same as the original
     * Board.
     *
     * @param original The Board to be copied.
     */
    Board(Board original) {
        // create copies of all the pieces on the board, whilst also duplicating values of possibleMoves
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // First copy possibleMoves value
                if (original.possibleMoves[x][y] == true) {
                    this.possibleMoves[x][y] = true;
                } else {
                    this.possibleMoves[x][y] = false;
                }
                // Now copy pieces
                if (original.board[x][y] == null) {
                    board[x][y] = null;
                } else {
                    board[x][y] = new Piece(original.board[x][y]);
                }
            }
        }
        // copies all other attributes of Board       
        this.hasWhiteKingMoved = original.hasWhiteKingMoved;
        this.hasBlacKingMoved = original.hasBlacKingMoved;
        this.hasBlackRook0Moved = original.hasBlackRook0Moved;
        this.hasBlackRook7Moved = original.hasBlackRook7Moved;
        this.hasWhiteRook0Moved = original.hasWhiteRook0Moved;
        this.hasWhiteRook7Moved = original.hasWhiteRook7Moved;
        this.whiteTurnNext = original.whiteTurnNext;
        this.pawnMovedActivatesEnPassant = original.pawnMovedActivatesEnPassant;
    }

    private void createPawnLine(boolean isWhite, int row) {
        for (int col = 0; col < 8; col++) {
            board[row][col] = new Piece(PieceType.PAWN, isWhite);

            String colour = isWhite ? "White" : "Black ";

            // System.out.println(colour + "pawn created at row " + row + ", col " + col);
        }
        System.out.println();
    }

    private void createPiecesLine(boolean isWhite, int row) {
        int col = 0;
        board[row][col++] = new Piece(PieceType.ROOK, isWhite);
        board[row][col++] = new Piece(PieceType.KNIGHT, isWhite);
        board[row][col++] = new Piece(PieceType.BISHOP, isWhite);
        board[row][col++] = new Piece(PieceType.QUEEN, isWhite);
        board[row][col++] = new Piece(PieceType.KING, isWhite);
        board[row][col++] = new Piece(PieceType.BISHOP, isWhite);
        board[row][col++] = new Piece(PieceType.KNIGHT, isWhite);
        board[row][col++] = new Piece(PieceType.ROOK, isWhite);

        String colour = isWhite ? "White" : "Black ";

//        System.out.println(colour + "pieces line created on row " + row);
//        System.out.println();
    }

    @Override
    public String toString() {
        String boardText = "";
        for (int row = 8; row >= 1; row--) {
            String line = "";
            for (char col = 'A'; col <= 'H'; col++) {
                Piece p = getPiece(new ChessCoordinate(col, row));
                if (p == null) {
                    line += " -- ";
                } else {
                    switch (p.getType()) {
                        case BISHOP:
                            line += " Bi ";
                            break;
                        case KING:
                            line += " Ki ";
                            break;
                        case KNIGHT:
                            line += " Kn ";
                            break;
                        case PAWN:
                            line += " Pa ";
                            break;
                        case QUEEN:
                            line += " Qu ";
                            break;
                        case ROOK:
                            line += " Ro ";
                            break;
                        default:
                            line += " Err ";
                    }
                }
            }
            boardText += line + System.lineSeparator();
        }
        return boardText;
    }

    /**
     * Returns the ChessCoordinate location of the given piece.
     *
     * @param piece The piece at the location.
     * @return The location of the given piece.
     */
    public ChessCoordinate getLocation(Piece piece) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board[x][y] == piece) {
                    return new Coordinate(x, y).convertToChessCoordinate();
                }
            }
        }
        return null;
    }

    /**
     * Checks whether a move is valid and if so, moves the piece.
     *
     * @param piece The piece to be moved.
     * @param destin The destination of the piece.
     * @return Whether it was successful at moving the piece.
     */
    public boolean movePiece(Piece piece, ChessCoordinate destin) {
        if (!isInPossibleMoves(destin)) {
            return false;
        }
        Coordinate origin = getLocation(piece).convertToCoordinate();
        int destRow = destin.convertToCoordinate().row;
        int destCol = destin.convertToCoordinate().col;
        int originRow = origin.row;
        int originCol = origin.col;

        if (!executeMove(piece, destin)) {
            System.err.println("Move execution failed.");
            return false;
        }

        // FOR CASTLING VALIDATION
        // If piece is a rook or king, record the fact that they have moved
        if (piece.getType() == PieceType.KING) {
            if (piece.isWhite()) {
                hasWhiteKingMoved = true;
            } else {
                hasBlacKingMoved = true;
            }
        } else {
            if (piece.getType() == PieceType.ROOK) {
                if (originCol == 0) {
                    if (piece.isWhite()) {
                        hasWhiteRook0Moved = true;
                    } else {
                        hasBlackRook0Moved = true;
                    }
                } else {
                    if (originCol == 7) {
                        if (piece.isWhite()) {
                            hasWhiteRook7Moved = true;
                        } else {
                            hasBlackRook7Moved = true;
                        }
                    }
                }
            }
        }
        // FOR EN PASSANT RULE
        // for all pawns, set their booleans to false
        for (Piece[] pieces : board) {
            for (Piece p : pieces) {
                if (p == null) {
                    continue;
                }
                p.setPawnHasJustMoved2(false);
            }
        }
        pawnMovedActivatesEnPassant = null;

        // if pawn has just moved two, update its boolean to true
        int xDif = Math.abs(originRow - destRow);
        if ((piece.getType() == PieceType.PAWN) && (xDif == 2)) {
            piece.setPawnHasJustMoved2(true);
        }
        convertAdvancedPawns();

        whiteTurnNext = !whiteTurnNext;

        return true;
    }

    public boolean executeMove(Piece piece, ChessCoordinate destin) {
        Coordinate origin = getLocation(piece).convertToCoordinate();
        int destRow = destin.convertToCoordinate().row;
        int destCol = destin.convertToCoordinate().col;
        int originRow = origin.row;
        int originCol = origin.col;
        board[originRow][originCol] = null;
        board[destRow][destCol] = piece;

        // if enPassant is being used this turn to move a pawn
        if (pawnMovedActivatesEnPassant != null) {
            if (pawnMovedActivatesEnPassant == piece) {
                // remove piece behind the pawn
                ChessCoordinate pawnToRemove = getLocation(pawnMovedActivatesEnPassant);
                int pawnToRemoveRow = originRow;
                int pawnToRemoveCol = pawnToRemove.convertToCoordinate().col;
                board[pawnToRemoveRow][pawnToRemoveCol] = null;
                System.out.println("Removed " + pawnToRemove + " due to En Passant rule.");
            }
        }
        // if castling is being used this turn to move a king
        boolean destIs2Away = false;
        if ((destRow == originRow) && (Math.abs(destCol - originCol) == 2)) {
            destIs2Away = true;
        }
        int rookOriginRow = originRow;
        int rookOriginCol = 0;
        if (destCol > originCol) {
            rookOriginCol = 7;
        }
        ChessCoordinate rookOrigin = new Coordinate(rookOriginRow, rookOriginCol).convertToChessCoordinate();
        if ((piece.getType() == PieceType.KING) && (destIs2Away)) {
            adjustRookForCastling(piece, rookOrigin);
        }
        return true;
    }

    /**
     * Returns the piece at the given ChessCoordinate.
     *
     * @param loc The ChessCoordinate of the piece.
     * @return The piece at the given location.
     */
    public Piece getPiece(ChessCoordinate loc) {
        int row = loc.convertToCoordinate().row;
        int col = loc.convertToCoordinate().col;
        return board[row][col];
    }

    /**
     * Checks whether a move is valid, WITHOUT taking into account moving into
     * check.
     *
     * @param piece The piece to be moved.
     * @param destin The destination of the piece.
     * @return Whether the move is valid.
     */
    public boolean isMoveValidExcludesCheck(Piece piece, ChessCoordinate destin) {

        // Is destination already occupied by same player?
        if ((getPiece(destin) != null) && (getPiece(destin).isWhite() == piece.isWhite())) {
            return false;
        }

        // Is destination within piecetype's moving ability?        
        if (!canPieceTypeReach(destin, piece)) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a move is valid, taking into account moving into check.
     *
     * @param piece The piece to be moved.
     * @param destin The destination of the piece.
     * @return Whether the move is valid.
     */
    public boolean isMoveValidIncludesCheck(Piece piece, ChessCoordinate destin) {
        if (!isMoveValidExcludesCheck(piece, destin)) {
            return false;
        }
        // Determines whether this move is invalid due to moving into check
        // clone board
        ChessCoordinate origin = getLocation(piece);
        Board clone = new Board(this);
        // execute proposed move
        Piece clonedPiece = clone.getPiece(origin);
        clone.executeMove(clonedPiece, destin);
        // check if king (of current player before clone) is in check 
        if (clone.kingIsInCheck(whiteTurnNext)) {
            return false;
        }
        return true;
    }

    /**
     * Determines whether a piece's movement pattern allows it to move to a
     * given destination.
     *
     * @param piece The piece to move.
     * @param destin The location to move to.
     * @return True if the movement pattern of that piece allows it to move to
     * the destination.
     */
    public boolean canPieceTypeReach(ChessCoordinate destin, Piece piece) {
        ChessCoordinate originCC = getLocation(piece);

        // If origin == dest. then return false
        if (originCC.equals(destin)) {
            return false;
        }
        int origRow = originCC.convertToCoordinate().row;
        int origCol = originCC.convertToCoordinate().col;
        int destRow = destin.convertToCoordinate().row;
        int destCol = destin.convertToCoordinate().col;

        int xDif = Math.abs(destRow - origRow);
        int yDif = Math.abs(destCol - origCol);

        switch (piece.getType()) {

            case PAWN:
                boolean pawnTakeDiag = canPawnTakeDiagonally(destin, piece);
                return (canPawnMoveForward(destin, piece) || (pawnTakeDiag));

            case ROOK:
                return canRookReach(destin, piece);

            case KNIGHT:
                // If xDif abs == 2 and yDif abs == 1 or vice versa then return true
                if ((xDif == 2) && (yDif == 1)) {
                    return true;
                }
                if ((xDif == 1) && (yDif == 2)) {
                    return true;
                }
                return false;

            case BISHOP:
                return canBishopReach(destin, piece);

            case QUEEN:
                boolean canBishopReach = canBishopReach(destin, piece);
                boolean canRookReach = canRookReach(destin, piece);
                return (canBishopReach || canRookReach);

            case KING:
                if ((xDif < 2) && (yDif < 2)) {
                    boolean kingSafeInDest = (!(isSquareUnderThreat(destin, whiteTurnNext)));
                    System.out.println("King can reach? " + kingSafeInDest);
                    return kingSafeInDest;
                }
                if ((xDif == 0) && (yDif == 2) && (!isPieceUnderThreat(originCC))) {
                    boolean result = castling(piece, destin);
                    System.out.println("Castling completed? " + result);
                    return result;
                }
                return false;

            default:
                return false;
        }
    }

    private boolean canRookReach(ChessCoordinate destin, Piece piece) {
        int destinRow = destin.convertToCoordinate().row;
        int destinCol = destin.convertToCoordinate().col;
        int originRow = getLocation(piece).convertToCoordinate().row;
        int originCol = getLocation(piece).convertToCoordinate().col;
        int rowDif = destinRow - originRow;
        int colDif = destinCol - originCol;
        int rowIncrement = 1;
        int colIncrement = 1;
        // If not moving orthogonally, rook cannot reach
        if ((rowDif != 0) && (colDif != 0)) {
            return false;
        }

        // for every space between origin and dest (non-inclusive)
        // if it is not null, return false since rook cannot jump over pieces
        if (rowDif == 0) {
            // MOVING HORIZONTALLY
            if (colDif < 0) {
                colIncrement = -1;
            }
            int colLoc = originCol + colIncrement;
            while (colLoc != destinCol) {
                ChessCoordinate cc = new Coordinate(destinRow, colLoc).convertToChessCoordinate();
                if (board[destinRow][colLoc] != null) {
                    return false;
                }
                colLoc += colIncrement;
            }

        } else {
            //colDif == 0, so MOVING VERTICALLY
            if (rowDif < 0) {
                rowIncrement = -1;
            }
            int rowLoc = originRow + rowIncrement;
            while (rowLoc != destinRow) {
                ChessCoordinate cc = new Coordinate(rowLoc, destinCol).convertToChessCoordinate();
                if (board[rowLoc][destinCol] != null) {
                    return false;
                }
                rowLoc += rowIncrement;
            }
        }
        return true;
    }

    private boolean canBishopReach(ChessCoordinate destin, Piece piece) {
        int destinRow = destin.convertToCoordinate().row;
        int destinCol = destin.convertToCoordinate().col;
        int originRow = getLocation(piece).convertToCoordinate().row;
        int originCol = getLocation(piece).convertToCoordinate().col;
        int rowDif = destinRow - originRow;
        int colDif = destinCol - originCol;
        // If abs.rowDif == abs.colDif then it is diagonal from bishops current location
        if (Math.abs(rowDif) != Math.abs(colDif)) {
            return false;
        }
        // Then check that the path is clear
        int rowIncrement = 1;
        int colIncrement = 1;
        if (rowDif < 0) {
            rowIncrement = -1;
        }
        if (colDif < 0) {
            colIncrement = -1;
        }

        // for every square from origin to dest (non-inclusive)
        // is there is a piece there, return false
        int rowLoc = originRow + rowIncrement;
        int colLoc = originCol + colIncrement;
        while ((rowLoc != destinRow) && colLoc != destinCol) {
            if (board[rowLoc][colLoc] != null) {
                ChessCoordinate cc = new Coordinate(rowLoc, colLoc).convertToChessCoordinate();
                return false;
            }
            rowLoc += rowIncrement;
            colLoc += colIncrement;
        }
        return true;
    }

    private boolean canPawnMoveForward(ChessCoordinate destin, Piece piece) {
        int destRow = destin.convertToCoordinate().row;
        int destCol = destin.convertToCoordinate().col;
        int originRow = getLocation(piece).convertToCoordinate().row;
        int originCol = getLocation(piece).convertToCoordinate().col;
        // If dest. is empty and one ahead of origin, or two of gamestart pos,
        // and has no piece in it then return true
        boolean destisEmpty = (board[destRow][destCol] == null);
        boolean pawnOneAhead;
        if (piece.isWhite()) {
            pawnOneAhead = ((originCol == destCol) && (originRow - destRow == 1));
        } else {
            pawnOneAhead = ((originCol == destCol) && (destRow - originRow == 1));
        }
        boolean pawnTwoAheadOfStart;
        int pathRow;
        boolean isAtStartRow;
        if (piece.isWhite()) {
            isAtStartRow = getLocation(piece).row == 2;
            pawnTwoAheadOfStart = ((originCol == destCol) && (destRow == 4) && (isAtStartRow));
            pathRow = 5;
        } else {
            isAtStartRow = getLocation(piece).row == 7;
            pawnTwoAheadOfStart = ((originCol == destCol) && (destRow == 3) && (isAtStartRow));
            pathRow = 2;
        }

        if (destisEmpty && (pawnOneAhead | (pawnTwoAheadOfStart && board[pathRow][destCol] == null))) {
            return true;
        }
        return false;
    }

    /**
     * Determines whether a given pawn can move diagonally to a given location
     * and take a piece there.
     *
     * @param destin Location to move to.
     * @param piece Piece to move.
     * @return True if move is possible.
     */
    private boolean canPawnTakeDiagonally(ChessCoordinate destin, Piece piece) {
        ChessCoordinate origin = getLocation(piece);
        int destinRow = destin.convertToCoordinate().row;
        int destinCol = destin.convertToCoordinate().col;
        int originRow = origin.convertToCoordinate().row;
        int originCol = origin.convertToCoordinate().col;
        // If dest. has enemy piece and is one diagonal away, then return true,
        // else return false.        
        boolean destIsEmpty = (board[destinRow][destinCol] == null);
        boolean canTakeNormal = ((!destIsEmpty) && (board[destinRow][destinCol].isWhite() != piece.isWhite()));
        // Preparing to determine if EN PASSANT is possible
        int pawnToTake_RowNum = originRow;

        // If can take normally or EN PASSANT, and dest. is diagonally one forward, return true
        ChessCoordinate cc = new Coordinate(pawnToTake_RowNum, destinCol).convertToChessCoordinate();

        // enPassantPawn is the piece that could potentially be taken as a result of this rule.
        Piece enPassantPawn = null;
        if ((pawnToTake_RowNum == 4) || (pawnToTake_RowNum == 3)) {
            enPassantPawn = board[pawnToTake_RowNum][destinCol];
        }

        boolean enPassantCondition = false;
        if (enPassantPawn != null) {
            enPassantCondition = enPassantPawn.isPawnHasJustMoved2();
        }
        boolean canTakeEnPassant = (destIsEmpty && enPassantCondition);
        if (canTakeEnPassant) {
            pawnMovedActivatesEnPassant = piece;
        }

        // TODO enPassantThisTurn cannot be set here because
        // it gets run by code looking for checks and threats 
        if (canTakeNormal | canTakeEnPassant) {
            if (piece.isWhite()) {
                if ((originRow - destinRow == 1) && (Math.abs(originCol - destinCol) == 1)) {
                    return true;
                }
            } else {
                if ((destinRow - originRow == 1) && (Math.abs(originCol - destinCol) == 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether a specific move is a valid castling move, then executes
     * it.
     *
     * @param king The king to be castled.
     * @param kingDestin The location the king will move to due to castling.
     * @return Whether the castling can be completed.
     */
    private boolean castling(Piece king, ChessCoordinate kingDestin) {
        System.out.println("castling method");
        int rookOriginRow = kingDestin.row;
        char rookOriginCol;
        boolean hasRookMoved;
        boolean hasKingMoved = hasBlacKingMoved;
        boolean white = king.isWhite();
        if (white) {
            hasKingMoved = hasWhiteKingMoved;
        }
        switch (kingDestin.col) {

            case 'C':
                rookOriginCol = 'A';
                if (white) {
                    hasRookMoved = hasWhiteRook0Moved;
                } else {
                    hasRookMoved = hasBlackRook0Moved;
                }
                break;

            case 'G':
                rookOriginCol = 'H';
                if (white) {
                    hasRookMoved = hasWhiteRook7Moved;
                } else {
                    hasRookMoved = hasBlackRook7Moved;
                }
                break;
            default:
                System.err.println("kingDestin.col = " + kingDestin.col + " is out of bounds.");
                return false;
        }
        ChessCoordinate rookOrigin = new ChessCoordinate(rookOriginCol, rookOriginRow);
        ChessCoordinate kingOrigin = getLocation(king);
        int kingOriginCol = kingOrigin.convertToCoordinate().col;
        int kingOriginRow = kingOrigin.convertToCoordinate().row;

        // Check the the king and appropriate rook have each never been moved
        // Appropriate rook has been determined by colour and whether it is in column C or G
        if (hasKingMoved || hasRookMoved) {
            return false;
        }
        // Always iterating from left to right
        // Either starting from left rook towards king...
        int start = 1; // = knight starting square
        int end = kingOriginCol; // up to but not including this square, the king starting square
        if (rookOriginCol == 'H') {
            // ...or from king towards right rook
            start = kingOriginCol + 1; // = bishop starting square
            end = 7; // up to but not including this square, the rook starting square
        }
        // Check that there are no pieces in between the king and rook
        for (int col = start; col < end; col++) {
            if (board[kingOriginRow][col] != null) {
                return false;
            }
        }

        // For each of the three spaces (current, path, destination of king)
        int colIncr = 1;
        if (rookOrigin.convertToCoordinate().col < kingOriginCol) {
            colIncr = -1;
        }
        for (int col = kingOriginCol; col != kingOriginCol + (3 * colIncr); col += colIncr) {
            ChessCoordinate toCheckforCheck = new Coordinate(kingOriginRow, col).convertToChessCoordinate();
            // make sure it is not in check
            if (isSquareUnderThreat(toCheckforCheck, king.isWhite())) {
                return false;
            }
        }
        return true;
    }

    private boolean adjustRookForCastling(Piece king, ChessCoordinate rookOrigin) {
        System.out.println("activateCastling method");
        ChessCoordinate kingOrigin = getLocation(king);
        int kingCol = kingOrigin.convertToCoordinate().col;
        int rookRow = rookOrigin.convertToCoordinate().row;
        int rookCol = rookOrigin.convertToCoordinate().col;
        // rook
        Piece rookToMove = board[rookRow][rookCol];
        board[rookRow][rookCol] = null;
        if (rookCol < kingCol) {
            // Place new rook on left side
            board[rookRow][rookCol + 3] = rookToMove;
            // Record that king and castle have moved
            if (king.isWhite()) {
                hasWhiteKingMoved = true;
                hasWhiteRook0Moved = true;
            } else {
                hasBlacKingMoved = true;
                hasBlackRook0Moved = true;
            }
        } else {
            // Place new rook on right side            
            board[rookRow][rookCol - 2] = rookToMove;
            // Record that king and castle have moved
            if (king.isWhite()) {
                hasWhiteKingMoved = true;
                hasWhiteRook7Moved = true;
            } else {
                hasBlacKingMoved = true;
                hasBlackRook7Moved = true;
            }
        }
        return true;

    }

    /**
     * Finds out whether a given location is under threat (in check). Uses
     * isSquareUnderThreat method
     *
     * @param destin The ChessCoordinate that the piece is located in.
     * @return True if the piece is under threat (in check).
     */
    public boolean isPieceUnderThreat(ChessCoordinate destin) {
        int row = destin.convertToCoordinate().row;
        int col = destin.convertToCoordinate().col;
        boolean pieceOwnerIsWhite = board[row][col].isWhite();
        return isSquareUnderThreat(destin, pieceOwnerIsWhite);
    }

    /**
     * Determines whether a given chess square is under threat or not.
     *
     * @param destin The location to investigate.
     * @param defenderIsWhite True if the defending player is white.
     * @return True if the defending location is under threat from offensive
     * player.
     */
    public boolean isSquareUnderThreat(ChessCoordinate destin, boolean defenderIsWhite) {
        // For each piece on the board
        for (Piece[] pieces : board) {
            for (Piece p : pieces) {
                if (p != null) {
                    // If it is not the same colour as the player
                    if (p.isWhite() != defenderIsWhite) {
                        // See if destination is under attack from it using isMoveValid method
                        if (isMoveValidExcludesCheck(p, destin)) {
                            System.out.println("Under threat from " + p + " at " + getLocation(p));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds the location of the piece causing threat to destination, given the
     * colour of the defending piece.
     *
     * @param destin The destination square that is under threat.
     * @param defenderIsWhite The colour of the defending piece.
     * @return The location of the offensive piece threatening the destin
     * square.
     */
    public ChessCoordinate squareUnderThreatFrom(ChessCoordinate destin, boolean defenderIsWhite) {
        // For each piece on the board
        for (Piece[] pieces : board) {
            for (Piece p : pieces) {
                if (p != null) {
                    // If it is not the same colour as the player
                    if (p.isWhite() != defenderIsWhite) {
                        // See if destination is under attack from it using isMoveValid method
                        if (isMoveValidExcludesCheck(p, destin)) {
                            System.out.println("Under threat from " + p + " at " + getLocation(p));
                            return getLocation(p);
                        }
                    }
                }
            }
        }
        return null;
    }

    public GameState checkGameState(boolean isWhiteCurrentTurn) {
        // If there are no valid moves for the current player, else return IN_PROGRESS
        for (Piece[] pieces : board) {
            for (Piece p : pieces) {
                // Default state for pieceIsWhite is opposite to isWhiteCurrentTurn
                // to make later IF statement fail by default
                boolean pieceIsWhite = !isWhiteCurrentTurn;
                if (p != null) {
                    pieceIsWhite = p.isWhite();
                    // Now the later IF statement will pass if piece is not null and is their turn now
                }
                if (pieceIsWhite == isWhiteCurrentTurn) {
                    for (int row = 1; row <= 8; row++) {
                        for (char col = 'A'; col <= 'H'; col++) {
                            if (isMoveValidIncludesCheck(p, new ChessCoordinate(col, row))) {
                                return GameState.IN_PROGRESS;
                            }
                        }
                    }
                }
            }
        }
        // If the king is in check then return VICTORY for current player.        
        if (kingIsInCheck(isWhiteCurrentTurn)) {
            if (isWhiteCurrentTurn) {
                return GameState.VICTORY_BLACK;
            } else {
                return GameState.VICTORY_WHITE;
            }
        } else {
            // If the king is not in check then return STALEMATE
            return GameState.STALEMATE;
        }
    }

    public ArrayList<ChessCoordinate> findPieces(PieceType type, boolean isWhite) {
        ArrayList<ChessCoordinate> list = new ArrayList();
        for (int row = 1; row <= 8; row++) {
            for (char col = 'A'; col <= 'H'; col++) {
                Piece p = getPiece(new ChessCoordinate(col, row));
                if (p != null) {
                    if ((p.isWhite() == isWhite) && (p.getType() == type)) {
                        list.add(new ChessCoordinate(col, row));
                    }
                }
            }
        }
        return list;
    }

    public ChessCoordinate findPiece(PieceType type, boolean isWhite) {
        ArrayList<ChessCoordinate> list = findPieces(type, isWhite);
        if (list.isEmpty()) {
            System.err.println("Error finding piece. List size = 0. PieceType " + type + ", isWhite " + isWhite);
        }
        return list.get(0);
        // This will crash if there are no pieces of said type on the board.
    }

    /**
     * Populates the 2D array of possible moves a given piece can make.
     *
     * @param p The piece to calculate all the possible moves of.
     */
    public void calculatePossibleMoves(Piece p) {
        // For every square on the board
        // if piece can move there, add true, else add false
        int total = 0;
        for (int row = 1; row <= 8; row++) {
            for (char col = 'A'; col <= 'H'; col++) {
                ChessCoordinate destin = new ChessCoordinate(col, row);
                int rowNum = destin.convertToCoordinate().row;
                int colNum = destin.convertToCoordinate().col;
                boolean isValid = isMoveValidIncludesCheck(p, destin);
                if (isValid) {
                    total++;
                }
                possibleMoves[rowNum][colNum] = isValid;
            }
        }
        System.out.println(total + " possible moves calculated for " + p + " at " + getLocation(p));
    }

    /**
     * Determines whether the given move is within the 2D array of
     * pre-calculated valid moves for the current piece.
     *
     * @param destin The destination of the given move.
     * @return True if the given move is in the array of valid moves.
     */
    public boolean isInPossibleMoves(ChessCoordinate destin) {
        int row = destin.convertToCoordinate().row;
        int col = destin.convertToCoordinate().col;
        return possibleMoves[row][col];
    }

    /**
     * Determines whether the board is currently in check for a given player.
     *
     * @param defenderIsWhite True if the defending player is white.
     * @return True if the defending player colour is currently in check.
     */
    public boolean kingIsInCheck(boolean defenderIsWhite) {
        ChessCoordinate kingLoc = findPiece(PieceType.KING, defenderIsWhite);
        return isPieceUnderThreat(kingLoc);
    }

    public void convertAdvancedPawns() {
        Piece[] topAndBottomRows = new Piece[16];
        int index = 0;
        for (int col = 0; col < 8; col++) {
            topAndBottomRows[index++] = board[0][col];
            topAndBottomRows[index++] = board[7][col];
        }
        for (Piece p : topAndBottomRows) {
            if (p != null) {
                Coordinate loc = getLocation(p).convertToCoordinate();
                if (p.getType() == PieceType.PAWN) {
                    board[loc.row][loc.col] = new Piece(PieceType.QUEEN, p.isWhite());
                }
            }
        }
    }

}
