package damon.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Damon on 5/10/2017.
 */

public class ChooseColorDialogFragment extends DialogFragment {

    public interface DialogListener {
        void onNegativeClick(ChooseColorDialogFragment colorDialogFragment);
        void onColorClick(ChooseColorDialogFragment colorDialogFragment, int which);
    }

    private DialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose A Color")
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onColorClick(ChooseColorDialogFragment.this, which);
                    }
                })
                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClick(ChooseColorDialogFragment.this);
                    }
                });

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
