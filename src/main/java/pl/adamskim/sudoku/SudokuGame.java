package pl.adamskim.sudoku;

import java.util.Stack;

public class SudokuGame {

    private BoardCreator boardCreator;
    private Board board;
    private Stack<Board> backBoards;

    public SudokuGame() {
        boardCreator = new InputCommandReader();
        board = boardCreator.createBoard();
        backBoards = new Stack<>();
    }

    public Board getBoard() {
        return board;
    }

    public boolean resolveSudoku() {
        boolean resolved = false;
        try {
              resolved = board.resolveBoard();
              if (!resolved) {
                  tryGuess();
                  resolveSudoku();
              }
        } catch (BoardNotResolvableException e) {
            if (backBoards.isEmpty()) {
                throw new RuntimeException("Sudoku not resolvable!");
            }
            board = backBoards.pop();
            resolveSudoku();
        }
        return true;
    }

    private void tryGuess() throws BoardNotResolvableException {
        SudokuElement firstNotFilled = board.findFirstNotFilled();
        if (firstNotFilled.getPossibleValues().isEmpty()) {
            throw new BoardNotResolvableException();
        }
        Integer ps = firstNotFilled.getPossibleValues().remove(0);
        backBoards.push(board.deepCopy());
        firstNotFilled.setValue(ps);
    }
}
