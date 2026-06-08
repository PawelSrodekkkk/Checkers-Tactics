package Checkers_Tactics.Environment;

public class Board {
    private static final int NUM_OF_TILES = 8;
    private static final int ROWS_PER_COLOR = 3;
    private static final int[][] boardState = new int[8][8];
    private static CheckersStartPosition whitePosition;

    public static boolean Initialize(CheckersStartPosition whitePosition) {
        Board.whitePosition = whitePosition;
        int curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_TOP ? 2 : 1;
        int startTopPos = 5;
        int endTopPos = startTopPos + 3;

        for (int row = startTopPos; row < endTopPos; ++row) {
            for (int col = row % 2; col < 8; col += 2) {
                boardState[row][col] = curColor;
            }
        }

        curColor = whitePosition == Board.CheckersStartPosition.WHITE_ON_TOP ? 1 : 2;
        int startBottomPos = 0;
        int endBottomPos = 3;

        for (int row = startBottomPos; row < endBottomPos; ++row) {
            for (int col = row % 2; col < 8; col += 2) {
                boardState[row][col] = curColor;
            }
        }

        return true;
    }

    public static boolean MoveCheckerOnce(int fromRow, int fromCol, int toRow, int toCol) {
        int canCheckerMoveResult = CanCheckerMoveOnce(fromRow, fromCol, toRow, toCol);
        if (canCheckerMoveResult == 0) {
            return false;
        } else {
            int invertedFromRow = 8 - fromRow - 1;
            int invertedToRow = 8 - toRow - 1;
            int checkerToMove = boardState[invertedFromRow][fromCol];
            boardState[invertedFromRow][fromCol] = 0;
            boardState[invertedToRow][toCol] = checkerToMove;
            if (canCheckerMoveResult == 2) {
                boardState[invertedFromRow + (invertedToRow - invertedFromRow) / 2][fromCol + (toCol - fromCol) / 2] = 0;
            }

            return true;
        }
    }

    public static int CanCheckerMoveOnce(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow >= 0 && fromCol >= 0 && toRow >= 0 && toCol >= 0) {
            int maxPossibleIndex = 7;
            if (fromRow <= maxPossibleIndex && fromCol <= maxPossibleIndex && toRow <= maxPossibleIndex && toCol <= maxPossibleIndex) {
                int invertedFromRow = 8 - fromRow - 1;
                int invertedToRow = 8 - toRow - 1;
                int checkerValue = boardState[invertedFromRow][fromCol];
                if (checkerValue == 0) {
                    return 0;
                } else if (boardState[invertedToRow][toCol] != 0) {
                    return 0;
                } else {
                    boolean tryMoveOneUp = invertedToRow == invertedFromRow + 1 && Math.abs(toCol - fromCol) == 1;
                    boolean tryMoveOneDown = invertedToRow == invertedFromRow - 1 && Math.abs(toCol - fromCol) == 1;
                    boolean tryMoveTwoUpRight = invertedToRow == invertedFromRow + 2 && toCol - fromCol == 2;
                    boolean tryMoveTwoUpLeft = invertedToRow == invertedFromRow + 2 && toCol - fromCol == -2;
                    boolean tryMoveTwoDownRight = invertedToRow == invertedFromRow - 2 && toCol - fromCol == 2;
                    boolean tryMoveTwoDownLeft = invertedToRow == invertedFromRow - 2 && toCol - fromCol == -2;
                    boolean enemyOnTrace = false;
                    if (whitePosition == Board.CheckersStartPosition.WHITE_ON_BOTTOM) {
                        if (checkerValue == 1) {
                            if (tryMoveOneUp) {
                                return 1;
                            }

                            enemyOnTrace = boardState[invertedFromRow + 1][fromCol + 1] == 2;
                            if (tryMoveTwoUpRight && enemyOnTrace) {
                                return 2;
                            }

                            enemyOnTrace = boardState[invertedFromRow + 1][fromCol - 1] == 2;
                            if (tryMoveTwoUpLeft && enemyOnTrace) {
                                return 2;
                            }
                        }

                        if (checkerValue == 2) {
                            if (tryMoveOneDown) {
                                return 1;
                            }

                            enemyOnTrace = boardState[invertedFromRow - 1][fromCol + 1] == 1;
                            if (tryMoveTwoDownRight && enemyOnTrace) {
                                return 2;
                            }

                            enemyOnTrace = boardState[invertedFromRow - 1][fromCol - 1] == 1;
                            if (tryMoveTwoDownLeft && enemyOnTrace) {
                                return 2;
                            }
                        }
                    } else {
                        if (checkerValue == 2) {
                            if (tryMoveOneUp) {
                                return 1;
                            }

                            enemyOnTrace = boardState[invertedFromRow + 1][fromCol + 1] == 1;
                            if (tryMoveTwoUpRight && enemyOnTrace) {
                                return 2;
                            }

                            enemyOnTrace = boardState[invertedFromRow + 1][fromCol - 1] == 1;
                            if (tryMoveTwoUpLeft && enemyOnTrace) {
                                return 2;
                            }
                        }

                        if (checkerValue == 1) {
                            if (tryMoveOneDown) {
                                return 1;
                            }

                            enemyOnTrace = boardState[invertedFromRow - 1][fromCol + 1] == 2;
                            if (tryMoveTwoDownRight && enemyOnTrace) {
                                return 2;
                            }

                            enemyOnTrace = boardState[invertedFromRow - 1][fromCol - 1] == 2;
                            if (tryMoveTwoDownLeft && enemyOnTrace) {
                                return 2;
                            }
                        }
                    }

                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static void Preview() {
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                int positionValue = boardState[row][col];
                switch (positionValue) {
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

    static {
        whitePosition = Board.CheckersStartPosition.WHITE_ON_BOTTOM;
    }

    public static enum CheckersStartPosition {
        WHITE_ON_TOP,
        WHITE_ON_BOTTOM;
    }
}
