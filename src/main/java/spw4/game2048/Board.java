package spw4.game2048;

import java.util.*;

/**
 * This class represents the game board. It provides
 * methods for seting a new tile and also moving the
 * existing tile in the given direction.
 */
public class Board {
    private final static int BOARD_SIZE = 4;
    private int[][] board;
    private int points;
    public static Random random;
    public List<Position> emptyPositions = new ArrayList<>();

    public Board() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                builder.append(board[i][j]);
                builder.append("    ");
            }
            builder.append("\n");
        }
        return builder.toString()
    }

    public void setTileValue(int value, int row, int column) {

        if (!isValidBoardPosition(row, column)) {
            throw new IllegalArgumentException("Row or column is out of bounds.");
        }

        if (!isValidTileValue(value)) {
            throw new IllegalArgumentException("Value has to be a power of two.");
        }

        board[row][column] = value;
        updateEmptyPositions();
    }

    public int getTileValue(int row, int column) {

        if (!isValidBoardPosition(row, column)) {
            throw new IllegalArgumentException("Row or column is out of bounds.");
        }

        return board[row][column];
    }

    private boolean isValidBoardPosition(int row, int column) {
        return row < BOARD_SIZE && column < BOARD_SIZE && row >= 0 && column >= 0;
    }

    private boolean isValidTileValue(int value) {
        return value != 1 && (value & value - 1) == 0;
    }

    public void initialize() {

        board = new int[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < 2; i++) {
            int value = random.nextInt(100) < 90 ? 2 : 4;
            int row = random.nextInt(BOARD_SIZE);
            int column = random.nextInt(BOARD_SIZE);

            while (getTileValue(row, column) != 0) {
                value = random.nextInt(100) < 90 ? 2 : 4;
                row = random.nextInt(BOARD_SIZE);
                column = random.nextInt(BOARD_SIZE);
            }

            setTileValue(value, row, column);
        }
    }

    public boolean move(Direction direction) {

        boolean leftRight = direction == Direction.left || direction == Direction.right;
        boolean leftUp = direction == Direction.left || direction == Direction.up;

        int[][] oldBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                oldBoard[row][column] = board[row][column];
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {

            LinkedList<Integer> currentElements = new LinkedList<>();

            for (int j = 0; j < BOARD_SIZE; j++) {

                int row = leftRight ? i : j;
                int column = leftRight ? j : i;

                if (getTileValue(row, column) != 0) {
                    if(leftUp) {
                        currentElements.addLast(getTileValue(row, column));
                    } else {
                        currentElements.addFirst(getTileValue(row, column));
                    }
                }
            }

            LinkedList<Integer> updatedElements = new LinkedList<>();
            while (currentElements.size() > 1) {
                int firstValue = currentElements.remove();
                int secondValue = currentElements.getFirst();
                if (firstValue == secondValue) {
                    int mergedValue = firstValue * 2;
                    updatedElements.add(mergedValue);
                    points += mergedValue;
                    currentElements.remove();
                } else {
                    updatedElements.add(firstValue);
                }
            }
            updatedElements.addAll(currentElements);

            int j = leftUp ? 0 : BOARD_SIZE - 1;
            while ((leftUp && j < BOARD_SIZE) || (!leftUp && j >= 0)) {

                int row = leftRight ? i : j;
                int column = leftRight ? j : i;

                if (updatedElements.isEmpty()) {
                    setTileValue(0, row, column);
                } else {
                    setTileValue(updatedElements.remove(), row, column);
                }

                if (leftUp) {
                    j++;

                } else {
                    j--;
                }
            }
        }

        boolean moved = !Arrays.deepEquals(oldBoard, board);

        if(emptyPositions.size() > 0 && moved) {
            var value = random.nextInt(100) < 90 ? 2 : 4;
            var randomIndex = random.nextInt(emptyPositions.size());
            var randomPosition = emptyPositions.get(randomIndex);
            setTileValue(value, randomPosition.getRow() , randomPosition.getColumn());
        }

        return moved;
    }

    public void updateEmptyPositions() {
        emptyPositions.clear();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if(getTileValue(row, column) == 0) {
                    emptyPositions.add(new Position(row, column));
                }
            }
        }
    }

    public int getPoints() {
        return points;
    }

    public boolean hasValidMoves() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                var value = getTileValue(row, column);
                if(value == 0) {
                    return true;
                }

                if((isValidBoardPosition(row, column - 1) && getTileValue(row, column - 1) == value)
                        || (isValidBoardPosition(row, column + 1) && getTileValue(row, column + 1) == value)
                        || (isValidBoardPosition(row + 1, column) && getTileValue(row + 1, column) == value)
                        || (isValidBoardPosition(row - 1, column) && getTileValue(row - 1, column) == value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains2048() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if(getTileValue(row, column) == 2048) {
                    return true;
                }
            }
        }
        return false;
    }
}
