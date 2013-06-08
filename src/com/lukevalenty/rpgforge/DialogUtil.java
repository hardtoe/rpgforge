package com.lukevalenty.rpgforge;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
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
            inflater.inflate(R.layout.new_project_layout, null);
        
        final TextView stringValueField = 
            (TextView) view.findViewById(R.id.projectName);
        
        stringValueField.addTextChangedListener(new TextWatcher() {
            private boolean myEdit = false;
            
            @Override
            public void onTextChanged(
                final CharSequence s, 
                final int start, 
                final int before, 
                final int count
            ) {
                // do nothing
            }
            
            @Override
            public void beforeTextChanged(
                final CharSequence s, 
                final int start, 
                final int count,
                final int after
            ) {
                // do nothing 
            }
            
            @Override
            public void afterTextChanged(
                final Editable s
            ) {
                if (myEdit == false) {
                    myEdit = true;
                 
                    for (int i = 0; i < s.length(); i++) {
                        if (invalidChar(s.charAt(i))) {
                            s.delete(i, i + 1);
                        }
                    }
                    
                    myEdit = false;
                }
            }

            private char[] invalidChars = {'*', '/', '"', ':', '<', '>', '?', '\\', '|', '+', ',', '.', ';', '=', '[', ']', '\'', '`', 127};
            
            private boolean invalidChar(
                final char c
            ) {
                for (int i = 0; i < invalidChars.length; i++) {
                    if (c == invalidChars[i]) {
                        return true;
                    }
                }
                
                return 
                    (c >= 0 && c <= 31);
            }
        });
        
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
