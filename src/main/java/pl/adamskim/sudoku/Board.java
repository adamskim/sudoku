package pl.adamskim.sudoku;

import javafx.util.Pair;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.adamskim.sudoku.SudokuRow.SIZE;

public class Board implements Serializable {

    private List<SudokuRow> rows;

    public Board() {
        rows = Stream.generate(SudokuRow::new).limit(SIZE).collect(Collectors.toList());
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
        List<SudokuElement> notFilledElements = getSudokuElementStream()
                .filter(e -> !e.filled()).collect(Collectors.toList());
        for (SudokuElement sudokuElement : notFilledElements) {
            List<Integer> possibleValues = sudokuElement.getPossibleValues();
            Iterator<Integer> possibleValuesIterator = possibleValues.iterator();
            while (possibleValuesIterator.hasNext()) {
                Integer possibleValue = possibleValuesIterator.next();
                if (toEliminate(possibleValue, findRowAndColumn(sudokuElement))) {
                    possibleValuesIterator.remove();
                    actionPerformed = true;
                }
            }
            if (possibleValues.isEmpty()) {
                throw new BoardNotResolvableException();
            }
            if (possibleValues.size() == 1) {
                sudokuElement.setValue(possibleValues.get(0));
            }
        }
        return actionPerformed;
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


    public boolean toEliminate(int value, Pair<Integer, Integer> coordinates) {
        int row = coordinates.getKey();
        int column = coordinates.getValue();
        return findInRow(value, row) || findInColumn(value, column) || findInBlock(value, column, row);
    }

    private boolean findInRow(int value, int row) {
        return rows.get(row).hasValue(value);
    }

    private boolean findInColumn(int value, int column) {
        return rows.stream().map(row -> row.getElements().get(column)).filter(e -> e.getValue() == value).findAny().isPresent();
    }

    private boolean findInBlock(int value, int column, int row) {
        int blockX = column / 3;
        int blockY = row / 3;
        for (int y = blockY * 3;  y <(blockY + 1) * 3; y++) {
            for (int x = blockX * 3;  x <(blockX + 1) * 3; x++) {
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
