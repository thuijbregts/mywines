package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.thomas.mywines.activities.MainActivity;

public class CalendarTextView extends TextView {

    public CalendarTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CalendarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.CALENDAR_FONT);
        setTypeface(tf);
    }
}