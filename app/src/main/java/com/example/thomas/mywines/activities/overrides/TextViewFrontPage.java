package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.thomas.mywines.activities.MainActivity;

public class TextViewFrontPage extends TextView {
    public TextViewFrontPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewFrontPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewFrontPage(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.MAIN_FONT);
        setTypeface(tf);
    }
}
