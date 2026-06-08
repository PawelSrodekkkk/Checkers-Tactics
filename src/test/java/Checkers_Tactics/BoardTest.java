
package Checkers_Tactics;

import Checkers_Tactics.Environment.Board;
import Checkers_Tactics.Environment.Board.CheckersStartPosition;

public class BoardTest {
    public static void main(String[] args) {
        Board.Initialize(CheckersStartPosition.WHITE_ON_TOP);
        Board.Preview();
        System.out.println();

        Board.MoveCheckerOnce(2, 0, 3, 1);
        Board.Preview();
        System.out.println();

        Board.MoveCheckerOnce(2, 0, 3, 1);
        Board.Preview();
        System.out.println();

        Board.MoveCheckerOnce(5, 1, 4, 2);
        Board.Preview();
        System.out.println();
    }
}
