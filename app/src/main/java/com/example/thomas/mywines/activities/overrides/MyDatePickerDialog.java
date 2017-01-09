package com.example.thomas.mywines.activities.overrides;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.test.AndroidTestRunner;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;

import java.lang.reflect.Field;

public class MyDatePickerDialog extends DatePickerDialog {

    private CharSequence title;
    private Typeface tf;

    public MyDatePickerDialog(Context context, int style, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, style, callBack, year, monthOfYear, dayOfMonth);

        tf = Typeface.createFromAsset(getContext().getAssets(), MainActivity.GENERAL_FONT);

    }

    public void hideDaysRow(){
        DatePicker dp_mes = getDatePicker();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0)
            {
                View daySpinner = dp_mes.findViewById(daySpinnerId);
                if (daySpinner != null)
                {
                    daySpinner.setVisibility(View.GONE);
                }
            }

            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0)
            {
                View monthSpinner = dp_mes.findViewById(monthSpinnerId);
                if (monthSpinner != null)
                {
                    monthSpinner.setVisibility(View.VISIBLE);
                }
            }

            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0)
            {
                View yearSpinner = dp_mes.findViewById(yearSpinnerId);
                if (yearSpinner != null)
                {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        } else { //Older SDK versions
            Field f[] = dp_mes.getClass().getDeclaredFields();
            for (Field field : f)
            {
                if(field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner"))
                {
                    field.setAccessible(true);
                    Object dayPicker = null;
                    try {
                        dayPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) dayPicker).setVisibility(View.GONE);
                }

                if(field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner"))
                {
                    field.setAccessible(true);
                    Object monthPicker = null;
                    try {
                        monthPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) monthPicker).setVisibility(View.VISIBLE);
                }

                if(field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner"))
                {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) yearPicker).setVisibility(View.VISIBLE);
                }
            }
        }
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
            /*else{
                if(view.getBackground() instanceof ColorDrawable) {
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.wines_title));
                }
            }*/
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wines_row_even));
        if(viewGroup instanceof DatePicker) {
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
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(title);
    }
}