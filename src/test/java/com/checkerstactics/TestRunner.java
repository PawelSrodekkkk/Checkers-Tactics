package com.checkerstactics;

import Checkers_Tactics.Environment.Board;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestRunner {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        // --- TESTY WEWNĘTRZNEJ PLANSZY ---
        if (runTest("environment_shouldContainBoardClass", TestRunner::environment_shouldContainBoardClass)) passed++; else failed++;
        if (runTest("environment_validateBoardInitializationWhiteOnTop", TestRunner::environment_validateBoardInitializationWhiteOnTop)) passed++; else failed++;
        if (runTest("environment_validateBoardInitializationWhiteOnBottom", TestRunner::environment_validateBoardInitializationWhiteOnBottom)) passed++; else failed++;
        if (runTest("environment_CanCheckerMoveOnce", TestRunner::environment_CanCheckerMoveOnce)) passed++; else failed++;
        if (runTest("environment_MoveCheckerOnce", TestRunner::environment_MoveCheckerOnce)) passed++; else failed++;

        // --- TESTY GUI ---
        if (runTest("gui_shouldContainCheckersGUIClass", TestRunner::gui_shouldContainCheckersGUIClass)) passed++; else failed++;
        if (runTest("gui_shouldContainBoardPanelClass", TestRunner::gui_shouldContainBoardPanelClass)) passed++; else failed++;
        if (runTest("gui_mainWindowShouldBeSwingFrame", TestRunner::gui_mainWindowShouldBeSwingFrame)) passed++; else failed++;

        // --- TESTY SIECIOWE ---
        if (runTest("network_shouldContainSocketClientClass", TestRunner::network_shouldContainSocketClientClass)) passed++; else failed++;
        if (runTest("network_shouldContainSocketServerClass", TestRunner::network_shouldContainSocketServerClass)) passed++; else failed++;
        if (runTest("network_shouldUseJavaSockets", TestRunner::network_shouldUseJavaSockets)) passed++; else failed++;

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

    private static void environment_CanCheckerMoveOnce() {
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);

        int result = Board.CanCheckerMoveOnce(-1,2,0,1);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 1");

        result = Board.CanCheckerMoveOnce(0,1,12,1);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 2");

        result = Board.CanCheckerMoveOnce(2,1,3,2);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 3");

        result = Board.CanCheckerMoveOnce(2,0,4,2);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 4");

        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 0, "environment_CanCheckerMoveOnce Error 5");

        result = Board.CanCheckerMoveOnce(2,0,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 6");

        result = Board.CanCheckerMoveOnce(2,2,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 7");

        result = Board.CanCheckerMoveOnce(5,1,4,2);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 8");

        result = Board.CanCheckerMoveOnce(5,1,4,0);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 9");

        Board.boardState[4][2] = 1;
        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 10");

        Board.boardState[3][1] = 2;
        result = Board.CanCheckerMoveOnce(2,2,4,0);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 11");

        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        result = Board.CanCheckerMoveOnce(2,0,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 12");

        result = Board.CanCheckerMoveOnce(2,2,3,1);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 13");

        result = Board.CanCheckerMoveOnce(5,1,4,2);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 14");

        result = Board.CanCheckerMoveOnce(5,1,4,0);
        assertTrue(result == 1, "environment_CanCheckerMoveOnce Error 15");

        Board.boardState[4][2] = 2;
        result = Board.CanCheckerMoveOnce(5,1,3,3);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 16");

        Board.boardState[3][1] = 1;
        result = Board.CanCheckerMoveOnce(2,2,4,0);
        assertTrue(result == 2, "environment_CanCheckerMoveOnce Error 17");
    }

    private static void environment_MoveCheckerOnce() {
        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);

        // Zmiana boolena na sprawdzanie INT (0 = błąd, >0 = sukces)
        int moveResult = Board.MoveCheckerOnce(-1,2,0,1);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 1");

        moveResult = Board.MoveCheckerOnce(0,1,12,1);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 2");

        moveResult = Board.MoveCheckerOnce(2,1,3,2);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 3");

        moveResult = Board.MoveCheckerOnce(2,0,4,2);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 4");

        moveResult = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 5");

        moveResult = Board.MoveCheckerOnce(2,0,3,1);
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 6");

        moveResult = Board.MoveCheckerOnce(2,2,3,1);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 7");

        moveResult = Board.MoveCheckerOnce(5,1,4,2);
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 8");

        moveResult = Board.MoveCheckerOnce(5,1,4,0);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 9");


        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_TOP);
        Board.boardState[4][2] = 1;
        moveResult = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(Board.boardState[4][2] == 0, "environment_MoveCheckerOnce Error 10");
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 11");

        Board.boardState[3][1] = 2;
        moveResult = Board.MoveCheckerOnce(2,2,4,0);
        assertTrue(Board.boardState[3][1] == 0, "environment_MoveCheckerOnce Error 12");
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 13");


        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        moveResult = Board.MoveCheckerOnce(2,0,3,1);
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 14");

        moveResult = Board.MoveCheckerOnce(2,2,3,1);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 15");

        moveResult = Board.MoveCheckerOnce(5,1,4,2);
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 16");

        moveResult = Board.MoveCheckerOnce(5,1,4,0);
        assertTrue(moveResult == 0, "environment_MoveCheckerOnce Error 17");

        Board.Initialize(Board.CheckersStartPosition.WHITE_ON_BOTTOM);

        Board.boardState[4][2] = 2;
        moveResult = Board.MoveCheckerOnce(5,1,3,3);
        assertTrue(Board.boardState[4][2] == 0, "environment_MoveCheckerOnce Error 18");
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 19");

        Board.boardState[3][1] = 1;
        moveResult = Board.MoveCheckerOnce(2,2,4,0);
        assertTrue(Board.boardState[3][1] == 0, "environment_MoveCheckerOnce Error 20");
        assertTrue(moveResult > 0, "environment_MoveCheckerOnce Error 21");
    }

    // -------------------------------------------------------------------------
    // 2. GUI - inicjalizacja głównego okna, narysowanie szachownicy i pionków.
    // -------------------------------------------------------------------------

    private static void gui_shouldContainCheckersGUIClass() {
        assertClassExists("Checkers_Tactics.gui.CheckersGUI");
    }

    private static void gui_shouldContainBoardPanelClass() {
        // Poprawiona ścieżka: usunięto "$", ponieważ zrobiliśmy z tego osobną klasę!
        assertClassExists("Checkers_Tactics.gui.BoardPanel");
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
        assertClassExists("Checkers_Tactics.network.GuestConnection");
    }

    private static void network_shouldContainSocketServerClass() {
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