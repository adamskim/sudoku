package pl.adamskim.sudoku;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudokuRow implements Serializable {
    public static final int SIZE = 9;

    private List<SudokuElement> elements;

    public SudokuRow() {
        elements = Stream.generate(SudokuElement::new).limit(SIZE).collect(Collectors.toList());
    }

    public List<SudokuElement> getElements() {
        return elements;
    }

    public boolean hasValue(Integer pv) {
        return elements.stream().map(e -> e.getValue()).filter(v -> v == pv).findAny().isPresent();
    }
}
