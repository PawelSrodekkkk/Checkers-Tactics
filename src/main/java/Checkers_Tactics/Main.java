package Checkers_Tactics;

import Checkers_Tactics.gui.MainMenu;

import javax.swing.*;

public class Main {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
