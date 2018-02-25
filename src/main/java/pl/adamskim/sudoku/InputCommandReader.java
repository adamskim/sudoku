package pl.adamskim.sudoku;

import java.util.Scanner;

public class InputCommandReader implements BoardCreator {
    public static final String START_CMD = "SUDOKU";
    private Scanner scanner;
    private Board board;

    public InputCommandReader() {
        scanner = new Scanner(System.in);
        board = new Board();
    }

    @Override
    public Board createBoard() {
        String line;
        do {
            System.out.print(String.format("Give 3 number col row and value (for example: 147) or write %s to start: ",
                    START_CMD));
            line = scanner.nextLine();
            execute(line);
        } while (!line.toUpperCase().equals(START_CMD));
        return board;
    }

    private void execute(String line) {
        if (!line.toUpperCase().equals(START_CMD)) {
            char[] inputNumbers = line.toCharArray();
            board.setElement(toInt(inputNumbers[0]) - 1, toInt(inputNumbers[1]) - 1, toInt(inputNumbers[2]));
            System.out.println(board);
        }
    }

    private int toInt(char inputNumber) {
        return Integer.valueOf(String.valueOf(inputNumber));
    }
}
