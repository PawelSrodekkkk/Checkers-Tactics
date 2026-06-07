package Checkers_Tactics.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CheckersGUI extends JFrame {
    public static final int NUM_OF_TILES = 8;
    private BufferedImage boardImage;
    private BufferedImage whitePieceImage;
    private BufferedImage blackPieceImage;

    // 0 = puste pole, 1 = biały pionek, 2 = czarny pionek
    private final int[][] boardState = new int[NUM_OF_TILES][NUM_OF_TILES];

    public CheckersGUI() {

        setTitle("Warcaby - GUI");
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
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (boardImage != null) {
                g.drawImage(boardImage, 0, 0, panelWidth, panelHeight, this);
            }

            // Obliczanie wymiarów pojedynczego pola
            int tileWidth = panelWidth / NUM_OF_TILES;
            int tileHeight = panelHeight / NUM_OF_TILES;

            int paddingX = (int) (tileWidth * 0.01);
            int paddingY = (int) (tileHeight * 0.01);
            int pieceWidth = tileWidth - (2 * paddingX);
            int pieceHeight = tileHeight - (2 * paddingY);

            for (int row = 0; row < NUM_OF_TILES; row++) {
                for (int col = 0; col < NUM_OF_TILES; col++) {
                    int pieceType = boardState[row][col];

                    if (pieceType != 0) {

                        int x = (col * tileWidth) + paddingX;
                        int y = (row * tileHeight) + paddingY;

                        if (pieceType == 1 && whitePieceImage != null) {
                            g.drawImage(whitePieceImage, x, y, pieceWidth, pieceHeight, this);
                        } else if (pieceType == 2 && blackPieceImage != null) {
                            g.drawImage(blackPieceImage, x, y, pieceWidth, pieceHeight, this);
                        }
                    }
                }
            }
        }
    }
}