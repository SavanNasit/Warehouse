package com.accrete.sixorbit.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.PassDateToCounsellor;

import java.util.Calendar;

/**
 * Created by agt on 3/11/17.
 */

public class RestrictFutureDatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    PassDateToCounsellor passDateToCounsellor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                this, year, month, day);
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        return mDatePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // do some stuff for example write on log and updateCount TextField on activity
        passDateToCounsellor.passDate(day + "-" + (month + 1) + "-" + year);
    }

    public void setListener(PassDateToCounsellor passDateToCounsellor) {
        this.passDateToCounsellor = passDateToCounsellor;

    }

}