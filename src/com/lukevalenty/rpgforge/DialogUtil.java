package com.lukevalenty.rpgforge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogUtil {
    public static void showStringPrompt(
        final Activity activity,
        final String textFieldHint,
        final String positiveButtonText,
        final String negativeButtonText,
        final StringPromptListener listener
    ) {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(activity);
        
        final LayoutInflater inflater = 
            activity.getLayoutInflater();
        
        final View view = 
            inflater.inflate(R.layout.new_project_dialog, null);
        
        final TextView stringValueField = 
            (TextView) view.findViewById(R.id.projectName);
        
        stringValueField.setHint(textFieldHint);
        
        builder.setView(view)
           .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   final String stringValue = 
                       stringValueField.getText().toString().trim();
                   
                   listener.onAccept(stringValue);
               }
           })
           .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // do nothing
               }
           });      
        
        builder.create().show();
    }
    
    public static interface StringPromptListener {
        public void onAccept(final String value);
    }
}
