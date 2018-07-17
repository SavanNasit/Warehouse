package com.accrete.sixorbit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.accrete.sixorbit.helper.DatabaseHandler;

import static com.accrete.sixorbit.activity.navigationView.DrawerActivity.context;


/**
 * Created by poonam on 1/6/17.
 */

public class rapidKartDbSyncService extends Service {
    private  String TAG = getClass().getName();
    DatabaseHandler databaseHandler = new DatabaseHandler(context);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  databaseHandler.updateFollowUps();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FirstService destroyed");
    }
}
