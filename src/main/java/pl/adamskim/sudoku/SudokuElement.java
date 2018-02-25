package pl.adamskim.sudoku;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.adamskim.sudoku.Board.BOARD_SIZE;

public class SudokuElement implements Serializable {
    private int value;
    private List<Integer> possibleValues;

    public SudokuElement() {
        possibleValues = IntStream.range(1, BOARD_SIZE + 1).boxed().collect(Collectors.toList());
    }

    public SudokuElement(int value) {
        if (value < 1 && value > 9) {
            throw new RuntimeException("Wrong value!");
        }
        this.value = value;
        possibleValues = Collections.emptyList();
    }

    boolean filled() {
        return value != 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Integer> getPossibleValues() {
        return possibleValues;
    }
}
