package com.accrete.sixorbit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.accrete.sixorbit.R;

/**
 * Created by agt on 27/10/17.
 */
public class IncomingSmsReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
// Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage
                            .createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage
                            .getDisplayOriginatingAddress();
                    String senderNum = "HP-SIXOBT";
                   // if (senderNum.equals(phoneNumber)) {
                        String message = currentMessage.getDisplayMessageBody();
                        String[] split_one = message.split("OTP for login is ");
                        String otpCode = split_one[1].substring(0, 8);
                        if (otpCode.length() == 8) {
                            //Send OTP
                            Intent otpIntent = new Intent(context.getString(R.string.send_mobile_otp));
                            // You can also include some extra data.
                            otpIntent.putExtra(context.getString(R.string.send_mobile_otp), otpCode);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent);
                        }

                        Log.e("SmsReceiver", "senderNum: " + senderNum
                                + "; message: " + message);
                //    }
                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
