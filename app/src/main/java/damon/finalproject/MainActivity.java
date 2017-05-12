package damon.finalproject;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements
        MainMenuFragment.OnMenuSelectListener,
        ChooseColorDialogFragment.DialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FragmentManager fragmentManager = getFragmentManager();
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_main_container, mainMenuFragment)
                .commit();
    }

    @Override
    public void onMenuSelect(View view) {
        switch (view.getId()) {
            case R.id.button_new_game:
                showDialog();
                break;
            case R.id.button_show_statistics:
                break;
            case R.id.button_instructions:
                showInstructions();
                break;
            default:
                break;
        }
    }

    public void showDialog() {
        DialogFragment dialog = new ChooseColorDialogFragment();
        String tag = dialog.getTag();
        dialog.show(getFragmentManager(), tag);
    }

    public void showInstructions() {
        FragmentManager fragmentManager = getFragmentManager();
        InstructionsFragment instructionsFragment = new InstructionsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_main_container, instructionsFragment)
                .commit();
    }

    @Override
    public void onColorClick(ChooseColorDialogFragment dialogFragment, int which) {
        Intent intent = new Intent(this, GameActivity.class);
        String hcolor;
        String aicolor;
        if(which == 0) {
            hcolor = "W";
            aicolor = "B";
        }
        else {
            hcolor = "B";
            aicolor = "W";
        }
        intent.putExtra("HUMAN_COLOR", hcolor);
        intent.putExtra("AI_COLOR", aicolor);
        startActivity(intent);
    }

    @Override
    public void onNegativeClick(ChooseColorDialogFragment dialogFragment) {
        dialogFragment.dismiss();
    }
}
