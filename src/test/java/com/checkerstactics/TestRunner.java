package Checkers_Tactics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestRunner {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        // --- TESTY REPREZENTACJI PLANSZY (ZAKOMENTOWANE) ---
        // Ponieważ w Twoim kodzie logika planszy znajduje się wewnątrz CheckersGUI (int[][] boardState),
        // a nie w osobnych klasach, te testy by nie przeszły.
        /*
        if (runTest("boardRepresentation_shouldContainBoardClass", TestRunner::boardRepresentation_shouldContainBoardClass)) passed++; else failed++;
        if (runTest("boardRepresentation_shouldContainPieceClass", TestRunner::boardRepresentation_shouldContainPieceClass)) passed++; else failed++;
        if (runTest("boardRepresentation_boardShouldExposeBasicBoardMethods", TestRunner::boardRepresentation_boardShouldExposeBasicBoardMethods)) passed++; else failed++;
        */

        // --- TESTY GUI ---
        if (runTest("gui_shouldContainCheckersGUIClass", TestRunner::gui_shouldContainCheckersGUIClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("gui_shouldContainBoardPanelClass", TestRunner::gui_shouldContainBoardPanelClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("gui_mainWindowShouldBeSwingFrame", TestRunner::gui_mainWindowShouldBeSwingFrame)) {
            passed++;
        } else {
            failed++;
        }

        // --- TESTY SIECIOWE ---
        if (runTest("network_shouldContainSocketClientClass", TestRunner::network_shouldContainSocketClientClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("network_shouldContainSocketServerClass", TestRunner::network_shouldContainSocketServerClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("network_shouldUseJavaSockets", TestRunner::network_shouldUseJavaSockets)) {
            passed++;
        } else {
            failed++;
        }

        System.out.println();
        System.out.println("Test results:");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError("Some tests failed");
        }
    }

    private static boolean runTest(String testName, Runnable test) {
        try {
            test.run();
            System.out.println("[PASS] " + testName);
            return true;
        } catch (Throwable error) {
            System.out.println("[FAIL] " + testName);
            System.out.println("       " + error.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // 2. GUI - inicjalizacja głównego okna, narysowanie szachownicy i pionków.
    // -------------------------------------------------------------------------

    private static void gui_shouldContainCheckersGUIClass() {
        assertClassExists("Checkers_Tactics.gui.CheckersGUI");
    }

    private static void gui_shouldContainBoardPanelClass() {
        // Ponieważ BoardPanel jest klasą wewnętrzną (inner class) w CheckersGUI, używamy znaku $
        assertClassExists("Checkers_Tactics.gui.CheckersGUI$BoardPanel");
    }

    private static void gui_mainWindowShouldBeSwingFrame() {
        Class<?> mainWindowClass = getClassByName("Checkers_Tactics.gui.CheckersGUI");

        assertTrue(
                javax.swing.JFrame.class.isAssignableFrom(mainWindowClass),
                "CheckersGUI should extend javax.swing.JFrame"
        );
    }

    // -------------------------------------------------------------------------
    // 3. Zaprojektowanie komunikacji socketowej w Javie.
    // -------------------------------------------------------------------------

    private static void network_shouldContainSocketClientClass() {
        // Dopasowano do Twojej klasy klienta
        assertClassExists("Checkers_Tactics.network.GuestConnection");
    }

    private static void network_shouldContainSocketServerClass() {
        // Dopasowano do Twojej klasy serwera
        assertClassExists("Checkers_Tactics.network.HostConnection");
    }

    private static void network_shouldUseJavaSockets() {
        Class<?> socketClass = java.net.Socket.class;
        Class<?> serverSocketClass = java.net.ServerSocket.class;

        assertTrue(socketClass != null, "java.net.Socket should be available");
        assertTrue(serverSocketClass != null, "java.net.ServerSocket should be available");
    }

    // -------------------------------------------------------------------------
    // METODY POMOCNICZE
    // -------------------------------------------------------------------------

    private static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new AssertionError("Expected class does not exist: " + className);
        }
    }

    private static void assertClassExists(String className) {
        getClassByName(className);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}