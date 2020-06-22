package ru.job4j.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialogFragment extends DialogFragment {
    private ConfirmDeleteDialogListener callback;
    private int dialog;

    public interface ConfirmDeleteDialogListener {
        void onPositiveDialogClick(DialogFragment dialog);
        void onNegativeDialogClick(DialogFragment dialog);
    }

    public ConfirmDeleteDialogFragment(int dialog){
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(dialog)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> callback.onPositiveDialogClick(this))
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> callback.onNegativeDialogClick(this))
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("%s must implement ConfirmDeleteDialogListener", context.toString()));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}