
package Checkers_Tactics;

import Checkers_Tactics.Environment.Board;
import Checkers_Tactics.Environment.Board.CheckersStartPosition;

public class BoardTest {
    public static void main(String[] args) {
        Board.Initialize(CheckersStartPosition.WHITE_ON_TOP);
        Board.Preview();
        System.out.println();
        Board.MoveCheckerOnce(2, 1, 3, 2);
        Board.Preview();
    }
}
