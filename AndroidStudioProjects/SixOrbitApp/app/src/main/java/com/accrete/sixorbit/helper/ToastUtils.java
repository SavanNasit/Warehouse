package com.accrete.sixorbit.helper;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.accrete.sixorbit.R;


/**
 * Created by poonam on 14/4/17.
 */

class ToastUtils {

    private ToastUtils() {
    }

    static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
        final GradientDrawable toastDrawable = (GradientDrawable) getDrawable(context, R.drawable.border_toast);
        toastDrawable.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        return toastDrawable;
    }

    static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return context.getDrawable(id);
            else
                return context.getResources().getDrawable(id);
        }
        return context.getResources().getDrawable(R.drawable.ic_error_outline_white_48dp);
    }
}