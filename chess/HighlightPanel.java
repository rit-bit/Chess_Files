/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rps.chess;

import java.awt.Color;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class HighlightPanel extends JPanel {
    
    public final Color defaultColour;

    public HighlightPanel(Color defaultColour, LayoutManager layout) {
        super(layout);
        this.defaultColour = defaultColour;
    }   
    
    public void revertToDefaultColour () {
        setBackground(defaultColour);
    }
    
}
