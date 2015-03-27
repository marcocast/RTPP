package com.rtpp.rtpp.utility;

import android.widget.EditText;

/**
 * Created by marco on 21/03/15.
 */
public class RtppUtility {

    private RtppUtility(){}

    public static String getTextContent(EditText editText){
        if(editText == null || editText.getText() == null){
            return "";
        }else{
            return editText.getText().toString().toUpperCase().replaceAll(" ","-").trim();
        }

    }
}
