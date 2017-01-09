package com.example.thomas.mywines.activities.overrides;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;

import java.lang.reflect.Field;

public class MyTimePickerDialog extends TimePickerDialog {

    private CharSequence title;
    private Typeface tf;

    public MyTimePickerDialog(Context context, int style, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, style, callBack, hourOfDay, minute, is24HourView);

        tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.GENERAL_FONT);

    }

    public void setTypeFace(){
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
        for(int i = 0; i < viewGroup.getChildCount(); i++)
            getTextChildren(viewGroup.getChildAt(i));
    }

    private void getTextChildren(View view){

        if(!(view instanceof ViewGroup)){
            if(view instanceof TextView){
                if(view instanceof Button){
                    ((Button) view).setBackgroundResource(R.drawable.wines_add_button);
                }
                ((TextView) view).setTypeface(tf);
                if(!((TextView) view).getText().equals(title))
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                ((TextView) view).setTextColor(ContextCompat.getColor(getContext(), R.color.wines_title));
            }
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wines_row_even));
        if(viewGroup instanceof TimePicker) {
            for(int i = 0; i < viewGroup.getChildCount(); i++)
                getTextChildrenDate(viewGroup.getChildAt(i));
        }
        else{
            for(int i = 0; i < viewGroup.getChildCount(); i++)
                getTextChildren(viewGroup.getChildAt(i));
        }
    }

    private void getTextChildrenDate(View view){
        if(!(view instanceof ViewGroup)){
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wines_row_even));
        for(int i = 0; i < viewGroup.getChildCount(); i++)
            getTextChildrenDate(viewGroup.getChildAt(i));

    }
    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);
        setTitle(title);
    }
}