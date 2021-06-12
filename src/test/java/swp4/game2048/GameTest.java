package swp4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spw4.game2048.Board;
import spw4.game2048.Direction;
import spw4.game2048.Game;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameTest {

    private Game game;
    @Mock
    private Random random;

    @BeforeEach
    public void init() {
        game = new Game();
    }

    @Test
    public void getScore_whenNewGame_returnsZero() {
        assertEquals(0, game.getScore());
    }

    @Test
    public void toString_whenNewGame_returnsNonEmptyString() {
        assertFalse(game.toString().isBlank());
    }

    @Test
    public void isOver_whenNewGame_returnsFalse() {
        assertFalse(game.isOver());
    }

    @Test
    public void isWon_whenNewGame_returnsFalse() {
        assertFalse(game.isWon());
    }

    @Test
    public void initialize_whenNewGame_resetsGameStats() {
        game.initialize();
        assertEquals(0, game.getMoves());
        assertEquals(0, game.getScore());
    }

    @Test
    public void move_incrementsMoves() {
        game.initialize();
        game.move(Direction.left);
        assertEquals(1, game.getMoves());
        game.move(Direction.up);
        assertEquals(2, game.getMoves());
        game.move(Direction.right);
        assertEquals(3, game.getMoves());
    }

    @Test
    public void getScore_afterMergingTwoTiles_returnsCorrectScore() {
        when(random.nextInt(anyInt()))
                .thenReturn(0).thenReturn(0).thenReturn(0)
                .thenReturn(0).thenReturn(1).thenReturn(0);

        Board.random = random;

        game.initialize();
        game.move(Direction.down);

        assertEquals(4, game.getScore());
    }

    @Test
    public void isOver_onFullBoardWithoutMoves_returnsTrue() {

        game.board = spy(Board.class);
        when(game.board.hasValidMoves()).thenReturn(false);

        assertTrue(game.isOver());
    }

    @Test
    public void isWon_with2048Tile_returnsTrue() {

        game.board = spy(Board.class);
        when(game.board.contains2048()).thenReturn(true);

        assertTrue(game.isWon());
    }

    @Test
    public void isOver_with2048Tile_returnsTrue() {

        game.board = spy(Board.class);
        when(game.board.contains2048()).thenReturn(true);

        assertTrue(game.isOver());
    }
}
