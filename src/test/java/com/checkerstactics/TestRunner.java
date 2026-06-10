package com.checkerstactics;

import Checkers_Tactics.Environment.Board;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestRunner {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        // --- TESTY WEWNĘTRZNEJ PLANSZY ---
        if (runTest("environment_shouldContainBoardClass", TestRunner::environment_shouldContainBoardClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("environment_validateBoardInitializationWhiteOnTop", TestRunner::environment_validateBoardInitializationWhiteOnTop)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("environment_validateBoardInitializationWhiteOnBottom", TestRunner::environment_validateBoardInitializationWhiteOnBottom)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("environment_CanCheckerMoveOnce", TestRunner::environment_CanCheckerMoveOnce)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("environment_MoveCheckerOnce", TestRunner::environment_MoveCheckerOnce)) {
            passed++;
        } else {
            failed++;
        }

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
    // 1. TESTY WEWNĘTRZNEJ PLANSZY
    // -------------------------------------------------------------------------

    private static void environment_shouldContainBoardClass() {
        assertClassExists("Checkers_Tactics.Environment.Board");
    }

    //Testy Inicjalizacji
    private static void environment_validateBoardInitializationWhiteOnTop() {

        Board.CheckersStartPosition whitePosition = Board.CheckersStartPosition.WHITE_ON_TOP;
        Board.Initialize(whitePosition);

        int startBottomPos = 5;
        int endBottomPos = startBottomPos + Board.ROWS_PER_COLOR;


        int curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_BOTTOM ? 1 : 2;
        for (int row = startBottomPos; row < endBottomPos; ++row)
            for (int col = row % 2; col < Board.NUM_OF_TILES; col += 2)
                assertTrue(Board.boardState[row][col] == curColor, "Inicjalizacja planszy jest niewłaściwa");

        int startTopPos = 0;
        int endTopPos = Board.ROWS_PER_COLOR;

        curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_TOP ? 1 : 2;
        for (int row = startTopPos; row < endTopPos; ++row)
            for (int col = row % 2; col < Board.NUM_OF_TILES; col += 2)
                assertTrue(Board.boardState[row][col] == curColor, "Inicjalizacja planszy jest niewłaściwa");

    }

    private static void environment_validateBoardInitializationWhiteOnBottom() {

        Board.CheckersStartPosition whitePosition = Board.CheckersStartPosition.WHITE_ON_BOTTOM;
        Board.Initialize(whitePosition);

        int startBottomPos = 5;
        int endBottomPos = startBottomPos + Board.ROWS_PER_COLOR;


        int curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_BOTTOM ? 1 : 2;
        for (int row = startBottomPos; row < endBottomPos; ++row)
            for (int col = row % 2; col < Board.NUM_OF_TILES; col += 2)
                assertTrue(Board.boardState[row][col] == curColor, "Inicjalizacja planszy jest niewłaściwa");

        int startTopPos = 0;
        int endTopPos = Board.ROWS_PER_COLOR;

        curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_TOP ? 1 : 2;
        for (int row = startTopPos; row < endTopPos; ++row)
            for (int col = row % 2; col < Board.NUM_OF_TILES; col += 2)
                assertTrue(Board.boardState[row][col] == curColor, "Inicjalizacja planszy jest niewłaściwa");

    }

    //Testy Ruchu pionków
    private static void environment_CanCheckerMoveOnce() {
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);

        //Błędne dane wejsciowe
        int result = Board.CanCheckerMoveOnce(-1,2,0,1);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 1");

        result = Board.CanCheckerMoveOnce(0,1,12,1);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 2");

        //Puste pole
        result = Board.CanCheckerMoveOnce(2,1,3,2);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 3");

        //Za daleko w prawo białe
        result = Board.CanCheckerMoveOnce(2,0,4,2);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 4");

        //Za daleko w prawo czarne
        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 5");


        //Ruch białymi do dołu w prawo
        result = Board.CanCheckerMoveOnce(2,0,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 6");

        //Ruch białymi do dołu w lewo
        result = Board.CanCheckerMoveOnce(2,2,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 7");

        //Ruch czarnymi do góry w prawo
        result = Board.CanCheckerMoveOnce(5,1,4,2);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 8");

        //Ruch czarnymi do góry w lewo
        result = Board.CanCheckerMoveOnce(5,1,4,0);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 9");


        //Zbicie białego przez czarne
        Board.boardState[4][2] = 1;
        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 10");

        //Zbicie czarnego przez białe
        Board.boardState[3][1] = 2;
        result = Board.CanCheckerMoveOnce(2,2,4,0);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 11");


        //PART 2 - BIAŁE NA DOLE-------------------------------------------
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        //Ruch białymi do góry w prawo
        result = Board.CanCheckerMoveOnce(2,0,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 12");

        //Ruch białymi do góry w lewo
        result = Board.CanCheckerMoveOnce(2,2,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 13");

        //Ruch czarnymi do dołu w prawo
        result = Board.CanCheckerMoveOnce(5,1,4,2);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 14");

        //Ruch czarnymi do dołu w lewo
        result = Board.CanCheckerMoveOnce(5,1,4,0);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 15");


        //Zbicie czarnego przez białe
        Board.boardState[4][2] = 2;
        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 16");

        //Zbicie białego przez czarne
        Board.boardState[3][1] = 1;
        result = Board.CanCheckerMoveOnce(2,2,4,0);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 17");
    }

    private static void environment_MoveCheckerOnce() {
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);

        //Błędne dane wejsciowe
        boolean success = Board.MoveCheckerOnce(-1,2,0,1);
        assertTrue(!success, "environment_MoveCheckerOnce Error 1");

        success = Board.MoveCheckerOnce(0,1,12,1);
        assertTrue(!success, "environment_MoveCheckerOnce Error 2");

        //Puste pole
        success = Board.MoveCheckerOnce(2,1,3,2);
        assertTrue(!success, "environment_MoveCheckerOnce Error 3");

        //Za daleko w prawo białe
        success = Board.MoveCheckerOnce(2,0,4,2);
        assertTrue(!success, "environment_MoveCheckerOnce Error 4");

        //Za daleko w prawo czarne
        success = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(!success, "environment_MoveCheckerOnce Error 5");


        //Ruch białymi do dołu w prawo
        success = Board.MoveCheckerOnce(2,0,3,1);
        assertTrue(success, "environment_MoveCheckerOnce Error 6");

        //Ruch białymi do dołu w lewo na zajęte pole
        success = Board.MoveCheckerOnce(2,2,3,1);
        assertTrue(!success, "environment_MoveCheckerOnce Error 7");

        //Ruch czarnymi do góry w prawo
        success = Board.MoveCheckerOnce(5,1,4,2);
        assertTrue(success, "environment_MoveCheckerOnce Error 8");

        //Ruch czarnymi do góry w lewo pustym polem
        success = Board.MoveCheckerOnce(5,1,4,0);
        assertTrue(!success, "environment_MoveCheckerOnce Error 9");


        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);
        //Zbicie białego przez czarne
        Board.boardState[4][2] = 1;
        success = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(Board.boardState[4][2] == 0, "environment_MoveCheckerOnce Error 10");
        assertTrue(success, "environment_MoveCheckerOnce Error 11");

        //Zbicie czarnego przez białe
        Board.boardState[3][1] = 2;
        success = Board.MoveCheckerOnce(2,2,4,0);
        assertTrue(Board.boardState[3][1] == 0, "environment_MoveCheckerOnce Error 12");
        assertTrue(success, "environment_MoveCheckerOnce Error 13");


        //PART 2 - BIAŁE NA DOLE-------------------------------------------
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        //Ruch białymi do góry w prawo
        success = Board.MoveCheckerOnce(2,0,3,1);
        assertTrue(success, "environment_MoveCheckerOnce Error 14");

        //Ruch białymi do góry w lewo na to samo pole
        success = Board.MoveCheckerOnce(2,2,3,1);
        assertTrue(!success, "environment_MoveCheckerOnce Error 15");

        //Ruch czarnymi do dołu w prawo
        success = Board.MoveCheckerOnce(5,1,4,2);
        assertTrue(success, "environment_MoveCheckerOnce Error 16");

        //Ruch czarnymi do dołu w lewo tym samym pionkiem
        success = Board.MoveCheckerOnce(5,1,4,0);
        assertTrue(!success, "environment_MoveCheckerOnce Error 17");

        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        //Zbicie czarnego przez białe
        Board.boardState[4][2] = 2;
        success = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(Board.boardState[4][2] == 0, "environment_MoveCheckerOnce Error 18");
        assertTrue(success, "environment_MoveCheckerOnce Error 19");

        //Zbicie białego przez czarne
        Board.boardState[3][1] = 1;
        success = Board.MoveCheckerOnce(2,2,4,0);
        assertTrue(Board.boardState[3][1] == 0, "environment_MoveCheckerOnce Error 20");
        assertTrue(success, "environment_MoveCheckerOnce Error 21");
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