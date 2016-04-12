package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.codeoregonapp.patrickleonard.tempestatibus.R;

/**
 * This class is a custom DialogFragment for presenting errors to the user
 * Created by Patrick Leonard on 10/18/2015.
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_title))
                .setMessage(context.getString(R.string.error_message))
                .setPositiveButton(R.string.error_ok_button_text, null);
        return builder.create();
    }
}
