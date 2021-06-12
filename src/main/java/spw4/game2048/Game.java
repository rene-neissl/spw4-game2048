package spw4.game2048;

import java.util.Random;

public class Game {

    public Board board;
    private int moves;

    public Game() {
        board = new Board();
        Board.random = new Random(1234);
    }

    public int getScore() {
        return board.getPoints();
    }

    public int getMoves() {
        return moves;
    }

    public boolean isOver() {
        return !board.hasValidMoves() || isWon();
    }

    public boolean isWon() {
        return board.contains2048();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Moves: ");
        builder.append(getMoves());

        builder.append("\tScore: ");
        builder.append(getScore());

        builder.append("\n");
        builder.append(board);

        return builder.toString();
    }

    public void initialize() {
        board.initialize();
    }

    public void move(Direction direction) {
        if(board.move(direction)) {
            moves++;
        }
    }
}
