package com.rtpp.rtpp.utility;

import android.widget.EditText;

/**
 * Created by marco on 21/03/15.
 */
public class TextUtility {

    private TextUtility(){}

    public static String getSessionTextContent(EditText editText){
        if(editText == null || editText.getText() == null){
            return "";
        }else{
            return editText.getText().toString().replaceAll(" ","-").trim();
        }

    }

    public static String getLoginTextContent(EditText editText){
        if(editText == null || editText.getText() == null){
            return "";
        }else{
            return editText.getText().toString().trim();
        }

    }
}
