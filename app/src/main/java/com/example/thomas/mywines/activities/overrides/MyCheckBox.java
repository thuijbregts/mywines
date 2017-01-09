package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.example.thomas.mywines.activities.MainActivity;


public class MyCheckBox extends CheckBox {
    public MyCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCheckBox(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.GENERAL_FONT);
        setTypeface(tf);
    }
}
