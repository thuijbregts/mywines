package com.example.thomas.mywines.activities.eventsActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.overrides.MyDatePickerDialog;
import com.example.thomas.mywines.activities.winesActivity.AddWineActivity;
import com.example.thomas.mywines.activities.winesActivity.WinesActivity;
import com.example.thomas.mywines.informationclasses.Wine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddWineForEventActivity extends AddWineActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isExpo = getIntent().getExtras().getBoolean("isExpo");
        RelativeLayout date = (RelativeLayout) findViewById(R.id.dialog_add_linear_date);
        date.setVisibility(View.GONE);
        if(isExpo){
            EditText sellerEdit = (EditText) findViewById(R.id.dialog_add_seller);
            sellerEdit.setText(getIntent().getExtras().getString("eventName"));
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.dialog_add_empty);
            empty.setVisibility(View.VISIBLE);
        }
        else{
            RelativeLayout seller = (RelativeLayout) findViewById(R.id.dialog_add_linear_seller);
            seller.setVisibility(View.GONE);

            RelativeLayout price = (RelativeLayout) findViewById(R.id.dialog_add_linear_price);
            price.setBackgroundResource(R.color.dialog_add_background_even);
            ((TextView)price.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.dialog_add_text_even));
        }
    }

    public void closeActivity(Wine wine){
        WinesActivityForEvent.activity.addItem(wine, true);
        finish();
    }
}
