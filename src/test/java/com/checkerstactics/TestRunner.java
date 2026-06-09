package com.checkerstactics;

import Checkers_Tactics.Environment.Board;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestRunner {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        //ENVIRONMENT SECTION//////////////////////////////////////////////////

        if (runTest("environment_shouldContainBoardClass", TestRunner::environment_shouldContainBoardClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("environment_boardShouldContainBasicMethods", TestRunner::environment_boardShouldContainBasicMethods)) {
            passed++;
        } else {
            failed++;
        }

        //GUI SECTION//////////////////////////////////////////////////

        if (runTest("gui_shouldContainCheckersGUIClass", TestRunner::gui_shouldContainCheckersGuiClass)) {
            passed++;
        } else {
            failed++;
        }

        //NETWORK SECTION//////////////////////////////////////////////////

        if (runTest("network_shouldContainGuestConnectionClass", TestRunner::network_shouldContainGuestConnectionClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("network_shouldContainHostConnectionClass", TestRunner::network_shouldContainHostConnectionClass)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("network_shouldUseJavaSockets", TestRunner::network_shouldUseJavaSockets)) {
            passed++;
        } else {
            failed++;
        }

        //FINALIZE

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

    private static void environment_shouldContainBoardClass() {
        assertClassExists("Environment.Board");
    }

    private static void environment_boardShouldContainBasicMethods() {
        Class<?> boardClass = getClassByName("Environment.Board");

        assertHasMethod(boardClass, "Initialize", Board.CheckersStartPosition.class);
        assertHasMethod(boardClass, "MoveCheckerOnce", int.class, int.class, int.class, int.class);
        assertHasMethod(boardClass, "CanCheckerMoveOnce", int.class, int.class, int.class, int.class);
    }

     // 2. GUI - inicjalizacja głównego okna, narysowanie szachownicy i pionków.



    private static void gui_shouldContainCheckersGuiClass() {
        assertClassExists("gui.CheckersGUI");
    }

    /*
     * 3. Zaprojektowanie komunikacji socketowej w Javie.
     */

    private static void network_shouldContainGuestConnectionClass() {
        assertClassExists("network.GuestConnection");
    }

    private static void network_shouldContainHostConnectionClass() {
        assertClassExists("network.HostConnection");
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
