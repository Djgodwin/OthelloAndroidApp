package damon.finalproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Damon on 5/9/2017.
 */

public class GameActivity extends AppCompatActivity implements GameFragment.OnBoardClickListener, GameController.OnMoveMadeListener {

    private GameController gameController;
    private GameFragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle extras = getIntent().getExtras();

        FragmentManager fragmentManager = getFragmentManager();
        gameFragment = new GameFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_game_container, gameFragment)
                .commit();

        String humanColor = extras.getString("HUMAN_COLOR");
        String aiColor = extras.getString("AI_COLOR");

        gameController = new GameController();
        gameController.setHumanColor(humanColor);
        gameController.setAiColor(aiColor);
        fragmentManager.beginTransaction()
                .add(gameController, gameController.getTag())
                .commit();
    }

    @Override
    public void onBoardClick(int x, int y) {
        if(gameController.getTurn() == 1) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        else {
            gameController.onHumanMoveMade(x, y);
        }
    }

    @Override
    public void onMoveMade(String[][] board, boolean badMove, String plyColor, String oppColor) {
        gameFragment.drawBoard(board, badMove);
        int wCount = 0;
        int bCount = 0;
        if(gameController.gameOver(board, plyColor, oppColor)) {
            for(int i = 0; i < 6; i++) {
                for(int j = 0; j < 6; j++) {
                    if(board[i][j].equals("W")) {
                        wCount++;
                    }
                    else if(board[i][j].equals("B")) {
                        bCount++;
                    }
                }
            }
            FragmentManager fragmentManager = getFragmentManager();
            GameOverFragment gameOverFragment = new GameOverFragment();
            Bundle args = new Bundle();
            if(wCount < bCount) {
                args.putString("GAME_OVER_MESSAGE", "Black Wins!");
            }
            else if(wCount > bCount) {
                args.putString("GAME_OVER_MESSAGE", "White Wins!");
            }
            else {
                args.putString("GAME_OVER_MESSAGE", "It's a tie!");
            }
            gameOverFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_game_container, gameOverFragment)
                    .commit();
        }
    }
}
