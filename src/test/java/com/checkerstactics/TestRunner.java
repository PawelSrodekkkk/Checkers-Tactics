package com.checkerstactics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestRunner {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        if (runTest("boardRepresentation_shouldContainBoardClass", TestRunner::boardRepresentation_shouldContainBoardClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("boardRepresentation_shouldContainPieceClass", TestRunner::boardRepresentation_shouldContainPieceClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("boardRepresentation_boardShouldExposeBasicBoardMethods", TestRunner::boardRepresentation_boardShouldExposeBasicBoardMethods)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("gui_shouldContainMainWindowClass", TestRunner::gui_shouldContainMainWindowClass)) {
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

     // Stworzenie wewnętrznej reprezentacji planszy i pionków.

    private static void boardRepresentation_shouldContainBoardClass() {
        assertClassExists("com.checkerstactics.board.Board");
    }

    private static void boardRepresentation_shouldContainPieceClass() {
        assertClassExists("com.checkerstactics.board.Piece");
    }

    private static void boardRepresentation_boardShouldExposeBasicBoardMethods() {
        Class<?> boardClass = getClassByName("com.checkerstactics.board.Board");

        assertHasMethod(boardClass, "getRows");
        assertHasMethod(boardClass, "getColumns");
        assertHasMethod(boardClass, "getPiece", int.class, int.class);
    }

     // 2. GUI - inicjalizacja głównego okna, narysowanie szachownicy i pionków.


    private static void gui_shouldContainMainWindowClass() {
        assertClassExists("com.checkerstactics.gui.MainWindow");
    }

    private static void gui_shouldContainBoardPanelClass() {
        assertClassExists("com.checkerstactics.gui.BoardPanel");
    }

    private static void gui_mainWindowShouldBeSwingFrame() {
        Class<?> mainWindowClass = getClassByName("com.checkerstactics.gui.MainWindow");

        assertTrue(
                javax.swing.JFrame.class.isAssignableFrom(mainWindowClass),
                "MainWindow should extend javax.swing.JFrame"
        );
    }

    /*
     * 3. Zaprojektowanie komunikacji socketowej w Javie.
     */

    private static void network_shouldContainSocketClientClass() {
        assertClassExists("com.checkerstactics.network.SocketClient");
    }

    private static void network_shouldContainSocketServerClass() {
        assertClassExists("com.checkerstactics.network.SocketServer");
    }

    private static void network_shouldUseJavaSockets() {
        Class<?> socketClass = java.net.Socket.class;
        Class<?> serverSocketClass = java.net.ServerSocket.class;

        assertTrue(socketClass != null, "java.net.Socket should be available");
        assertTrue(serverSocketClass != null, "java.net.ServerSocket should be available");
    }


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

    private static void assertHasMethod(Class<?> checkedClass, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = checkedClass.getDeclaredMethod(methodName, parameterTypes);
            assertTrue(method != null, "Expected method does not exist: " + methodName);
        } catch (NoSuchMethodException exception) {
            throw new AssertionError(
                    "Expected method does not exist: "
                            + checkedClass.getName()
                            + "."
                            + methodName
            );
        }
    }

    private static void assertHasPublicNoArgsConstructor(Class<?> checkedClass) {
        try {
            Constructor<?> constructor = checkedClass.getDeclaredConstructor();
            assertTrue(constructor != null, "Expected no-args constructor does not exist");
        } catch (NoSuchMethodException exception) {
            throw new AssertionError(
                    "Expected no-args constructor does not exist in class: "
                            + checkedClass.getName()
            );
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}