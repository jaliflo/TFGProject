package com.example.franciscojavier.tfgproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AcceptConDialog extends DialogFragment {

    public interface AcceptConDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AcceptConDialogListener listener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            listener = (AcceptConDialogListener) activity;
        }catch (ClassCastException e){

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to chat with "+getArguments().getString("username")+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(AcceptConDialog.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(AcceptConDialog.this);
                    }
                });
        return builder.create();
    }
}
