package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CalendarCellView extends RelativeLayout{

    private int day;
    private int month;
    private int year;

    public CalendarCellView(Context context) {
        super(context);
    }

    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
