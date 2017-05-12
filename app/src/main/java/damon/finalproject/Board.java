package damon.finalproject;

/**
 * Created by Damon on 5/11/2017.
 */

public class Board {

    private String[][] gameBoard;
    private int moveX;
    private int moveY;

    public String[][] getGameBoard() {
        return this.gameBoard;
    }

    public void setGameBoard(String[][] board) {
        this.gameBoard = board;
    }

    public int getMoveX() {
        return this.moveX;
    }

    public void setMoveX(int newX) {
        this.moveX = newX;
    }

    public int getMoveY() {
        return this.moveY;
    }

    public void setMoveY(int newY) {
        this.moveY = newY;
    }
}
