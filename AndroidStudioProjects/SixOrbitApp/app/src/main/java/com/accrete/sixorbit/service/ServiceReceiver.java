package com.accrete.sixorbit.service;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.AudioRecorder;
import com.accrete.sixorbit.receiver.PhonecallReceiver;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.WINDOW_SERVICE;


/**
 * Created by poonam on 1/11/18.
 */

public class ServiceReceiver extends PhonecallReceiver {
    public boolean viewCreated = false;
    Context mContext;
    Random random = new Random();
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";

    private CircleImageView profileImage;
    private TextView backPersonType;
    private TextView backName;
    private TextView backBalance;
    private TextView backCancel;
    private ImageView backCall;
    private ImageView backMessage;
    private ImageView backShare;
    private ImageView backView;




    AudioRecorder audioRecorder = new AudioRecorder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
            CreateRandomAudioFileName(5) + "AudioSixOrbitRecording");
    private WindowManager wm;

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d("incoming call", "1 " + " num" + number + " " + start);
        Toast.makeText(ctx, "Incoming Call" + number, Toast.LENGTH_LONG).show();
        ctx.startService(new Intent(ctx, ServiceFloating.class));
        //   mContext = ctx;

    /* final Intent intent = new Intent(mContext, IncomingCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("phone_no",number);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mContext.startActivity(intent);
            }
        },2000);
*/

    }


    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        mContext = ctx;
        ctx.startService(new Intent(ctx, ServiceFloating.class));
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

        Log.d("onIncomingCallEnded", "3 " + number + " " + start + " " + end);
        /*try {
            audioRecorder.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //  params.gravity = Gravity.LEFT   | Gravity.TOP;

        final WindowManager wm = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.know_your_caller_pop_up, null);

        profileImage = (CircleImageView)view.findViewById( R.id.profile_image );
        backPersonType = (TextView)view.findViewById( R.id.back_person_type );
        backName = (TextView)view.findViewById( R.id.back_name );
        backBalance = (TextView)view.findViewById( R.id.back_balance );
        backCancel = (TextView)view.findViewById( R.id.back_cancel );
        backCall = (ImageView)view.findViewById( R.id.back_call );
        backMessage = (ImageView)view.findViewById( R.id.back_message );
        backShare = (ImageView)view.findViewById( R.id.back_share );
        backView = (ImageView)view.findViewById( R.id.back_view );

        backCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(view);
            }
        });



    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("onOutgoingCallEnded", "4 " + number);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //  params.gravity = Gravity.LEFT   | Gravity.TOP;

        final WindowManager wm = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.know_your_caller_pop_up, null);
        profileImage = (CircleImageView)view.findViewById( R.id.profile_image );
        backPersonType = (TextView)view.findViewById( R.id.back_person_type );
        backName = (TextView)view.findViewById( R.id.back_name );
        backBalance = (TextView)view.findViewById( R.id.back_balance );
        backCancel = (TextView)view.findViewById( R.id.back_cancel );
        backCall = (ImageView)view.findViewById( R.id.back_call );
        backMessage = (ImageView)view.findViewById( R.id.back_message );
        backShare = (ImageView)view.findViewById( R.id.back_share );
        backView = (ImageView)view.findViewById( R.id.back_view );

        backCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(view);
            }
        });

        wm.addView(view, params);



       /* try {
            audioRecorder.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ctx.stopService(new Intent(ctx, ServiceFloating.class));
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d("onMissedCall", "5 " + number);
    }

}

