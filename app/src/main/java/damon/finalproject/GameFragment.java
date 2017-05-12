package damon.finalproject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Damon on 5/9/2017.
 */

public class GameFragment extends Fragment implements View.OnClickListener {

    private OnBoardClickListener mListener;
    private ArrayList<Button> gridSpaces;

    public interface OnBoardClickListener {
        void onBoardClick(int x, int y);
    }

    @Override
    public void onActivityCreated(Bundle savedStateInstance) {
        super.onActivityCreated(savedStateInstance);
        gridSpaces = new ArrayList<>();
        ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(R.id.game_board);
        for(int i = 0; i < 36; i++) {
            gridSpaces.add((Button) viewGroup.getChildAt(i));
            gridSpaces.get(i).setOnClickListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveStateInstance) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onClick(View view) {
        int x = 0, y = 0;
        for(int i = 0; i < 36; i++) {
            if(gridSpaces.get(i) == view) {
                x = Math.floorDiv(i, 6);
                y = i % 6;
            }
        }
        mListener.onBoardClick(x, y);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBoardClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBoardClickListener");
        }
    }

    public void drawBoard(String[][] board, boolean badMove) {
        if(badMove) {
            Toast.makeText(getContext(), "That move is not valid. Try again.", Toast.LENGTH_LONG).show();
        }
        else {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (board[i][j].equals("W")) {
                        gridSpaces.get(i*6+j).setForeground(getResources().getDrawable(R.drawable.game_piece_white));
                    } else if (board[i][j].equals("B")) {
                        gridSpaces.get(i*6+j).setForeground(getResources().getDrawable(R.drawable.game_piece_black));
                    }
                }
            }
        }
    }
}
