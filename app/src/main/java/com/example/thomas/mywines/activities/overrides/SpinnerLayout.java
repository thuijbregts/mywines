package com.example.thomas.mywines.activities.overrides;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.winesActivity.WinesActivity;


public class SpinnerLayout extends RelativeLayout {

    public SpinnerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SpinnerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(getVisibility() == View.VISIBLE){
            ListView searchList = (ListView) WinesActivity.activity.findViewById(R.id.wines_search_spinner_list);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) getX(), (int) getY() + h + 73 * MainActivity.convertDpToPixel(1, WinesActivity.activity), 0, 0);
            searchList.setLayoutParams(lp);

        }
    }
}
