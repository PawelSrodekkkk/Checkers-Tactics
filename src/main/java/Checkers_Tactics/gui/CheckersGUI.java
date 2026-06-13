package Checkers_Tactics.gui;

import Checkers_Tactics.Environment.Board;

import javax.swing.*;
import java.awt.*;

public class CheckersGUI extends JFrame {
    private int currentPlayer = 1;

    private final JLabel whiteTurnLabel;
    private final JLabel blackTurnLabel;
    private final JLabel whiteTimeLabel;
    private final JLabel blackTimeLabel;

    private Timer timer;
    private int whiteSeconds = 0;
    private int blackSeconds = 0;

    public CheckersGUI() {
        setTitle("Checkers Tactics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(51, 49, 43));

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Opcje");
        JMenuItem returnItem = new JMenuItem("Wróć do Menu Głównego");
        returnItem.addActionListener(e -> returnToMenu());
        gameMenu.add(returnItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        JPanel topPanel = createPlayerPanel("Czarne");
        blackTurnLabel = (JLabel) topPanel.getComponent(0);
        blackTimeLabel = (JLabel) topPanel.getComponent(1);

        JPanel bottomPanel = createPlayerPanel("Białe (Twój ruch)");
        whiteTurnLabel = (JLabel) bottomPanel.getComponent(0);
        whiteTimeLabel = (JLabel) bottomPanel.getComponent(1);

        add(topPanel, BorderLayout.NORTH);

        add(new BoardPanel(this), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        startTimer();
    }

    private JPanel createPlayerPanel(String defaultText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(51, 49, 43));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel turnLabel = new JLabel(defaultText, SwingConstants.LEFT);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        turnLabel.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel("00:00", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timeLabel.setForeground(Color.WHITE);

        panel.add(turnLabel, BorderLayout.WEST);
        panel.add(timeLabel, BorderLayout.EAST);
        return panel;
    }

    private void startTimer() {
         timer = new Timer(1000, e -> {
            if (currentPlayer == 1) {
                whiteSeconds++;
                whiteTimeLabel.setText(String.format("%02d:%02d", whiteSeconds / 60, whiteSeconds % 60));
            } else {
                blackSeconds++;
                blackTimeLabel.setText(String.format("%02d:%02d", blackSeconds / 60, blackSeconds % 60));
            }
        });
        timer.start();
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        if (currentPlayer == 1) {
            whiteTurnLabel.setText("Białe (Twój ruch)");
            blackTurnLabel.setText("Czarne");
        } else {
            whiteTurnLabel.setText("Białe");
            blackTurnLabel.setText("Czarne (Twój ruch)");
        }
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void checkWinCondition() {
        if (!Board.HasAnyValidMoves(currentPlayer)) {
            if (timer != null) timer.stop();

            Timer delayTimer = new Timer(300, e -> {
                int winner = (currentPlayer == 1) ? 2 : 1;
                String winnerName = (winner == 1) ? "Białe" : "Czarne";

                Object[] options = {"Wróć do Menu", "Wyjdź z gry"};
                int choice = JOptionPane.showOptionDialog(this,
                        "Wygrywają " + winnerName + "!\nGratulacje!",
                        "Koniec Gry!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (choice == 0) {
                    returnToMenu();
                } else {
                    System.exit(0);
                }
            });

            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    public void returnToMenu() {
        if (timer != null) timer.stop();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}