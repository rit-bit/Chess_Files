/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rps.chess;

/**
 *
 * @author User
 */
public class ChessCoordinate {
    
    public final int row;
    public final char col;
    
    public ChessCoordinate(char col, int row) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Converts ChessCoordinate (A1) to Coordinate (0,0)
     * @return 
     */
    public Coordinate convertToCoordinate () {
        if ((row < 1) | (row > 8) | (col < 'A') | col > 'H') {
            throw new RuntimeException("ChessCoordinate (" + col + row + ") is out of bounds");
        }
        int r, c;
        r = Math.abs(row - 8);
        c = col - 'A';
        return new Coordinate(r, c);
    }

    @Override
    public String toString() {
        return "" + col + row;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessCoordinate) {
            ChessCoordinate cc = (ChessCoordinate) obj;
            if ((this.col == cc.col) && (this.row == cc.row)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.row;
        hash = 23 * hash + this.col;
        return hash;
    }
    
    
        
}
