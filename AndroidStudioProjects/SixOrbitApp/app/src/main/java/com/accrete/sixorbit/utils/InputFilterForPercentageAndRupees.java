package com.accrete.sixorbit.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by agt on 17/1/18.
 */

public class InputFilterForPercentageAndRupees implements InputFilter {
    private double min;
    private double max;

    public InputFilterForPercentageAndRupees(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //noinspection EmptyCatchBlock
        try {
            double input = Double.parseDouble(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}