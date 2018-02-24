package pl.adamskim.sudoku;

import java.util.*;

public class SudokuGameRunner {

    public static void main(String[] args) {

        boolean gameFinished = false;
        while(!gameFinished) {
            SudokuGame theGame = new SudokuGame();
            gameFinished = theGame.resolveSudoku();
            System.out.println(theGame.getBoard());
        }
    }
}

