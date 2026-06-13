package Checkers_Tactics.gui;

import Checkers_Tactics.Environment.Board;

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

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int currentPlayer = 1;
    private JLabel turnLabel;
    private JLabel timeLabel;
    private Timer timer;
    private int elapsedSeconds = 0;

    public CheckersGUI() {
        setTitle("Checkers Tactics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            boardImage = ImageIO.read(new File("src/images/chessboard.png"));
            whitePieceImage = ImageIO.read(new File("src/images/whitePiece.png"));
            blackPieceImage = ImageIO.read(new File("src/images/blackPiece.png"));
        } catch (IOException e) {
            System.out.println("Błąd podczas ładowania grafik: " + e.getMessage());
        }

        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 49, 43));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        turnLabel = new JLabel("Ruch: Białe", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        turnLabel.setForeground(Color.WHITE);

        timeLabel = new JLabel("00:00", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timeLabel.setForeground(Color.WHITE);

        topPanel.add(turnLabel, BorderLayout.CENTER);
        topPanel.add(timeLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new BoardPanel(), BorderLayout.CENTER);

        startTimer();
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
        timer.start();
    }

    private void updateTurnLabel() {
        if (currentPlayer == 1) {
            turnLabel.setText("Ruch: Białe");
        } else {
            turnLabel.setText("Ruch: Czarne");
        }
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
                        if (Board.boardState[row][col] == currentPlayer) {
                            selectedRow = row;
                            selectedCol = col;
                        }
                    } else {
                        if (row == selectedRow && col == selectedCol) {
                            selectedRow = -1;
                            selectedCol = -1;
                        } else {
                            if (Board.MoveCheckerOnce(selectedRow, selectedCol, row, col)) {
                                selectedRow = -1;
                                selectedCol = -1;

                                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                                updateTurnLabel();
                            } else {
                                if (Board.boardState[row][col] == currentPlayer) {
                                    selectedRow = row;
                                    selectedCol = col;
                                } else {
                                    selectedRow = -1;
                                    selectedCol = -1;
                                }
                            }
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

                g2d.setColor(new Color(186, 186, 186, 180));
                double moveHighlightRatio = 0.2;
                double moveHighlightWidth = tileWidth * moveHighlightRatio;
                double moveHighlightHeight = tileHeight * moveHighlightRatio;

                for (int r = 0; r < NUM_OF_TILES; r++) {
                    for (int c = 0; c < NUM_OF_TILES; c++) {
                        if (Board.CanCheckerMoveOnce(selectedRow, selectedCol, r, c) > 0) {
                            double targetCenterX = (c * tileWidth) + (tileWidth / 2.0);
                            double targetCenterY = (r * tileHeight) + (tileHeight / 2.0);

                            int thX = (int) Math.round(targetCenterX - (moveHighlightWidth / 2.0));
                            int thY = (int) Math.round(targetCenterY - (moveHighlightHeight / 2.0));

                            g2d.fillOval(thX, thY, (int) Math.round(moveHighlightWidth), (int) Math.round(moveHighlightHeight));
                        }
                    }
                }
            }

            for (int row = 0; row < NUM_OF_TILES; row++) {
                for (int col = 0; col < NUM_OF_TILES; col++) {
                    int pieceType = Board.boardState[row][col];

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