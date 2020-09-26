package rps.chess;


import rps.chess.PieceType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class Piece {

    private PieceType type = PieceType.PAWN;
    private final boolean isWhite;
    private boolean pawnHasJustMoved2 = false;

    /**
     * Normal constructor for new Piece.
     * @param type Enum for PAWN, ROOK, KNIGHT, BISHOP, KING, OR QUEEN.
     * @param isWhite True if piece is white, false for black.
     */
    public Piece(PieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }
    
    /**
     * Copy Constructor to create a new Piece object the same as the original Piece.
     * @param original The Piece to be copied.
     */
    Piece (Piece original) {
        this.isWhite = original.isWhite();
        this.type = original.getType();
        this.pawnHasJustMoved2 = original.isPawnHasJustMoved2();
    }

    @Override
    public String toString() {
        String output = "Black ";
        if (isWhite) {
            output = "White ";
        }
        output += type.toString();
        return output;
    }        

    /**
     * @return the type
     */
    public PieceType getType() {
        return type;
    }

    /**
     * @return the isWhite
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * @return the pawnHasJustMoved2
     */
    public boolean isPawnHasJustMoved2() {
        return pawnHasJustMoved2;
    }

    /**
     * @param pawnHasJustMoved2 the pawnHasJustMoved2 to set
     */
    public void setPawnHasJustMoved2(boolean pawnHasJustMoved2) {
        this.pawnHasJustMoved2 = pawnHasJustMoved2;
    }
    
    
}
