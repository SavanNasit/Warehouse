package com.accrete.sixorbit.fragment.Drawer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.PassDateToCounsellor;

import java.util.Calendar;

/**
 * Created by poonam on 31/5/17.
 */

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {
    PassDateToCounsellor passDateToCounsellor;
    private String strHour, strMinute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            strHour = bundle.getString("hour");
            strMinute = bundle.getString("minute");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        //Create and return a new instance of TimePickerDialog

        if (strHour != null && !strHour.isEmpty()) {
            if (Integer.parseInt(strMinute) < 30) {
                if (Integer.parseInt(strHour) == 0) {

                    return new TimePickerDialog(getActivity(), R.style.DialogTheme, this,
                            Integer.parseInt(strHour) , 00,
                            DateFormat.is24HourFormat(getActivity()));
                } else {
                    return new TimePickerDialog(getActivity(), R.style.DialogTheme, this,
                            Integer.parseInt(strHour) - 1, Integer.parseInt(strMinute) + 30,
                            DateFormat.is24HourFormat(getActivity()));
                }
            } else {
                if (Integer.parseInt(strHour) == 0) {
                    return new TimePickerDialog(getActivity(), R.style.DialogTheme, this,
                            Integer.parseInt(strHour) , Integer.parseInt(strMinute) - 30,
                            DateFormat.is24HourFormat(getActivity()));
                } else {
                    return new TimePickerDialog(getActivity(), R.style.DialogTheme, this,
                            Integer.parseInt(strHour), Integer.parseInt(strMinute) - 30,
                            DateFormat.is24HourFormat(getActivity()));
                }
            }
        }
        return new TimePickerDialog(getActivity(), R.style.DialogTheme, this, hour, min,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String tag = getTag();
        Log.e("TAG TIME", tag);
        passDateToCounsellor.passTime(hourOfDay + ":" + minute);
    }

    public void setListener(PassDateToCounsellor passDateToCounsellor) {
        this.passDateToCounsellor = passDateToCounsellor;
    }
}