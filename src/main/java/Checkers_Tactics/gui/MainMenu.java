package Checkers_Tactics.gui;

import Checkers_Tactics.Main;
import Checkers_Tactics.network.GuestConnection;
import Checkers_Tactics.network.HostConnection;
import Checkers_Tactics.network.NetworkConnection;
import Checkers_Tactics.network.NetworkGameSession;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class MainMenu extends JFrame {

    private BufferedImage backgroundImage;

    public MainMenu() {
        setTitle("Checkers Tactics - Menu Główne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(new File("src/images/main-menu-background.jpg"));
        } catch (IOException e) {
            System.out.println("Błąd ładowania obrazka tła menu: " + e.getMessage());
        }

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    setBackground(new Color(51, 49, 43));
                }
            }
        };

        mainPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("CHECKERS TACTICS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JButton localPlayBtn = createMenuButton("Graj lokalnie");
        localPlayBtn.addActionListener(e -> startLocalGame());
        mainPanel.add(localPlayBtn, gbc);

        gbc.gridy++;
        JButton aiPlayBtn = createMenuButton("Graj z komputerem");
        aiPlayBtn.addActionListener(e -> showNotImplementedMessage("Gra z komputerem"));
        mainPanel.add(aiPlayBtn, gbc);

        gbc.gridy++;
        JButton onlinePlayBtn = createMenuButton("Graj w sieci");
        onlinePlayBtn.addActionListener(e -> startOnlineGame());
        mainPanel.add(onlinePlayBtn, gbc);

        gbc.gridy++;
        JButton exitBtn = createMenuButton("Wyjście");
        exitBtn.addActionListener(e -> System.exit(0));
        mainPanel.add(exitBtn, gbc);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            private final Color normalColor = new Color(136, 132, 132, 190);
            private final Color hoverColor = new Color(182, 180, 180, 250);
            private Color currentColor = normalColor;

            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);

                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        currentColor = hoverColor;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        currentColor = normalColor;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(currentColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(300, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void startLocalGame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            CheckersGUI gameWindow = new CheckersGUI();
            gameWindow.setVisible(true);
        });
    }

    private void startOnlineGame() {
        Object[] options = {"Utwórz grę", "Dołącz do gry", "Anuluj"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Host gra białymi i rozpoczyna partię.\nGość gra czarnymi.",
                "Gra sieciowa",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            Integer port = askForPort();
            if (port != null) {
                hostGame(port);
            }
        } else if (choice == 1) {
            joinGame();
        }
    }

    private Integer askForPort() {
        String value = JOptionPane.showInputDialog(
                this,
                "Port nasłuchiwania:",
                Integer.toString(Main.PORT)
        );
        if (value == null) {
            return null;
        }
        return parsePort(value);
    }

    private void hostGame(int port) {
        HostConnection connection = new HostConnection(port);
        String localAddress = getLocalAddress();
        connectInBackground(
                connection,
                NetworkGameSession.Role.HOST,
                connection::waitForGuest,
                "Oczekiwanie na gracza...\nAdres hosta: " + localAddress + "\nPort: " + port
        );
    }

    private void joinGame() {
        JTextField addressField = new JTextField("localhost");
        JTextField portField = new JTextField(Integer.toString(Main.PORT));
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Adres IP hosta:"));
        panel.add(addressField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Dołącz do gry",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String address = addressField.getText().trim();
        Integer port = parsePort(portField.getText());
        if (address.isEmpty() || port == null) {
            return;
        }

        GuestConnection connection = new GuestConnection(address, port);
        connectInBackground(
                connection,
                NetworkGameSession.Role.GUEST,
                connection::connect,
                "Łączenie z " + address + ":" + port + "..."
        );
    }

    private Integer parsePort(String value) {
        try {
            int port = Integer.parseInt(value.trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
            return port;
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Port musi być liczbą od 1 do 65535.",
                    "Nieprawidłowy port",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    private void connectInBackground(
            NetworkConnection connection,
            NetworkGameSession.Role role,
            ConnectionAction action,
            String statusText
    ) {
        JDialog progressDialog = createProgressDialog(statusText);
        SwingWorker<NetworkGameSession, Void> worker = new SwingWorker<>() {
            @Override
            protected NetworkGameSession doInBackground() throws Exception {
                action.connect();
                return new NetworkGameSession(connection, role);
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                setEnabled(true);
                try {
                    NetworkGameSession session = get();
                    dispose();
                    CheckersGUI gameWindow = new CheckersGUI(session);
                    gameWindow.setVisible(true);
                } catch (CancellationException ignored) {
                    connection.close();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    connection.close();
                } catch (ExecutionException exception) {
                    connection.close();
                    Throwable cause = exception.getCause();
                    JOptionPane.showMessageDialog(
                            MainMenu.this,
                            "Nie udało się nawiązać połączenia:\n" + cause.getMessage(),
                            "Błąd połączenia",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        JButton cancelButton = (JButton) progressDialog.getRootPane().getClientProperty("cancelButton");
        cancelButton.addActionListener(e -> {
            worker.cancel(true);
            connection.close();
            progressDialog.dispose();
            setEnabled(true);
        });

        setEnabled(false);
        worker.execute();
        progressDialog.setVisible(true);
    }

    private JDialog createProgressDialog(String statusText) {
        JDialog dialog = new JDialog(this, "Gra sieciowa", false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea status = new JTextArea(statusText);
        status.setEditable(false);
        status.setOpaque(false);
        status.setFont(new Font("SansSerif", Font.PLAIN, 15));
        status.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JButton cancelButton = new JButton("Anuluj");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);

        dialog.add(status, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.getRootPane().putClientProperty("cancelButton", cancelButton);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(360, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private String getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException exception) {
            return "sprawdź adres poleceniem ipconfig";
        }
    }

    private void showNotImplementedMessage(String featureName) {
        JOptionPane.showMessageDialog(
                this,
                featureName + " nie jest jeszcze zaimplementowana.\nBędzie dostępna wkrótce!",
                "W budowie",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @FunctionalInterface
    private interface ConnectionAction {
        void connect() throws IOException;
    }
}
