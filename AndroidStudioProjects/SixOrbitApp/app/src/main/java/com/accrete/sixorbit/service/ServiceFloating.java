package com.accrete.sixorbit.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by poonam on 1/16/18.
 */


public class ServiceFloating extends Service {
    public static int ID_NOTIFICATION = 2018;
    View view;
    private boolean flagVisible = false;
    private NotificationManager notificationManager;
    private CircleImageView profileImage;
    private TextView frontPersonType;
    private TextView frontName;
    private TextView frontBalance;
    private TextView frontCancel;
    private TextView frontViewAccount;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("inez", "inez onCreate");
        super.onCreate();

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //  params.gravity = Gravity.LEFT   | Gravity.TOP;

        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.know_your_caller_pop_up_front, null);
        profileImage = (CircleImageView)view.findViewById( R.id.profile_image );
        frontPersonType = (TextView)view.findViewById( R.id.front_person_type );
        frontName = (TextView)view.findViewById( R.id.front_name );
        frontBalance = (TextView)view.findViewById( R.id.front_balance );
        frontCancel = (TextView)view.findViewById( R.id.front_cancel );
        frontViewAccount = (TextView)view.findViewById( R.id.front_view_account );



        wm.addView(view, params);

        frontCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(view);
            }
        });

        frontViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ServiceFloating.this, "View Account", Toast.LENGTH_SHORT).show();
            }
        });
        try {
            view.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);


                            //Update the layout with new X & Y coordinate
                            wm.updateViewLayout(view, params);
                            return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
       // createNotification();
        return START_STICKY;
    }

    public void onDestroy() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(view);
        if (view != null) wm.removeView(view);
      /*  if(flagVisible) {
            notificationManager.cancel(ID_NOTIFICATION);
            flagVisible=false;
        }
*/

    }

    public void createNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), ServiceFloating.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher).setTicker("Click to start app").setWhen(System.currentTimeMillis())
                .setContentTitle("SixOrbit");
               // .setContentText("Click to start launcher");
         notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_NOTIFICATION, builder.build());
       
       flagVisible = true;
    }
}
