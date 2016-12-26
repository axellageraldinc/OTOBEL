package com.example.axellageraldinc.smartalarm;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Axellageraldinc A on 26-Dec-16.
 */

public class CustomTextViewRegular extends TextView {

    public CustomTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf"));
    }
}
