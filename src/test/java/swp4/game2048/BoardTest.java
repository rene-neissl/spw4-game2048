package swp4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spw4.game2048.Board;
import spw4.game2048.Direction;
import spw4.game2048.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardTest {

    Board board;
    @Mock
    Random random;

    @BeforeEach
    public void init() {
        board = new Board();
        Board.random = new Random();
    }

    @ParameterizedTest(name = "Value {0}")
    @ValueSource(ints = {2, 4, 8})
    public void getTile_withValidValuesAndPosition_returnsCorrectValue(int value) {
        board.setTileValue(value, 0, 0);
        assertEquals(value, board.getTileValue(0, 0));
    }

    @ParameterizedTest(name = "Row {0} - Col {1}")
    @CsvSource({"0, -1", "-1, 0" , "4, 0", "0, 4"})
    public void setTile_withInvalidPositions_throwsException(int row, int col) {
        assertThrows(IllegalArgumentException.class, () -> board.setTileValue(2, row, col));
    }

    @ParameterizedTest(name = "Value {0}")
    @ValueSource(ints = {1, 3, -2})
    public void setTile_withInvalidValues_throwsException(int value) {
        assertThrows(IllegalArgumentException.class,() -> board.setTileValue(value,0, 0));
    }

    @ParameterizedTest(name = "Row {0} - Col {1}")
    @CsvSource({"0, -1", "-1, 0" , "6, 0", "5, -4"})
    public void getTile_withInvalidPositions_throwsException(int row, int col) {
        assertThrows(IllegalArgumentException.class, () -> board.getTileValue(row, col));
    }

    @Test
    public void initialize_onNewBoard_setsTwoRandomTiles() {
        when(random.nextInt(anyInt()))
                .thenReturn(0).thenReturn(2).thenReturn(0)
                .thenReturn(99).thenReturn(3).thenReturn(1);

        Board.random = random;

        board.initialize();
        assertAll(
                () -> assertEquals(2, board.getTileValue(2, 0)),
                () -> assertEquals(4, board.getTileValue(3, 1))
        );
    }

    @Test
    public void initialize_withTwoEqualRandomPositions_triesAgain() {
        when(random.nextInt(anyInt()))
                .thenReturn(0).thenReturn(2).thenReturn(0)
                .thenReturn(95).thenReturn(2).thenReturn(0)
                .thenReturn(99).thenReturn(3).thenReturn(1);

        Board.random = random;

        board.initialize();
        assertAll(
                () -> assertEquals(2, board.getTileValue(2, 0)),
                () -> assertEquals(4, board.getTileValue(3, 1))
        );
    }

    @Nested
    class MoveTests {

        @BeforeEach
        void init() {
            board.emptyPositions = mock(ArrayList.class);
            lenient().when(board.emptyPositions.add(any())).thenReturn(true);
            lenient().when(board.emptyPositions.size()).thenReturn(1);
            lenient().when(board.emptyPositions.get(anyInt())).thenReturn(new Position(1, 0));
        }

        @Test
        public void moveRight_singleTileOnEmptyBoard_movesTile() {

            board.setTileValue(2, 0, 0);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(0, board.getTileValue(0, 2)),
                    () -> assertEquals(2, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveDown_singleTileOnEmptyBoard_movesTile() {

            when(board.emptyPositions.get(anyInt())).thenReturn(new Position(0, 1));

            board.setTileValue(2, 0, 0);
            board.move(Direction.down);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(1, 0)),
                    () -> assertEquals(0, board.getTileValue(2, 0)),
                    () -> assertEquals(2, board.getTileValue(3, 0))
            );
        }

        @Test
        public void moveUp_singleTileOnEmptyBoard_movesTile() {

            when(board.emptyPositions.get(anyInt())).thenReturn(new Position(0, 1));

            board.setTileValue(2, 3, 0);
            board.move(Direction.up);
            assertAll(
                    () -> assertEquals(2, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(1, 0)),
                    () -> assertEquals(0, board.getTileValue(2, 0)),
                    () -> assertEquals(0, board.getTileValue(3, 0))
            );
        }

        @Test
        public void moveLeft_singleTileOnEmptyBoard_movesTile() {
            board.setTileValue(2, 0, 3);
            board.move(Direction.left);
            assertAll(
                    () -> assertEquals(2, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(0, board.getTileValue(0, 2)),
                    () -> assertEquals(0, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_twoTilesOnEmptyBoard_movesTilesWithMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(2, 0, 1);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(0, board.getTileValue(0, 2)),
                    () -> assertEquals(4, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_threeTilesOnEmptyBoard_movesTilesWithOnlyTwoMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(2, 0, 1);
            board.setTileValue(2, 0, 3);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(2, board.getTileValue(0, 2)),
                    () -> assertEquals(4, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_fourTilesOnEmptyBoard_movesTilesWithOnlyTwoMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(2, 0, 1);
            board.setTileValue(2, 0, 2);
            board.setTileValue(2, 0, 3);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(4, board.getTileValue(0, 2)),
                    () -> assertEquals(4, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_fourTilesOnEmptyBoard_movesTilesWithoutMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(4, 0, 1);
            board.setTileValue(2, 0, 2);
            board.setTileValue(4, 0, 3);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(2, board.getTileValue(0, 0)),
                    () -> assertEquals(4, board.getTileValue(0, 1)),
                    () -> assertEquals(2, board.getTileValue(0, 2)),
                    () -> assertEquals(4, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_threeTilesOnEmptyBoard_movesTilesWithMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(2, 0, 1);
            board.setTileValue(4, 0, 2);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(4, board.getTileValue(0, 2)),
                    () -> assertEquals(4, board.getTileValue(0, 3))
            );
        }

        @Test
        public void moveRight_fourTilesOnEmptyBoard_movesTilesWithTwoMerging() {
            board.setTileValue(2, 0, 0);
            board.setTileValue(2, 0, 1);
            board.setTileValue(4, 0, 2);
            board.setTileValue(4, 0, 3);
            board.move(Direction.right);
            assertAll(
                    () -> assertEquals(0, board.getTileValue(0, 0)),
                    () -> assertEquals(0, board.getTileValue(0, 1)),
                    () -> assertEquals(4, board.getTileValue(0, 2)),
                    () -> assertEquals(8, board.getTileValue(0, 3))
            );
        }
    }

    @Test
    public void getPoints_twoTilesWithSameValuesOnEmptyBoard_mergingIncrementsPoints() {
        board.setTileValue(2, 0, 0);
        board.setTileValue(2, 0, 1);
        board.move(Direction.right);
        assertEquals(4, board.getPoints());
    }

    @Test
    public void getPoints_fourTilesWithSameValuesOnEmptyBoard_mergingIncrementsPoints() {
        board.setTileValue(2, 0, 0);
        board.setTileValue(2, 0, 1);
        board.setTileValue(4, 0, 2);
        board.setTileValue(4, 0, 3);
        board.move(Direction.right);
        assertEquals(12, board.getPoints());
    }

    @Test
    public void move_withFifteenTiles_addsNewTile() {

        Board.random = new Random();

        board.setTileValue(2, 0, 0);
        board.setTileValue(4, 0, 1);
        board.setTileValue(2, 0, 2);
        board.setTileValue(4, 0, 3);

        board.setTileValue(4, 1, 0);
        board.setTileValue(2, 1, 1);
        board.setTileValue(4, 1, 2);
        board.setTileValue(2, 1, 3);

        board.setTileValue(2, 2, 0);
        board.setTileValue(4, 2, 1);
        board.setTileValue(2, 2, 2);
        board.setTileValue(4, 2, 3);

        board.setTileValue(4, 3, 0);
        board.setTileValue(2, 3, 1);
        board.setTileValue(4, 3, 2);
        // Last cell empty

        board.move(Direction.right);

        assertTrue(board.getTileValue(3, 0) > 0);
    }

    @Test
    public void validMoves_onEmptyBoard_returnsTrue() {
        assertTrue(board.hasValidMoves());
    }

    @Test
    public void validMoves_onFullBoardWithoutMoves_returnsFalse() {

        board.setTileValue(2, 0, 0);
        board.setTileValue(4, 0, 1);
        board.setTileValue(2, 0, 2);
        board.setTileValue(4, 0, 3);

        board.setTileValue(4, 1, 0);
        board.setTileValue(2, 1, 1);
        board.setTileValue(4, 1, 2);
        board.setTileValue(2, 1, 3);

        board.setTileValue(2, 2, 0);
        board.setTileValue(4, 2, 1);
        board.setTileValue(2, 2, 2);
        board.setTileValue(4, 2, 3);

        board.setTileValue(4, 3, 0);
        board.setTileValue(2, 3, 1);
        board.setTileValue(4, 3, 2);
        board.setTileValue(2, 3, 3);

        assertFalse(board.hasValidMoves());
    }

    @Test
    public void validMoves_onFullBoardWithMoves_returnsTrue() {

        board.setTileValue(2, 0, 0);
        board.setTileValue(4, 0, 1);
        board.setTileValue(2, 0, 2);
        board.setTileValue(4, 0, 3);

        board.setTileValue(4, 1, 0);
        board.setTileValue(2, 1, 1);
        board.setTileValue(4, 1, 2);
        board.setTileValue(2, 1, 3);

        board.setTileValue(2, 2, 0);
        board.setTileValue(4, 2, 1);
        board.setTileValue(2, 2, 2);
        board.setTileValue(4, 2, 3);

        board.setTileValue(4, 3, 0);
        board.setTileValue(2, 3, 1);
        board.setTileValue(2, 3, 2);
        board.setTileValue(2, 3, 3);

        assertTrue(board.hasValidMoves());
        board.move(Direction.left);
        assertTrue(board.hasValidMoves());
    }

    @Test
    public void contains2048_with2048Tile_returnsTrue() {
        board.setTileValue(2048, 0, 0);
        assertTrue(board.contains2048());
    }

    @Test
    public void contains2048_without2048Tile_returnsFalse() {
        board.setTileValue(2, 0, 0);
        assertFalse(board.contains2048());
    }

    @Test
    public void moveRight_withoutTileMoving_ReturnsSameEmptyPositionCount() {
        board.setTileValue(2, 0, 3);
        var previousEmptyPositionCount = board.emptyPositions.size();
        board.move(Direction.right);
        assertEquals(previousEmptyPositionCount, board.emptyPositions.size());
    }
}
