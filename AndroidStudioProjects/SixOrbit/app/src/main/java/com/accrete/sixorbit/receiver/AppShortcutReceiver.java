package com.accrete.sixorbit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.accrete.sixorbit.helper.ShortcutHelper;

/**
 * Created by poonam on 2/20/18.
 */

public class AppShortcutReceiver extends BroadcastReceiver {
    private static final String TAG = "AppShortcutReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + intent);
        if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
            // Refresh all shortcut to updateCount the labels.
            // (Right now shortcut labels don't contain localized strings though.)
            //new ShortcutHelper(context).refreshShortcuts(/*force=*/ true);
          //  new ShortcutHelper(context).getShortcuts();
      /*      if( new ShortcutHelper(context).getShortcuts().equals("Create Quotation")) {

                new ShortcutHelper(context).disableShortcut(new ShortcutHelper(context).getShortcuts().get().get());
            }*/

        }
    }
}