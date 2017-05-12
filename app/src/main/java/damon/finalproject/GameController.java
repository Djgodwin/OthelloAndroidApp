package damon.finalproject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

public class GameController extends Fragment {

    private String humanColor;
    private String aiColor;
    private int turn;
    private String[][] board;
    private Board gameBoard;
    private OnMoveMadeListener mListener;

    public interface OnMoveMadeListener {
        void onMoveMade(String[][] board, boolean badMove, String plyColor, String oppColor);
    }

    public int getTurn() {
        return this.turn;
    }

    public void setHumanColor(String newColor) {
        this.humanColor = newColor;
    }

    public void setAiColor(String newColor) {
        this.aiColor = newColor;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMoveMadeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMoveMadeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.board = new String[6][6];
        this.gameBoard = new Board();

        initBoard(this.board);
        this.gameBoard.setGameBoard(this.board);
        if(aiColor.equals("W")) {
            turn = 1;
            aiMakeMove();
        }
    }

    public void onHumanMoveMade(int x, int y) {
        ArrayList<Integer> piecesToFlip = new ArrayList<>();
        if(checkMove(gameBoard.getGameBoard(), x, y, humanColor, aiColor, piecesToFlip)) {
            makeMove(gameBoard.getGameBoard(), x, y, humanColor, piecesToFlip);
            mListener.onMoveMade(board, false, aiColor, humanColor);
            aiMakeMove();
        }
        else {
            mListener.onMoveMade(board, true, aiColor, humanColor);
        }
    }

    /**
     * The code below was created for an introductory course in A.I., that I have previously taken.
     */

    public void aiMakeMove() {
        turn = 1;
        int[] bestMove = new int[3];
        ArrayList<Integer> piecesToFlip = new ArrayList<>();
        gameBoard.setGameBoard(board);
        decideMove(gameBoard, true, aiColor, humanColor, bestMove);
        checkMove(board, bestMove[0], bestMove[1], aiColor, humanColor, piecesToFlip);
        makeMove(board, bestMove[0], bestMove[1], aiColor, piecesToFlip);
        mListener.onMoveMade(board, false, humanColor, aiColor);
        turn = 0;
    }

    //Function initializes the board.
    public void initBoard(String[][] board) {
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if((i == 2 && j == 2) || (i == 3 && j == 3))
                    board[i][j] = "W";
                else if((i == 2 && j == 3) || (i == 3 && j == 2))
                    board[i][j] = "B";
                else
                    board[i][j] = "*";
            }
        }
    }

    //Function creates a copy of the board.
    public void copyBoard(String[][] board, String[][] newBoard) {
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
    }

    //Computer determines its move.
    public void decideMove(Board gameBoard, boolean isMax, String plyColor, String oppColor, int[] bestMove) {
		/*An array is used to hold the coordinates of the best move as well as a bit that indicates whether or not to terminate the iterative deepening search.
		  bestMove[0] holds the row number of the best move,
		  bestMove[1] holds the column number of the best move,
		  bestMove[2] holds the a 0 or a 1 to indicate whether or not to terminate the search.*/
        bestMove[2] = 0;
        int bestScore = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        //Continue calling the search algorithm until bestMove[2] is 1.
        for(int d = 0;; d++) {
            bestScore = AB_minimax(d, gameBoard, isMax, plyColor, oppColor, bestMove, alpha, beta);
            if(bestMove[2] == 1)
                break;
        }
    }

    //Function implements the minimax algorithm with alpha beta pruning.
    public int AB_minimax(int d, Board currBoard, boolean isMax, String plyColor, String oppColor, int[] bestMove, int alpha, int beta) {
        //Variable declarations.
        String swap;
        int bestScore = 0;
        ArrayList<Board> children = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();

        //If the depth is 0, return the heuristic score.
        if(d == 0) {
            return evalScore(currBoard, oppColor);
        }
        //Otherwise if a terminal node is reached, or if depth 3 is reached, set bestMove[2] = 1 and return the heuristic score.
        else if(gameOver(currBoard.getGameBoard(), plyColor, oppColor) || d == 3) {
            bestMove[2] = 1;
            return evalScore(currBoard, oppColor);
        }

        //Expand the current board to get all valid moves.
        expand(currBoard, plyColor, oppColor, children);

        //If the current player is the maximizing player.
        if(isMax) {
            //Switch which player is playing, for the next call to minimax.
            swap = plyColor;
            plyColor = oppColor;
            oppColor = swap;
            bestScore = Integer.MIN_VALUE;
            //Loop through all children, calling minimax until it's time to terminate.
            for(int i = 0; i < children.size(); i++) {
                //Compare the previous best score with the score that is returned from the minimax call.
                //The max of these is added to an ArrayList of scores.
                scores.add(Math.max(bestScore, AB_minimax(d - 1, children.get(i), false, plyColor, oppColor, bestMove, alpha, beta)));
                //Alpha is found by taking the max of the best score so far, the previously found alpha value.
                alpha = Math.max(alpha, scores.get(i));
                //The search is cutoff if the beta value is no better than the alpha value.
                if(beta <= alpha)
                    break;
            }
            //Loop through all the best scores stored and based on the which value is highest, get the corresponding move.
            for(int i = 0; i < scores.size(); i++) {
                if(scores.get(i) > bestScore) {
                    bestScore = scores.get(i);
                    bestMove[0] = children.get(i).getMoveX();
                    bestMove[1] = children.get(i).getMoveY();
                }
            }
            return bestScore;
        }
        //If the current player is the minimizing player.
        else {
            //Switch which player is playing, for the next call to minimax.
            swap = plyColor;
            plyColor = oppColor;
            oppColor = swap;
            bestScore = Integer.MAX_VALUE;
            //Loop through all children, calling minimax until it's time to terminate.
            for(int i = 0; i < children.size(); i++) {
                //Compare the previous best score with the score that is returned from the minimax call.
                //The minimum of these is added to an ArrayList of scores.
                scores.add(Math.min(bestScore, AB_minimax(d - 1, children.get(i), true, plyColor, oppColor, bestMove, alpha, beta)));
                //Beta is found by taking the minimum of the best score so far, the previously found beta value.
                beta = Math.min(beta, scores.get(i));
                //The search is cutoff if the beta value is no better than the alpha value.
                if(beta <= alpha)
                    break;
            }
            //Loop through all the best scores stored and based on the which value is highest, get the corresponding move.
            for(int i = 0; i < scores.size(); i++) {
                if(scores.get(i) < bestScore) {
                    bestScore = scores.get(i);
                    bestMove[0] = children.get(i).getMoveX();
                    bestMove[1] = children.get(i).getMoveY();
                }
            }
            return bestScore;
        }
    }

    //Function expands a board state.
    public void expand(Board currBoard, String plyColor, String oppColor, ArrayList<Board> children) {
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                Board newBoard = new Board();
                ArrayList<Integer> piecesToFlip = new ArrayList<Integer>();
                String[][] child = new String[6][6];
                copyBoard(currBoard.getGameBoard(), child);
                if(checkMove(child, i, j, plyColor, oppColor, piecesToFlip)) {
                    makeMove(child, i, j, plyColor, piecesToFlip);
                    newBoard.setGameBoard(child);
                    newBoard.setMoveX(i);
                    newBoard.setMoveY(j);
                    children.add(newBoard);
                }
            }
        }
    }

    //Function returns a heuristic score to be used in minimax.
    //The heuristic score is calculated by counting the number of pieces on the board for the current player.
    public int evalScore(Board currBoard, String oppColor) {
        int score = 0;
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if(currBoard.getGameBoard()[i][j].compareTo(oppColor) == 0)
                    score++;
            }
        }
        return score;
    }

    //Function checks if a move is valid.
    public boolean checkMove(String[][] board, int moveX, int moveY, String plyColor, String oppColor, ArrayList<Integer> piecesToFlip) {
        //Variable declarations
        int i = 0;
        int j = 0;
        int l = 0;
        boolean upLft = false;
        boolean up = false;
        boolean upRgt = false;
        boolean lft = false;
        boolean rgt = false;
        boolean dwnLft = false;
        boolean dwn = false;
        boolean dwnRgt = false;
        int numOfOpp = 0;

        //Make a copy of the board to check the move before actually making it.
        String[][] tmpBoard = new String[6][6];
        copyBoard(board, tmpBoard);

        //Check if the move will be outside of the board's bounds.
        if(moveX < 0 || moveX > 6)
            return false;
        if(moveY < 0 || moveY > 6)
            return false;

        //Check if the space is already occupied.
        if(tmpBoard[moveX][moveY].compareTo("*") != 0)
            return false;

        //If the space is on the board and not occupied, try the move to see if it's valid.
        tmpBoard[moveX][moveY] = plyColor;

        //Loop up and left if possible
        if(moveX - 1 >= 1 && moveY - 1 >= 1) {
            //If the adjacent space is occupied by the opposing player's piece, keep looking.
            if(tmpBoard[moveX - 1][moveY - 1].compareTo(oppColor) == 0) {
                i = moveX - 1;
                j = moveY - 1;
                while(i >= 0 && j >= 0) {
                    //If a space is occupied by the opposing player's piece, increment a counter.
                    if(tmpBoard[i][j].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(i);
                        piecesToFlip.add(j);
                    }
                    //If the space is empty, break the loop.
                    if(tmpBoard[i][j].compareTo("*") == 0)
                        break;
					/*If the space is occupied by the player's piece and there was at least one opposing piece,
					  then the move is valid and the loop is broken.*/
                    if(tmpBoard[i][j].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        upLft = true;
                        break;
                    }
                    i--;
                    j--;
                }
            }
        }
        //If the search was false, remove the pieces to flip that were previously found.
        if(!upLft) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look up if possible
        if(moveX - 1 >= 1) {
            if(tmpBoard[moveX - 1][moveY].compareTo(oppColor) == 0) {
                for(int k = moveX - 1; k >= 0; k--) {
                    if(tmpBoard[k][moveY].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(k);
                        piecesToFlip.add(moveY);
                    }
                    if(tmpBoard[k][moveY].compareTo("*") == 0)
                        break;
                    if(tmpBoard[k][moveY].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        up = true;
                        break;
                    }
                }
            }
        }
        if(!up) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Loop up and right if possible
        if(moveX - 1 >= 1 && moveY + 1 <= 4) {
            if(tmpBoard[moveX - 1][moveY + 1].compareTo(oppColor) == 0) {
                i = moveX - 1;
                j = moveY + 1;
                while(i >= 0 && j < 6) {
                    if(tmpBoard[i][j].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(i);
                        piecesToFlip.add(j);
                    }
                    if(tmpBoard[i][j].compareTo("*") == 0)
                        break;
                    if(tmpBoard[i][j].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        upRgt = true;
                        break;
                    }
                    i--;
                    j++;
                }
            }
        }
        if(!upRgt) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look left if possible
        if(moveY - 1 >= 1) {
            if(tmpBoard[moveX][moveY - 1].compareTo(oppColor) == 0) {
                for(int k = moveY - 1; k >= 0; k--) {
                    if(tmpBoard[moveX][k].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(moveX);
                        piecesToFlip.add(k);
                    }
                    if(tmpBoard[moveX][k].compareTo("*") == 0)
                        break;
                    if(tmpBoard[moveX][k].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        lft = true;
                        break;
                    }
                }
            }
        }
        if(!lft) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look right if possible
        if(moveY + 1 <= 4) {
            if(tmpBoard[moveX][moveY + 1].compareTo(oppColor) == 0) {
                for(int k = moveY + 1; k < 6; k++) {
                    if(tmpBoard[moveX][k].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(moveX);
                        piecesToFlip.add(k);
                    }
                    if(tmpBoard[moveX][k].compareTo("*") == 0)
                        break;
                    if(tmpBoard[moveX][k].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        rgt = true;
                        break;
                    }
                }
            }
        }
        if(!rgt) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look down and left if possible
        if(moveX + 1 <= 4 && moveY - 1 >= 1) {
            if(tmpBoard[moveX + 1][moveY - 1].compareTo(oppColor) == 0) {
                i = moveX + 1;
                j = moveY - 1;
                while(i < 6 && j >= 0) {
                    if(tmpBoard[i][j].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(i);
                        piecesToFlip.add(j);
                    }
                    if(tmpBoard[i][j].compareTo("*") == 0)
                        break;
                    if(tmpBoard[i][j].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        dwnLft = true;
                        break;
                    }
                    i++;
                    j--;
                }
            }
        }
        if(!dwnLft) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look down if possible
        if(moveX + 1 <= 4) {
            if(tmpBoard[moveX + 1][moveY].compareTo(oppColor) == 0) {
                for(int k = moveX + 1; k < 6; k++) {
                    if(tmpBoard[k][moveY].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(k);
                        piecesToFlip.add(moveY);
                    }
                    if(tmpBoard[k][moveY].compareTo("*") == 0)
                        break;
                    if(tmpBoard[k][moveY].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        dwn = true;
                        break;
                    }
                }
            }
        }
        if(!dwn) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        numOfOpp = 0;
        //Look down and right if possible
        if(moveX + 1 <= 4 && moveY + 1 <= 4) {
            if(tmpBoard[moveX + 1][moveY + 1].compareTo(oppColor) == 0) {
                i = moveX + 1;
                j = moveY + 1;
                while(i < 6 && j < 6) {
                    if(tmpBoard[i][j].compareTo(oppColor) == 0) {
                        numOfOpp++;
                        piecesToFlip.add(i);
                        piecesToFlip.add(j);
                    }
                    if(tmpBoard[i][j].compareTo("*") == 0)
                        break;
                    if(tmpBoard[i][j].compareTo(plyColor) == 0 && numOfOpp > 0) {
                        dwnRgt = true;
                        break;
                    }
                    i++;
                    j++;
                }
            }
        }
        if(!dwnRgt) {
            l = piecesToFlip.size() - 1;
            for(int n = 0; n < numOfOpp; n++) {
                piecesToFlip.remove(l);
                piecesToFlip.remove(l - 1);
                l = l - 2;
            }
        }
        //If there were valid moves return true.
        if(upLft || up || upRgt || lft || rgt || dwnLft || dwn || dwnRgt)
            return true;
        else
            return false;
    }

    //Function makes a move.
    public void makeMove(String[][] board, int moveX, int moveY, String plyColor, ArrayList<Integer> piecesToFlip) {
        int x;
        int y;
        board[moveX][moveY] = plyColor;
        for(int i = piecesToFlip.size() - 1; i > 0; i-=2) {
            y = piecesToFlip.get(i);
            x = piecesToFlip.get(i - 1);
            board[x][y] = plyColor;
        }
    }

    //Function determines if the game is over.
    public boolean gameOver(String[][] board, String plyColor, String oppColor) {
        ArrayList<Integer> noFlip = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if(checkMove(board, i, j, plyColor, oppColor, noFlip)) {
                    return false;
                }
            }
        }
        return true;
    }
}
