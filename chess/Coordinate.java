package rps.chess;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class Coordinate {
    
    public final int row, col;
    
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Converts Coordinate (0,0) to ChessCoordinate (A1)
     * @param row
     * @param col
     * @return 
     */
    public ChessCoordinate convertToChessCoordinate () {
        if ((row < 0) | (col < 0) | (row > 7) | (col > 7)) {
            throw new RuntimeException("ChessCoordinate (row " + row + ", col " + col + ") is out of bounds");
        }
        int r = Math.abs(row - 8);
        char colChar = 'A';
        colChar += col;
        return new ChessCoordinate(colChar, r);
    }

    @Override
    public String toString() {
        return "" + row + ", " + col;
    }
    
    
            
}
