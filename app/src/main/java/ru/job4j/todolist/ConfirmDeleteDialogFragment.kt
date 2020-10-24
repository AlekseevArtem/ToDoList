package ru.job4j.todolist

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmDeleteDialogFragment(private val dialog: Int) : DialogFragment() {
    private lateinit var callback: ConfirmDeleteDialogListener

    interface ConfirmDeleteDialogListener {
        fun onPositiveDialogClick(dialog: DialogFragment)
        fun onNegativeDialogClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(dialog)
                .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int -> callback.onPositiveDialogClick(this) }
                .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> callback.onNegativeDialogClick(this) }
                .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as ConfirmDeleteDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(String.format("%s must implement ConfirmDeleteDialogListener", context.toString()))
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback
    }
}