package Checkers_Tactics.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckersGUI extends JFrame {
    public static final int NUM_OF_TILES = 8;
    private BufferedImage boardImage;
    private BufferedImage whitePieceImage;
    private BufferedImage blackPieceImage;

    // 0 = puste pole, 1 = biały pionek, 2 = czarny pionek
    private final int[][] boardState = new int[NUM_OF_TILES][NUM_OF_TILES];

    private int selectedRow = -1;
    private int selectedCol = -1;

    public CheckersGUI() {

        setTitle("Checkers Tactics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);

        try {
            boardImage = ImageIO.read(new File("src/images/chessboard.png"));

            whitePieceImage = ImageIO.read(new File("src/images/whitePiece.png"));
            blackPieceImage = ImageIO.read(new File("src/images/blackPiece.png"));
        } catch (IOException e) {
            System.out.println("Błąd podczas ładowania grafik: " + e.getMessage());
        }
        setupTestBoard(); // na razie dane są wpisane z palca

        add(new BoardPanel());
    }

    private void setupTestBoard() {
        // Czarne:
        boardState[0][0] = 2;
        boardState[0][2] = 2;
        boardState[0][4] = 2;
        boardState[0][6] = 2;

        boardState[1][1] = 2;
        boardState[1][3] = 2;
        boardState[1][5] = 2;
        boardState[1][7] = 2;

        boardState[2][0] = 2;
        boardState[2][2] = 2;
        boardState[2][4] = 2;
        boardState[2][6] = 2;

        // Białe:
        boardState[5][1] = 1;
        boardState[5][3] = 1;
        boardState[5][5] = 1;
        boardState[5][7] = 1;

        boardState[6][0] = 1;
        boardState[6][2] = 1;
        boardState[6][4] = 1;
        boardState[6][6] = 1;

        boardState[7][1] = 1;
        boardState[7][3] = 1;
        boardState[7][5] = 1;
        boardState[7][7] = 1;
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int tileWidth = getWidth() / NUM_OF_TILES;
                    int tileHeight = getHeight() / NUM_OF_TILES;

                    int col = e.getX() / tileWidth;
                    int row = e.getY() / tileHeight;

                    if (col < 0 || col > NUM_OF_TILES - 1 || row < 0 || row > NUM_OF_TILES - 1) return;

                    if (selectedRow == -1) {
                        if (boardState[row][col] != 0) {
                            selectedRow = row;
                            selectedCol = col;
                        }
                    } else {
                        if (row == selectedRow && col == selectedCol) {
                            selectedRow = -1;
                            selectedCol = -1;
                        } else {
                            boardState[row][col] = boardState[selectedRow][selectedCol];
                            boardState[selectedRow][selectedCol] = 0;

                            selectedRow = -1;
                            selectedCol = -1;
                        }
                    }
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (boardImage != null) {
                g2d.drawImage(boardImage, 0, 0, panelWidth, panelHeight, this);
            }

            double tileWidth = (double) panelWidth / NUM_OF_TILES;
            double tileHeight = (double) panelHeight / NUM_OF_TILES;

            double pieceToTileRatio = 0.9;
            double pieceWidth = tileWidth * pieceToTileRatio;
            double pieceHeight = tileHeight * pieceToTileRatio;

            if (selectedRow != -1 && selectedCol != -1) {
                double tileCenterX = (selectedCol * tileWidth) + (tileWidth / 2.0);
                double tileCenterY = (selectedRow * tileHeight) + (tileHeight / 2.0);

                double highlightToTileRatio = 0.95;
                double highlightWidth = tileWidth * highlightToTileRatio;
                double highlightHeight = tileHeight * highlightToTileRatio;

                int hX = (int) Math.round(tileCenterX - (highlightWidth / 2.0));
                int hY = (int) Math.round(tileCenterY - (highlightHeight / 2.0));

                g2d.setColor(new Color(0, 255, 0, 80));
                g2d.fillOval(hX, hY, (int) Math.round(highlightWidth), (int) Math.round(highlightHeight));
            }

            for (int row = 0; row < NUM_OF_TILES; row++) {
                for (int col = 0; col < NUM_OF_TILES; col++) {
                    int pieceType = boardState[row][col];

                    if (pieceType != 0) {
                        double tileCenterX = (col * tileWidth) + (tileWidth / 2.0);
                        double tileCenterY = (row * tileHeight) + (tileHeight / 2.0);

                        int x = (int) Math.round(tileCenterX - (pieceWidth / 2.0));
                        int y = (int) Math.round(tileCenterY - (pieceHeight / 2.0));

                        if (pieceType == 1 && whitePieceImage != null) {
                            g2d.drawImage(whitePieceImage, x, y, (int) Math.round(pieceWidth), (int) Math.round(pieceHeight), this);
                        } else if (pieceType == 2 && blackPieceImage != null) {
                            g2d.drawImage(blackPieceImage, x, y, (int) Math.round(pieceWidth), (int) Math.round(pieceHeight), this);
                        }
                    }
                }
            }
        }
    }
}