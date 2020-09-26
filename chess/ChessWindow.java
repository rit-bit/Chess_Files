package rps.chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author User
 */
public class ChessWindow extends JFrame {

    private final Color Rd = Color.RED;
    private final Color Ylw = Color.YELLOW;
    private final Color Grn = Color.GREEN;
    private final Color Blk = Color.BLACK;
    private final JLabel[][] chessLabels = new JLabel[8][8];
    private final HighlightPanel[][] highlights = new HighlightPanel[8][8];
    private static final int GAP_SIZE = 5;
    private static final int WINDOW_SIZE = 600;
    private static final int SQUARE_SIZE = 50;
    private static final Dimension FILL_SIZE = new Dimension(2, 2);
    private Board board;
    private ChessCoordinate selectedSquare = new ChessCoordinate('A', 1);
    private boolean isSquareSelected = false;
    private final JLabel lblTurn;
    private static JMenuItem highlightsEnabledOption;
    private static JMenuItem boardLabelsEnabledOption;
    private JPanel boardLetterLabels, boardNumberLabels;

    public ChessWindow() throws HeadlessException {
        board = new Board();
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setSize(WINDOW_SIZE, WINDOW_SIZE);
        super.setMinimumSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
        super.setLayout(new BorderLayout(GAP_SIZE, GAP_SIZE));
        super.setTitle("Chess by Richard");

        // Menu bar creation
        JMenuBar menuBar = new JMenuBar();
        super.setJMenuBar(menuBar);
        JMenu file = new JMenu("Game");
        menuBar.add(file);
        JMenu options = new JMenu("Options");
        menuBar.add(options);
        JMenuItem newGame = new JMenuItem("New game");
        file.add(newGame);
        newGame.setAction(new AbstractAction("New game") {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });
        JMenuItem save = new JMenuItem("Save");
        file.add(save);
        save.setAction(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        JMenuItem load = new JMenuItem("Load");
        file.add(load);
        load.setAction(new AbstractAction("Load") {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });
        highlightsEnabledOption = new JCheckBoxMenuItem(new AbstractAction("Show square highlights") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHighlightsClicked();
            }
        });
        options.add(highlightsEnabledOption);
        highlightsEnabledOption.setSelected(true);

        boardLabelsEnabledOption = new JCheckBoxMenuItem(new AbstractAction("Show board labels") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLabelsClicked();
            }
        });
        options.add(boardLabelsEnabledOption);
        boardLabelsEnabledOption.setSelected(true);

        // AspectRatio holds the mainChessPanel and retains square shape
        JPanel aspectRatio = new JPanel(new GridBagLayout());
        // MainChessPanel contains the chess board including axis labels
        JPanel mainChessPanel = new JPanel(new BorderLayout());
        // axisLabelLetters contains A-H to label the edge of the board        
        boardLetterLabels = new JPanel();
        boardLetterLabels.setLayout(new BoxLayout(boardLetterLabels, BoxLayout.LINE_AXIS));
        for (char letter = 'A'; letter <= 'H'; letter++) {
            JPanel pnl = new JPanel();
            String letterStr = "" + letter;
            JLabel lbl = new JLabel(letterStr);
            boardLetterLabels.add(pnl);
            pnl.add(lbl);
        }
        // axisLabelNumbers contains 1-8 to label the edge of the board
        boardNumberLabels = new JPanel();
        boardNumberLabels.setLayout(new BoxLayout(boardNumberLabels, BoxLayout.PAGE_AXIS));
        for (int number = 8; number >= 1; number--) {
            JPanel pnl = new JPanel();
            String numberStr = "" + number;
            JLabel lbl = new JLabel(numberStr);
            boardNumberLabels.add(pnl);
            pnl.add(lbl);
        }
        // chessSquares contains the actual 64 coloured squares of the chess board:
        JPanel chessSquares = new JPanel(new GridLayout(8, 8));
        aspectRatio.add(mainChessPanel);
        mainChessPanel.add(chessSquares, BorderLayout.CENTER);
        mainChessPanel.add(boardLetterLabels, BorderLayout.PAGE_START);
        mainChessPanel.add(boardNumberLabels, BorderLayout.LINE_START);
        lblTurn = new JLabel("White turn");
        mainChessPanel.add(lblTurn, BorderLayout.PAGE_END);
        aspectRatio.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePreview(mainChessPanel, aspectRatio);
            }
        });
        super.getContentPane().add(aspectRatio, BorderLayout.CENTER);

        // Create the 64 panels for squares of the chess board
        int panelIndex = 0;
        for (int row = 8; row >= 1; row--) {
            for (char col = 'A'; col <= 'H'; col++) {
                Coordinate locCoord = new ChessCoordinate(col, row).convertToCoordinate();
                Color colour = ((((locCoord.col % 2) + row) % 2 == 1) ? Color.WHITE : Color.PINK);
                HighlightPanel highlight = new HighlightPanel(colour, new BorderLayout());
                // highlight sits behind the pnlChessSq, & is slightly larger
                highlight.setSize(SQUARE_SIZE, SQUARE_SIZE);
                JPanel pnlChessSq = new JPanel();
                pnlChessSq.setSize(SQUARE_SIZE, SQUARE_SIZE / 2);
                pnlChessSq.setName("pnlSquare" + panelIndex++);
                highlights[locCoord.row][locCoord.col] = highlight;
                pnlChessSq.setBackground(colour);
                highlight.setBackground(colour);
                JLabel lbl = new JLabel();
                pnlChessSq.add(lbl);
                chessLabels[locCoord.row][locCoord.col] = lbl;
                highlight.add(pnlChessSq, BorderLayout.CENTER);

                highlight.add(new Box.Filler(FILL_SIZE, FILL_SIZE, FILL_SIZE), BorderLayout.PAGE_START);
                highlight.add(new Box.Filler(FILL_SIZE, FILL_SIZE, FILL_SIZE), BorderLayout.PAGE_END);
                highlight.add(new Box.Filler(FILL_SIZE, FILL_SIZE, FILL_SIZE), BorderLayout.LINE_START);
                highlight.add(new Box.Filler(FILL_SIZE, FILL_SIZE, FILL_SIZE), BorderLayout.LINE_END);

                chessSquares.add(highlight, -1);
                final int xx = locCoord.row;
                final int yy = locCoord.col;
                pnlChessSq.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        squareClicked(new Coordinate(xx, yy).convertToChessCoordinate());
                    }
                });
            }
        }
        drawPieces();
    }

    /**
     * Used to maintain square aspect ratio for the chess board
     *
     * @param mainChessPanel
     * @param aspectRatio
     */
    private static void resizePreview(JPanel mainChessPanel, JPanel aspectRatio) {
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        int size = Math.min(w, h);
        mainChessPanel.setPreferredSize(new Dimension(size, size));
        aspectRatio.revalidate();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChessWindow window = new ChessWindow();
                window.setVisible(true);
            }
        });
    }

    public void squareClicked(ChessCoordinate sqClicked) {
        System.out.println("Clicked square " + sqClicked.col + sqClicked.row + ",");
        Piece clickedPiece = board.getPiece(sqClicked);
        Piece pieceToMove = board.getPiece(selectedSquare);
        if (clickedPiece != null) {
            String piecename = clickedPiece.toString();
            System.out.println("which contains " + piecename);
        } else {
            System.out.println("which is empty.");
        }

        // If no piece is selected
        if (!isSquareSelected) {
            // If there is no piece on clicked square, deselect piece
            if (clickedPiece == null) {
                deselectSquare();
                // If it is their turn, select piece, otherwise deselect piece
            } else {
                if (clickedPiece.isWhite() == board.isWhiteTurnNext()) {
                    setSelectedSquare(sqClicked);
                } else {
                    deselectSquare();
                }
            }

            // If piece has been selected
        } else {
            boolean clckSqSameSelSq = sqClicked.equals(getSelectedSquare());
            boolean sqOccupiedBySame = false;
            if (clickedPiece != null) {
                if (clickedPiece.isWhite() == board.isWhiteTurnNext()) {
                    sqOccupiedBySame = true;
                }
            }
            // If clicked square is not the selected square, and square is not occupied by same player
            // then move piece
            if (!clckSqSameSelSq) {
                if (!sqOccupiedBySame) {
                    boolean movedSuccess = board.movePiece(pieceToMove, sqClicked);
                    deselectSquare();
                    drawPieces();
                    redrawInCheckHighlights();

                    String msg;
                    String ttl = "Checkmate!";;
                    int msgType = 0;
                    Icon icn = null;
                    int opType = 0;
                    Object[] options = {"New Game", "Exit"};
                    int userChoice = -3;
                    switch (board.checkGameState(board.isWhiteTurnNext())) {
                        case STALEMATE:
                            msg = "No more moves are possible.";
                            ttl = "Stalemate";
                            userChoice = JOptionPane.showOptionDialog(this, msg, ttl, opType, msgType, icn, options, null);
                            break;

                        case VICTORY_WHITE:
                            msg = "White pieces have won!";
                            msgType = JOptionPane.OK_OPTION;
                            userChoice = JOptionPane.showOptionDialog(this, msg, ttl, opType, msgType, icn, options, null);
                            break;

                        case VICTORY_BLACK:
                            msg = "Black pieces have won!";
                            msgType = JOptionPane.OK_OPTION;
                            userChoice = JOptionPane.showOptionDialog(this, msg, ttl, opType, msgType, icn, options, null);
                            break;

                        case IN_PROGRESS:

                            break;
                    }
                    if (userChoice == 1) {
                        this.dispose();
                    } else if (userChoice == 0) {
                        newGame();
                        return;
                    }

                    if (board.isWhiteTurnNext()) {
                        lblTurn.setText("White turn");
                    } else {
                        lblTurn.setText("Black turn");
                    }

                    // If clicked square is not the selected square, and square IS occupied by same player
                    // then select new square
                } else if (sqOccupiedBySame) {
                    setSelectedSquare(sqClicked);
                }
            } else {
                // If clicked square is selected already, deselect
                deselectSquare();
            }
        }
    }

    public void drawPieces() {
        // For every square on the board
        for (int row = 1; row <= 8; row++) {
            for (char col = 'A'; col <= 'H'; col++) {
                ChessCoordinate loc = new ChessCoordinate(col, row);
                Piece p = board.getPiece(loc);
                // If there is a piece there, draw it
                if (p != null) {
                    String blackOrWhite, strPiece;
                    if (p.isWhite()) {
                        blackOrWhite = "white_";
                    } else {
                        blackOrWhite = "black_";
                    }
                    strPiece = p.getType().toString() + ".png";
                    drawPiece(loc, blackOrWhite, strPiece);
                } else {
                    // If there is not a piece there, clear the icon
                    clearLabelIcon(loc);
                }
            }
        }
    }

    private void drawPiece(ChessCoordinate loc, String blackOrWhite, String strPiece) {
        try {
            String filePath = "C:/Users/User/Documents/NetBeansProjects/Chess/graphics/png/";
            File file = new File(filePath + blackOrWhite + strPiece);
            Image img = ImageIO.read(file);
            img = img.getScaledInstance(50, 50, Image.SCALE_FAST);
            ImageIcon icon = new ImageIcon(img);
            int row = loc.convertToCoordinate().row;
            int col = loc.convertToCoordinate().col;
            chessLabels[row][col].setIcon(icon);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void clearLabelIcon(ChessCoordinate cc) {
        int row = cc.convertToCoordinate().row;
        int col = cc.convertToCoordinate().col;
        chessLabels[row][col].setIcon(null);
    }

    /**
     * @return the selectedSquare, or null if no square is selected
     */
    public ChessCoordinate getSelectedSquare() {
        if (!isSquareSelected) {
            return null;
        }
        return selectedSquare;
    }

    /**
     * Selects the square that has been clicked, and highlights squares as
     * necessary.
     *
     * @param selectedSquare the coordinate to set as selected
     */
    public void setSelectedSquare(ChessCoordinate selectedSquare) {
        this.selectedSquare = selectedSquare;
        isSquareSelected = true;
        Piece p = board.getPiece(selectedSquare);
        board.calculatePossibleMoves(p);
        removeColourHighlights(Grn);
        removeColourHighlights(Ylw);
        showHighlight(Ylw, selectedSquare);
        for (char col = 'A'; col <= 'H'; col++) {
            for (int row = 1; row <= 8; row++) {
                ChessCoordinate square = new ChessCoordinate(col, row);
                if (board.isInPossibleMoves(square)) {
                    showHighlight(Grn, square);
                }
            }
        }
    }

    public void deselectSquare() {
        isSquareSelected = false;
        removeColourHighlights(Grn);
        removeColourHighlights(Ylw);
    }

    public void showHighlight(Color colour, ChessCoordinate squareCC) {
        if (!highlightsEnabledOption.isSelected()) {
            return;
        }
        int row = squareCC.convertToCoordinate().row;
        int col = squareCC.convertToCoordinate().col;
        highlights[row][col].setBackground(colour);

    }

    /**
     * Removes all highlights of the specified type. If colour is null, removes
     * highlights of all colours
     *
     * @param colour All highlights of this colour will be removed. If null, all
     * colours will be removed.
     */
    public void removeColourHighlights(Color colour) {
        for (HighlightPanel[] hls : highlights) {
            for (HighlightPanel highlight : hls) {
                if ((colour == null) || highlight.getBackground() == colour) {
                    highlight.revertToDefaultColour();
                }
            }
        }
    }

    public void redrawInCheckHighlights() {
        removeColourHighlights(Rd);
        removeColourHighlights(Blk);
        // draw red highlight on king in check
        ChessCoordinate kingWhiteCC = board.findPiece(PieceType.KING, true);
        ChessCoordinate kingBlackCC = board.findPiece(PieceType.KING, false);
        if (board.isPieceUnderThreat(kingWhiteCC)) {
            showHighlight(Rd, kingWhiteCC);
            ChessCoordinate offensivePiece = board.squareUnderThreatFrom(kingWhiteCC, true);
            showHighlight(Blk, offensivePiece);
        } else {
            if (board.isPieceUnderThreat(kingBlackCC)) {
                showHighlight(Rd, kingBlackCC);
                ChessCoordinate offensivePiece = board.squareUnderThreatFrom(kingBlackCC, false);
                showHighlight(Blk, offensivePiece);
            }
        }
    }

    private void newGame() {
        board = new Board();
        isSquareSelected = false;
        removeColourHighlights(null);
        drawPieces();
        lblTurn.setText("White turn");
    }

    public void showHighlightsClicked() {
        if (highlightsEnabledOption.isSelected()) {
            if (isSquareSelected) {
                // set the selectedSquare to what it already was to draw highlights where needed
                setSelectedSquare(getSelectedSquare());
            }
        } else {
            removeColourHighlights(null);
        }
    }

    public void showLabelsClicked() {
        boolean isVisible = boardLabelsEnabledOption.isSelected();
        boardNumberLabels.setVisible(isVisible);
        boardLetterLabels.setVisible(isVisible);
    }
    
    public void saveGame() {
        // TODO Save game
        
    }
    
    public void loadGame() {
        // TODO Load game
    }
    
}
