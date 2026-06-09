package Checkers_Tactics.Environment;

public class Board
{
    public static final int NUM_OF_TILES = 8;
    public static final int ROWS_PER_COLOR = 3;
    //orientation: from top left to bottom right
    //0 - pusty, 1 - biała figura, 2 - czarna figura
    public static final int[][] boardState = new int[NUM_OF_TILES][NUM_OF_TILES];
    private static CheckersStartPosition whitePosition = Board.CheckersStartPosition.WHITE_ON_BOTTOM;

    public static boolean Initialize(CheckersStartPosition whitePosition)
    {
        //clear board
        for (int row = 0; row < NUM_OF_TILES; row++)
            for (int col = 0; col < NUM_OF_TILES; col++)
                boardState[row][col] = 0;

        Board.whitePosition = whitePosition;
        int startBottomPos = 5;
        int endBottomPos = startBottomPos + ROWS_PER_COLOR;


        int curColor = whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM ? 1 : 2;
        for (int row = startBottomPos; row < endBottomPos; ++row)
            for (int col = row % 2; col < NUM_OF_TILES; col += 2)
                boardState[row][col] = curColor;

        int startTopPos = 0;
        int endTopPos = ROWS_PER_COLOR;

        curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_TOP ? 1 : 2;
        for (int row = startTopPos; row < endTopPos; ++row)
            for (int col = row % 2; col < NUM_OF_TILES; col += 2)
                boardState[row][col] = curColor;

        return true;
    }

    public static boolean MoveCheckerOnce(int fromRow, int fromCol, int toRow, int toCol)
    {
        int canCheckerMoveResult = CanCheckerMoveOnce(fromRow, fromCol, toRow, toCol);
        if (canCheckerMoveResult == 0)
            return false;

        int checkerToMove = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = 0;
        boardState[toRow][toCol] = checkerToMove;
        if (canCheckerMoveResult == 2)
        {
            boardState[fromRow + (toRow - fromRow) / 2][fromCol + (toCol - fromCol) / 2] = 0;
        }

        return true;
    }

    /**
    Zwraca informacje o ruchu pionka z punktu do punktu

    @return {@code 0} - jeśli ruch jest niemożliwy,
    {@code 1} - jeśli ruch jest możliwy i NIE MA bicia pionka
    {@code 2} - jeśli ruch jest możliwy i JEST bicie pionka
     */
    public static int CanCheckerMoveOnce(int fromRow, int fromCol, int toRow, int toCol)
    {
        //move outside the board
        if (fromRow < 0 || fromCol < 0 || toRow < 0 || toCol < 0)
            return 0;

        int maxPossibleIndex = NUM_OF_TILES - 1;
        if (fromRow > maxPossibleIndex || fromCol > maxPossibleIndex || toRow > maxPossibleIndex || toCol > maxPossibleIndex)
            return 0;

        //try to move empty space
        int checkerValue = boardState[fromRow][fromCol];
        if (checkerValue == 0)
            return 0;

        //try to move to NOT empty space
        if (boardState[toRow][toCol] != 0)
            return 0;

        boolean tryMoveOneUp = toRow == fromRow - 1 && Math.abs(toCol - fromCol) == 1;
        boolean tryMoveOneDown = toRow == fromRow + 1 && Math.abs(toCol - fromCol) == 1;
        boolean tryMoveTwoUpRight = toRow == fromRow - 2 && toCol - fromCol == 2;
        boolean tryMoveTwoUpLeft = toRow == fromRow - 2 && toCol - fromCol == -2;
        boolean tryMoveTwoDownRight = toRow == fromRow + 2 && toCol - fromCol == 2;
        boolean tryMoveTwoDownLeft = toRow == fromRow + 2 && toCol - fromCol == -2;
        boolean enemyOnTrace = false;
        if (whitePosition == Board.CheckersStartPosition.WHITE_ON_BOTTOM)
        {
            if (checkerValue == 1)
            {
                if (tryMoveOneUp)
                    return 1;

                enemyOnTrace = isEnemyOnTile(fromRow - 1, fromCol + 1, 2);
                if (tryMoveTwoUpRight && enemyOnTrace)
                    return 2;

                enemyOnTrace = isEnemyOnTile(fromRow - 1, fromCol - 1, 2);
                if (tryMoveTwoUpLeft && enemyOnTrace)
                    return 2;
            }

            if (checkerValue == 2)
            {
                if (tryMoveOneDown)
                    return 1;

                enemyOnTrace = isEnemyOnTile(fromRow + 1, fromCol + 1, 1);
                if (tryMoveTwoDownRight && enemyOnTrace)
                    return 2;

                enemyOnTrace = isEnemyOnTile(fromRow + 1, fromCol - 1, 1);
                if (tryMoveTwoDownLeft && enemyOnTrace)
                    return 2;
            }

            return 0;
        }

        if (checkerValue == 2)
        {
            if (tryMoveOneUp)
                return 1;

            enemyOnTrace = isEnemyOnTile(fromRow - 1, fromCol + 1, 1);;
            if (tryMoveTwoUpRight && enemyOnTrace)
                return 2;

            enemyOnTrace = isEnemyOnTile(fromRow - 1, fromCol - 1, 1);;
            if (tryMoveTwoUpLeft && enemyOnTrace)
                return 2;
        }

        if (checkerValue == 1)
        {
            if (tryMoveOneDown)
                return 1;

            enemyOnTrace = isEnemyOnTile(fromRow + 1, fromCol + 1, 2);;
            if (tryMoveTwoDownRight && enemyOnTrace)
                return 2;

            enemyOnTrace = isEnemyOnTile(fromRow + 1, fromCol - 1, 2);;
            if (tryMoveTwoDownLeft && enemyOnTrace)
                return 2;
        }

        return 0;
    }

    private static boolean isEnemyOnTile(int row, int col, int enemyNum)
    {
        if(row < 0 || col < 0)
            return false;
        if(row > NUM_OF_TILES - 1 || col > NUM_OF_TILES - 1)
            return false;

        return boardState[row][col] == enemyNum;
    }

    public static void Preview()
    {
        for (int row = 0; row < NUM_OF_TILES; ++row)
        {
            for (int col = 0; col < NUM_OF_TILES; ++col)
            {
                int positionValue = boardState[row][col];
                switch (positionValue)
                {
                    case 0:
                        System.out.print("[ ] ");
                        break;
                    case 1:
                        System.out.print("[W] ");
                        break;
                    case 2:
                        System.out.print("[B] ");
                }
            }

            System.out.println();
        }

    }

    public enum CheckersStartPosition
    {
        WHITE_ON_TOP, WHITE_ON_BOTTOM
    }
}
