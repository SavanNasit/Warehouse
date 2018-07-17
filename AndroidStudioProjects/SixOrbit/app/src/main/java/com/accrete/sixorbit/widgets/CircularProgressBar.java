package com.accrete.sixorbit.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by agt on 21/2/18.
 */

public class CircularProgressBar extends ProgressBar {
    public CircularProgressBar(Context paramContext) {
        super(paramContext);
    }

    public CircularProgressBar(Context paramContext,
                               AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public CircularProgressBar(Context paramContext,
                               AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void draw(Canvas paramCanvas) {
        int i = getMeasuredWidth();
        int j = getMeasuredHeight();
        paramCanvas.save();
        paramCanvas.rotate(135.0F, i / 2, j / 2);
        super.draw(paramCanvas);
        paramCanvas.restore();
    }
}