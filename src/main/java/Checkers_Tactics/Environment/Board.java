package Checkers_Tactics.Environment;

public class Board
{
    public static final int NUM_OF_TILES = 8;
    public static final int ROWS_PER_COLOR = 3;

    // orientation: from top left to bottom right
    // 0 - pusty, 1 - biała figura, 2 - czarna figura, 3 - biała damka, 4 - czarna damka
    public static final int[][] boardState = new int[NUM_OF_TILES][NUM_OF_TILES];
    private static CheckersStartPosition whitePosition = Board.CheckersStartPosition.WHITE_ON_BOTTOM;

    public static boolean Initialize(CheckersStartPosition whitePosition)
    {
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

    public static int MoveCheckerOnce(int fromRow, int fromCol, int toRow, int toCol)
    {
        int canCheckerMoveResult = CanCheckerMoveOnce(fromRow, fromCol, toRow, toCol);
        if (canCheckerMoveResult == 0)
            return 0;

        int checkerToMove = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = 0;
        boardState[toRow][toCol] = checkerToMove;

        // Usunięcie zbitej figury po drodze (teraz radzi sobie z dalekimi odległościami Latającej Damki)
        if (canCheckerMoveResult == 2)
        {
            int rowDir = Integer.compare(toRow, fromRow);
            int colDir = Integer.compare(toCol, fromCol);

            int r = fromRow + rowDir;
            int c = fromCol + colDir;

            while (r != toRow && c != toCol) {
                boardState[r][c] = 0;
                r += rowDir;
                c += colDir;
            }
        }

        // Promocja na damkę, jeśli pionek dotarł na koniec planszy
        if (checkerToMove == 1) {
            int promotionRow = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM) ? 0 : NUM_OF_TILES - 1;
            if (toRow == promotionRow) boardState[toRow][toCol] = 3;
        } else if (checkerToMove == 2) {
            int promotionRow = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM) ? NUM_OF_TILES - 1 : 0;
            if (toRow == promotionRow) boardState[toRow][toCol] = 4;
        }

        return canCheckerMoveResult;
    }

    public static int CanCheckerMoveOnce(int fromRow, int fromCol, int toRow, int toCol)
    {
        if (fromRow < 0 || fromCol < 0 || toRow < 0 || toCol < 0) return 0;
        int maxPossibleIndex = NUM_OF_TILES - 1;
        if (fromRow > maxPossibleIndex || fromCol > maxPossibleIndex || toRow > maxPossibleIndex || toCol > maxPossibleIndex) return 0;

        int checkerValue = boardState[fromRow][fromCol];
        if (checkerValue == 0) return 0;
        if (boardState[toRow][toCol] != 0) return 0;

        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // Każdy ruch MUSI być po przekątnej
        if (rowDiff != colDiff) return 0;

        int dist = rowDiff;
        int rowDir = Integer.compare(toRow, fromRow); // daje nam -1 lub 1
        int colDir = Integer.compare(toCol, fromCol); // daje nam -1 lub 1

        boolean isWhite = (checkerValue == 1 || checkerValue == 3);
        boolean isKing = (checkerValue == 3 || checkerValue == 4);

        if (isKing) {
            // Logika latającej damki
            int piecesInBetween = 0;
            int enemyRow = -1;
            int enemyCol = -1;

            int r = fromRow + rowDir;
            int c = fromCol + colDir;

            // Sprawdzamy trasę lotu
            while (r != toRow && c != toCol) {
                if (boardState[r][c] != 0) {
                    piecesInBetween++;
                    enemyRow = r;
                    enemyCol = c;
                }
                r += rowDir;
                c += colDir;
            }

            if (piecesInBetween == 0) return 1; // Zwykły ruch przelotowy
            if (piecesInBetween == 1) { // Dokładnie jedna figura na drodze
                if (isEnemyOnTile(enemyRow, enemyCol, checkerValue)) {
                    return 2; // Bicie
                }
            }

            return 0; // Skok nad swoimi lub zablokowana ścieżka
        } else {
            // Zwykły pionek
            boolean movesUp = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM ? isWhite : !isWhite);
            boolean movesDown = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM ? !isWhite : isWhite);

            if (dist == 1) {
                if (movesUp && rowDir == -1) return 1;
                if (movesDown && rowDir == 1) return 1;
            } else if (dist == 2) {
                // Skok przez wroga
                if (movesUp && rowDir == -1 && isEnemyOnTile(fromRow - 1, fromCol + colDir, checkerValue)) return 2;
                if (movesDown && rowDir == 1 && isEnemyOnTile(fromRow + 1, fromCol + colDir, checkerValue)) return 2;
            }

            return 0;
        }
    }

    public static boolean HasAnyJumps(int row, int col)
    {
        int checkerValue = boardState[row][col];
        if (checkerValue == 0) return false;

        boolean isKing = (checkerValue == 3 || checkerValue == 4);
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        if (isKing) {
            // Damka potrafi bić z daleka
            for (int[] d : directions) {
                int r = row + d[0];
                int c = col + d[1];

                // Idziemy po przekątnej dopóki nie natrafimy na krawędź planszy
                while (r >= 0 && r < NUM_OF_TILES && c >= 0 && c < NUM_OF_TILES) {
                    if (boardState[r][c] != 0) {
                        // Ktoś tu stoi. Czy to wróg?
                        if (isEnemyOnTile(r, c, checkerValue)) {
                            // Sprawdzamy czy pole ZARAZ ZA WROGIEM jest puste (żeby mogła wylądować)
                            int landR = r + d[0];
                            int landC = c + d[1];
                            if (landR >= 0 && landR < NUM_OF_TILES && landC >= 0 && landC < NUM_OF_TILES) {
                                if (boardState[landR][landC] == 0) {
                                    return true;
                                }
                            }
                        }
                        break; // Pierwsza napotkana figura wszystko blokuje - kończymy szukanie w tym kierunku
                    }
                    r += d[0];
                    c += d[1];
                }
            }
        } else {
            // Zwykły pionek musi szukać wroga w odległości 2
            boolean isWhite = (checkerValue == 1);
            boolean movesUp = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM ? isWhite : !isWhite);
            boolean movesDown = (whitePosition == CheckersStartPosition.WHITE_ON_BOTTOM ? !isWhite : isWhite);

            for (int[] d : directions) {
                if ((movesUp && d[0] == -1) || (movesDown && d[0] == 1)) {
                    if (CanCheckerMoveOnce(row, col, row + d[0] * 2, col + d[1] * 2) == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isEnemyOnTile(int row, int col, int myPieceValue)
    {
        if(row < 0 || col < 0 || row > NUM_OF_TILES - 1 || col > NUM_OF_TILES - 1)
            return false;

        int targetValue = boardState[row][col];
        if (targetValue == 0) return false;

        boolean iAmWhite = (myPieceValue == 1 || myPieceValue == 3);
        boolean targetIsBlack = (targetValue == 2 || targetValue == 4);
        boolean targetIsWhite = (targetValue == 1 || targetValue == 3);

        return iAmWhite ? targetIsBlack : targetIsWhite;
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
                    case 0: System.out.print("[ ] "); break;
                    case 1: System.out.print("[W] "); break;
                    case 2: System.out.print("[B] "); break;
                    case 3: System.out.print("[W*]"); break;
                    case 4: System.out.print("[B*]"); break;
                }
            }
            System.out.println();
        }
    }

    public enum CheckersStartPosition {
        WHITE_ON_TOP, WHITE_ON_BOTTOM
    }
}