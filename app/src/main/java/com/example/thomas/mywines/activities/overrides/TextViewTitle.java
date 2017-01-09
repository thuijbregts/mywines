package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.thomas.mywines.activities.MainActivity;

public class TextViewTitle extends TextView {
    public TextViewTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewTitle(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.TITLE_FONT);
        setTypeface(tf);
    }
}
