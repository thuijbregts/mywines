package com.example.thomas.mywines.activities.overrides;

import android.app.Activity;
import android.app.Dialog;
import android.drm.DrmStore;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.calendarActivity.CalendarActivity;
import com.example.thomas.mywines.activities.eventsActivity.EventsActivity;
import com.example.thomas.mywines.activities.winesActivity.WinesActivity;
import com.example.thomas.mywines.informationclasses.Event;
import com.example.thomas.mywines.informationclasses.Wine;

public class ConfirmDialog extends Dialog {

    private Activity activity;
    private Object objectToDelete;
    private int viewId;

    public ConfirmDialog(Activity context, int themeResId) {
        super(context, themeResId);
        activity = context;
    }

    public void setObjectToDelete(Object objectToDelete) {
        this.objectToDelete = objectToDelete;
    }

    public void setViewId(int viewId){
        this.viewId = viewId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_delete_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        Button cancel = (Button) findViewById(R.id.events_upcoming_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button confirm = (Button) findViewById(R.id.events_upcoming_dialog_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof WinesActivity){
                    Wine wine = (Wine) objectToDelete;
                    ((WinesActivity)activity).removeItem(wine, viewId);
                }
                else{
                    Event event = (Event) objectToDelete;
                    if(activity instanceof EventsActivity){
                        ((EventsActivity)activity).removeItem(event);
                    }
                    else{
                        ((CalendarActivity)activity).removeItem(event);
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        TextView alert1 = (TextView) findViewById(R.id.events_upcoming_dialog_sentence1);
        TextView alert2 = (TextView) findViewById(R.id.events_upcoming_dialog_sentence2);
        TextView name = (TextView) findViewById(R.id.events_upcoming_dialog_name);

        if(activity instanceof WinesActivity){
            Wine wine = (Wine) objectToDelete;
            alert2.setVisibility(View.GONE);
            alert1.setText(activity.getResources().getString(R.string.wines_dialog_delete_alert));
            name.setText(wine.getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        }
        else{
            Event event = (Event) objectToDelete;
            name.setText(event.getName());
            alert1.setText(activity.getResources().getString(R.string.events_dialog_delete_alert));
            alert2.setText(activity.getResources().getString(R.string.events_dialog_delete_alert2));
            if(EventsActivity.isUpcoming(event))
                alert2.setVisibility(View.GONE);
        }
    }
}
