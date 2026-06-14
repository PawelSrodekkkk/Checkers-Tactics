package Checkers_Tactics.gui;

import Checkers_Tactics.Environment.Board;
import Checkers_Tactics.network.MessageType;
import Checkers_Tactics.network.Move;
import Checkers_Tactics.network.NetworkGameSession;
import Checkers_Tactics.network.NetworkMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class CheckersGUI extends JFrame {
    public static final int MOVE_PENDING = -1;

    private int currentPlayer = 1;

    private final JLabel whiteTurnLabel;
    private final JLabel blackTurnLabel;
    private final JLabel whiteTimeLabel;
    private final JLabel blackTimeLabel;
    private final BoardPanel boardPanel;

    private final NetworkGameSession networkSession;
    private final boolean onlineGame;
    private final int localPlayer;
    private final boolean host;

    private Timer timer;
    private int whiteSeconds = 0;
    private int blackSeconds = 0;
    private boolean waitingForMoveResponse = false;
    private boolean gameOver = false;
    private boolean closing = false;
    private int remoteContinuationRow = -1;
    private int remoteContinuationColumn = -1;

    public CheckersGUI() {
        this(null);
    }

    public CheckersGUI(NetworkGameSession networkSession) {
        this.networkSession = networkSession;
        onlineGame = networkSession != null;
        localPlayer = onlineGame ? networkSession.getLocalPlayer() : 0;
        host = onlineGame && networkSession.getRole() == NetworkGameSession.Role.HOST;

        setTitle(createWindowTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(51, 49, 43));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                closeNetworkSession();
            }
        });

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

        JPanel bottomPanel = createPlayerPanel("Białe");
        whiteTurnLabel = (JLabel) bottomPanel.getComponent(0);
        whiteTimeLabel = (JLabel) bottomPanel.getComponent(1);

        boardPanel = new BoardPanel(this);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateTurnLabels();
        startTimer();
        startNetworkSession();
    }

    private String createWindowTitle() {
        if (!onlineGame) {
            return "Checkers Tactics";
        }
        return localPlayer == 1
                ? "Checkers Tactics - Gra sieciowa (białe)"
                : "Checkers Tactics - Gra sieciowa (czarne)";
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

    private void startNetworkSession() {
        if (!onlineGame) {
            return;
        }

        try {
            networkSession.start(new NetworkGameSession.Listener() {
                @Override
                public void onMessage(NetworkMessage message) {
                    SwingUtilities.invokeLater(() -> handleNetworkMessage(message));
                }

                @Override
                public void onDisconnected(String reason) {
                    SwingUtilities.invokeLater(() -> handleOpponentDisconnected(reason));
                }
            });
        } catch (IOException exception) {
            SwingUtilities.invokeLater(() -> handleOpponentDisconnected(exception.getMessage()));
        }
    }

    public boolean canLocalPlayerInteract() {
        if (gameOver || waitingForMoveResponse) {
            return false;
        }
        return !onlineGame || currentPlayer == localPlayer;
    }

    public int attemptLocalMove(Move move) {
        if (!canLocalPlayerInteract() || !isPlayerPiece(move.getFromRow(), move.getFromColumn(), currentPlayer)) {
            return 0;
        }

        if (onlineGame && !host) {
            int moveResult = Board.CanCheckerMoveOnce(
                    move.getFromRow(),
                    move.getFromColumn(),
                    move.getToRow(),
                    move.getToColumn()
            );
            if (moveResult == 0) {
                return 0;
            }

            waitingForMoveResponse = true;
            try {
                networkSession.send(new NetworkMessage(MessageType.MOVE_REQUEST, move, null));
                return MOVE_PENDING;
            } catch (IOException exception) {
                waitingForMoveResponse = false;
                return 0;
            }
        }

        int moveResult = Board.MoveCheckerOnce(
                move.getFromRow(),
                move.getFromColumn(),
                move.getToRow(),
                move.getToColumn()
        );

        if (moveResult > 0 && onlineGame) {
            try {
                networkSession.send(new NetworkMessage(MessageType.MOVE_APPLIED, move, null));
            } catch (IOException ignored) {
                // The session reports the disconnect through its listener.
            }
        }
        return moveResult;
    }

    public void finishLocalTurn() {
        switchPlayer();
        checkWinCondition();
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        updateTurnLabels();
    }

    private void updateTurnLabels() {
        whiteTurnLabel.setText(playerLabel("Białe", 1));
        blackTurnLabel.setText(playerLabel("Czarne", 2));
    }

    private String playerLabel(String colorName, int player) {
        if (currentPlayer != player) {
            return colorName;
        }
        if (!onlineGame || localPlayer == player) {
            return colorName + " (Twój ruch)";
        }
        return colorName + " (Ruch przeciwnika)";
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    private void handleNetworkMessage(NetworkMessage message) {
        if (closing || gameOver) {
            return;
        }

        switch (message.getType()) {
            case START_GAME -> boardPanel.repaint();
            case MOVE_REQUEST -> {
                if (host) {
                    applyGuestMove(message.getMove());
                }
            }
            case MOVE_APPLIED -> {
                if (!host) {
                    applyHostApprovedMove(message.getMove());
                }
            }
            case MOVE_REJECTED -> {
                if (!host) {
                    waitingForMoveResponse = false;
                    boardPanel.rejectPendingMove();
                    JOptionPane.showMessageDialog(
                            this,
                            message.getText() == null ? "Host odrzucił ruch." : message.getText(),
                            "Ruch odrzucony",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
            case GAME_OVER -> {
                int winner = parseWinner(message.getText());
                showGameOver(winner);
            }
            case OPPONENT_DISCONNECTED -> handleOpponentDisconnected(message.getText());
        }
    }

    private void applyGuestMove(Move move) {
        if (move == null
                || currentPlayer != 2
                || !isPlayerPiece(move.getFromRow(), move.getFromColumn(), 2)
                || !matchesRequiredContinuation(move)) {
            rejectGuestMove("Ruch jest niedozwolony albo wykonany poza kolejnością.");
            return;
        }

        int allowedMove = Board.CanCheckerMoveOnce(
                move.getFromRow(),
                move.getFromColumn(),
                move.getToRow(),
                move.getToColumn()
        );
        if (allowedMove == 0 || (remoteContinuationRow != -1 && allowedMove != 2)) {
            rejectGuestMove("Podczas wielokrotnego bicia trzeba kontynuować bicie tym samym pionkiem.");
            return;
        }

        int moveResult = Board.MoveCheckerOnce(
                move.getFromRow(),
                move.getFromColumn(),
                move.getToRow(),
                move.getToColumn()
        );
        if (moveResult == 0) {
            rejectGuestMove("Ten ruch jest niedozwolony.");
            return;
        }

        try {
            networkSession.send(new NetworkMessage(MessageType.MOVE_APPLIED, move, null));
        } catch (IOException ignored) {
            return;
        }

        boardPanel.applyRemoteMove();

        if (moveResult == 2 && Board.HasAnyJumps(move.getToRow(), move.getToColumn())) {
            remoteContinuationRow = move.getToRow();
            remoteContinuationColumn = move.getToColumn();
        } else {
            remoteContinuationRow = -1;
            remoteContinuationColumn = -1;
            switchPlayer();
            checkWinCondition();
        }
    }

    private void applyHostApprovedMove(Move move) {
        if (move == null) {
            handleProtocolError();
            return;
        }

        int moveResult = Board.MoveCheckerOnce(
                move.getFromRow(),
                move.getFromColumn(),
                move.getToRow(),
                move.getToColumn()
        );
        if (moveResult == 0) {
            handleProtocolError();
            return;
        }

        if (boardPanel.isPendingMove(move)) {
            waitingForMoveResponse = false;
            boardPanel.confirmPendingMove(move, moveResult);
            return;
        }

        boardPanel.applyRemoteMove();
        if (moveResult != 2 || !Board.HasAnyJumps(move.getToRow(), move.getToColumn())) {
            switchPlayer();
            checkWinCondition();
        }
    }

    private boolean matchesRequiredContinuation(Move move) {
        if (remoteContinuationRow == -1) {
            return true;
        }
        return move.getFromRow() == remoteContinuationRow
                && move.getFromColumn() == remoteContinuationColumn;
    }

    private void rejectGuestMove(String reason) {
        try {
            networkSession.send(new NetworkMessage(MessageType.MOVE_REJECTED, null, reason));
        } catch (IOException ignored) {
            // The session reports the disconnect through its listener.
        }
    }

    private boolean isPlayerPiece(int row, int column, int player) {
        if (row < 0 || row >= Board.NUM_OF_TILES || column < 0 || column >= Board.NUM_OF_TILES) {
            return false;
        }
        int piece = Board.boardState[row][column];
        return player == 1 ? piece == 1 || piece == 3 : piece == 2 || piece == 4;
    }

    public void checkWinCondition() {
        if (onlineGame && !host) {
            return;
        }
        if (Board.HasAnyValidMoves(currentPlayer)) {
            return;
        }

        int winner = currentPlayer == 1 ? 2 : 1;
        if (onlineGame) {
            try {
                networkSession.send(new NetworkMessage(
                        MessageType.GAME_OVER,
                        null,
                        Integer.toString(winner)
                ));
            } catch (IOException ignored) {
                // The local result is still valid even if the opponent disconnected.
            }
        }
        showGameOver(winner);
    }

    private int parseWinner(String text) {
        try {
            int winner = Integer.parseInt(text);
            return winner == 1 || winner == 2 ? winner : 0;
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private void showGameOver(int winner) {
        if (gameOver) {
            return;
        }
        gameOver = true;
        if (timer != null) {
            timer.stop();
        }

        String winnerName = winner == 1 ? "Białe" : winner == 2 ? "Czarne" : "Nieznany gracz";
        Timer delayTimer = new Timer(300, e -> {
            Object[] options = {"Wróć do Menu", "Wyjdź z gry"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Wygrywają " + winnerName + "!\nGratulacje!",
                    "Koniec Gry!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                returnToMenu();
            } else {
                closeNetworkSession();
                System.exit(0);
            }
        });

        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void handleProtocolError() {
        closeNetworkSession();
        JOptionPane.showMessageDialog(
                this,
                "Stan planszy różni się od stanu hosta. Połączenie zostanie zamknięte.",
                "Błąd synchronizacji",
                JOptionPane.ERROR_MESSAGE
        );
        returnToMenu();
    }

    private void handleOpponentDisconnected(String reason) {
        if (closing || gameOver || !isDisplayable()) {
            return;
        }
        if (timer != null) {
            timer.stop();
        }

        String details = reason == null || reason.isBlank() ? "" : "\nSzczegóły: " + reason;
        JOptionPane.showMessageDialog(
                this,
                "Przeciwnik rozłączył się z grą." + details,
                "Utracono połączenie",
                JOptionPane.WARNING_MESSAGE
        );
        returnToMenu();
    }

    public void returnToMenu() {
        if (closing) {
            return;
        }
        closing = true;
        if (timer != null) {
            timer.stop();
        }
        closeNetworkSession();
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }

    private void closeNetworkSession() {
        if (networkSession != null) {
            networkSession.close();
        }
    }
}
