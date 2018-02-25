package pl.adamskim.sudoku;

import javafx.util.Pair;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board implements Serializable {

    public static final int BOARD_SIZE = 9;
    public static final int BLOCK_SIZE = 3;

    private List<SudokuRow> rows;

    public Board() {
        rows = Stream.generate(SudokuRow::new).limit(BOARD_SIZE).collect(Collectors.toList());
    }

    public boolean resolveBoard() throws BoardNotResolvableException {
        boolean progress;
        do {
            progress = iterateOverBoard();
        } while (progress);
        return isSolved();
    }

    private boolean iterateOverBoard() throws BoardNotResolvableException {
        boolean actionPerformed = false;
        List<SudokuElement> notFilledElements = getNotFilledElements();
        for (SudokuElement sudokuElement : notFilledElements) {
            List<Integer> possibleValues = sudokuElement.getPossibleValues();
            List<Integer> possibleValuesToEliminate = getPossibleValuesToEliminate(sudokuElement);
            possibleValues.removeAll(possibleValuesToEliminate);
            tryFillElement(sudokuElement);
            actionPerformed = actionPerformed || !possibleValuesToEliminate.isEmpty();
        }
        return actionPerformed;
    }

    private List<SudokuElement> getNotFilledElements() {
        return getSudokuElementStream().filter(e -> !e.filled()).collect(Collectors.toList());
    }

    private void tryFillElement(SudokuElement sudokuElement) throws BoardNotResolvableException {
        List<Integer> possibleValues = sudokuElement.getPossibleValues();
        if (possibleValues.isEmpty()) {
            throw new BoardNotResolvableException();
        }
        if (possibleValues.size() == 1) {
            sudokuElement.setValue(possibleValues.get(0));
        }
    }

    private List<Integer> getPossibleValuesToEliminate(SudokuElement sudokuElement) {
        return sudokuElement.getPossibleValues().stream().filter(pv -> toEliminate(pv, findRowAndColumn(sudokuElement)))
                .collect(Collectors.toList());
    }

    private Stream<SudokuElement> getSudokuElementStream() {
        return rows.stream().flatMap(row -> row.getElements().stream());
    }

    private Pair<Integer, Integer> findRowAndColumn(SudokuElement sudokuElement) {
        int rowNumber = 0;
        for (SudokuRow row : rows) {
            if (row.getElements().contains(sudokuElement)) {
                int colNumber = row.getElements().indexOf(sudokuElement);
                return new Pair(rowNumber, colNumber);
            }
            rowNumber++;
        }
        return null;
    }

    private boolean isSolved() {
        return !getSudokuElementStream().filter(e -> !e.filled()).findFirst().isPresent();
    }

    public SudokuElement findFirstNotFilled() {
        return getSudokuElementStream().filter(e -> e.getValue() == 0).findFirst()
                .orElseThrow(()-> new RuntimeException("Sudoku already solved!"));
    }


    public boolean toEliminate(int value, Pair<Integer, Integer> elementLocation) {
        int row = elementLocation.getKey();
        int column = elementLocation.getValue();
        return findInRow(value, row) || findInColumn(value, column) || findInBlock(value, column, row);
    }

    private boolean findInRow(int value, int row) {
        return rows.get(row).hasValue(value);
    }

    private boolean findInColumn(int value, int column) {
        return rows.stream().map(row -> row.getElements().get(column)).filter(e -> e.getValue() == value).findAny().isPresent();
    }

    private boolean findInBlock(int value, int column, int row) {
        int blockX = column / BLOCK_SIZE;
        int blockY = row / BLOCK_SIZE;
        for (int y = blockY * BLOCK_SIZE;  y <(blockY + 1) * BLOCK_SIZE; y++) {
            for (int x = blockX * BLOCK_SIZE;  x <(blockX + 1) * BLOCK_SIZE; x++) {
                if (rows.get(y).getElements().get(x).getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public Board deepCopy() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return (Board) objInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String toString() {
        String board = "";
        for (int y = 0; y < rows.size(); y++) {
            SudokuRow row = rows.get(y);
            for (int x = 0; x < row.getElements().size(); x++) {
                int value = row.getElements().get(x).getValue();
                board += String.format("[%s]", value == 0 ? " " : value);
            }
            board += "\n";
        }
        return board;
    }
}
