package com.accrete.sixorbit.helper;

import android.os.Environment;

/**
 * Created by poonam on 6/11/17.
 */

public class CheckForSDCard {
    //Check If SD Card is present or not method
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}