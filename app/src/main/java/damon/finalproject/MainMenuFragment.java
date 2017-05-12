package damon.finalproject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Damon on 5/9/2017.
 */

public class MainMenuFragment extends Fragment implements View.OnClickListener {

    private OnMenuSelectListener mListener;
    private Button newGameButton, showStatsButton, showInstructionsButton;

    public interface OnMenuSelectListener {
        void onMenuSelect(View view);
    }

    @Override
    public void onActivityCreated(Bundle saveStateInstance) {
        super.onActivityCreated(saveStateInstance);
        newGameButton = (Button) getActivity().findViewById(R.id.button_new_game);
        showStatsButton = (Button) getActivity().findViewById(R.id.button_show_statistics);
        showInstructionsButton = (Button) getActivity().findViewById(R.id.button_instructions);

        newGameButton.setOnClickListener(this);
        showStatsButton.setOnClickListener(this);
        showInstructionsButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMenuSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMenuSelectListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return layoutInflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onClick(View view) {
        mListener.onMenuSelect(view);
    }
}
