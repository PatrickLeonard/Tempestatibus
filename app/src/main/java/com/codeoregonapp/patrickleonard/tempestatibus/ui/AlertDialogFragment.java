package com.codeoregonapp.patrickleonard.tempestatibus.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.forecastRetrievalUtility.ForecastRetrievalServiceConstants;

/**
 * This class is a custom DialogFragment for presenting errors to the user
 * Created by Patrick Leonard on 10/18/2015.
 */
public class AlertDialogFragment extends DialogFragment {

    public static final String ALERT_TITLE = "ALERT_TITLE";
    public static final String ALERT_MESSAGE = "ALERT_MESSAGE";
    public static final String ERROR_TYPE = "ERROR_TYPE";
    private String mTitle;
    private String mMessage;
    private int mErrorType;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getTitle() { return mTitle; }
    
    public void setTitle(String title) {
        mTitle = title;
    }

    public int getErrorType() {
        return mErrorType;
    }

    public void setErrorType(int errorType) {
        this.mErrorType = errorType;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        setTitle(args.getString(ALERT_TITLE));
        setMessage(args.getString(ALERT_MESSAGE));
        setErrorType(args.getInt(ERROR_TYPE));
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        switch(getErrorType()) {
            case ForecastRetrievalServiceConstants.NETWORK_FAILURE_RESULT: {
                return createWirelessSettingsDialog();
            }
            case ForecastRetrievalServiceConstants.LOCATION_FAILURE_RESULT:{
                return createLocationSettingsDialog();
            }
            default: return getNonFixableErrorDialog();
        }
    }

    public Dialog createLocationSettingsDialog()  {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTitle())
                .setMessage(getMessage())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity)getActivity();
                            mainActivity.startLocationSettingsActivity();
                            mainActivity.setRefusedSettings(false);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity)getActivity();
                            Toast.makeText(getActivity(), mTitle, Toast.LENGTH_SHORT).show();
                            mainActivity.setRefusedSettings(true);
                        }

                    }
                });
        return builder.create();
    }

    public Dialog createWirelessSettingsDialog()  {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTitle())
                .setMessage(getMessage())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity)getActivity();
                            mainActivity.startWirelessSettingsActivity();
                            mainActivity.setRefusedSettings(false);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity)getActivity();
                            Toast.makeText(getActivity(), mTitle, Toast.LENGTH_SHORT).show();
                            mainActivity.setRefusedSettings(true);
                        }

                    }
                });
        return builder.create();
    }

    private Dialog getNonFixableErrorDialog() {
        Context context = getActivity();
        if(mTitle == null || mTitle.equals("")) {
            mTitle = context.getString(R.string.error_title);
        }
        if(mMessage == null || mMessage.equals("")) {
            mMessage = context.getString(R.string.error_message);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton(R.string.error_ok_button_text, null);
        return builder.create();
    }
}
