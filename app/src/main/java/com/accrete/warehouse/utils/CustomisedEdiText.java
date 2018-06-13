package com.accrete.warehouse.utils;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by poonam on 4/26/18.
 */

public class CustomisedEdiText extends android.support.v7.widget.AppCompatAutoCompleteTextView{
    ArrayList<CustomisedEdiTextListener> listeners;

    public CustomisedEdiText(Context context)
    {
        super(context);
        listeners = new ArrayList<>();
    }

    public CustomisedEdiText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        listeners = new ArrayList<>();
    }

    public CustomisedEdiText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        listeners = new ArrayList<>();
    }

    public void addListener(CustomisedEdiTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here you can catch paste, copy and cut events
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id){
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    public void onTextCut(){
        for (CustomisedEdiTextListener listener : listeners) {
            listener.onUpdate();
        }
    }

    public void onTextCopy(){
    }

    /**
     * adding listener for Paste for example
     */
    public void onTextPaste(){

    }
}