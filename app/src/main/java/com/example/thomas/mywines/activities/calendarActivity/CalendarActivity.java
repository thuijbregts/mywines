package com.example.thomas.mywines.activities.calendarActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.overrides.CalendarCellView;
import com.example.thomas.mywines.activities.overrides.MyDatePickerDialog;
import com.example.thomas.mywines.activities.winesActivity.AddWineActivity;
import com.example.thomas.mywines.informationclasses.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends Activity {

    private final Calendar c = Calendar.getInstance();

    private Calendar today;
    private int currentMonth;
    private int currentYear;

    private LinearLayout mainLayout;
    private CalendarCellView selectedView;
    private int selectedDay;

    private ArrayList<Event> allEvents = MainActivity.dbHelper.getAllEventsList();
    private ArrayList<Event> eventsForDay = new ArrayList<>();
    private ListView eventsListView;
    private CalendarListAdapter eventsListAdapter;
    private TextView noEvents;

    private boolean firstDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        mainLayout = (LinearLayout) findViewById(R.id.calendar_main_layout);
        eventsListView = (ListView) findViewById(R.id.calendar_list);
        eventsListAdapter = new CalendarListAdapter(this, eventsForDay);
        eventsListView.setAdapter(eventsListAdapter);

        noEvents = (TextView) findViewById(R.id.calendar_list_empty);

        setCalendarValues();
        setUpButtons();
        selectedDay = today.get(Calendar.DAY_OF_YEAR);

        createCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCalendarValues();
        createCalendar();
    }

    private void setCalendarValues(){
        firstDraw = true;

        today = Calendar.getInstance();
        currentMonth = today.get(Calendar.MONTH);
        currentYear = today.get(Calendar.YEAR);
    }

    public void addItem(Event event){
        MainActivity.dbHelper.addEvent(event);
        allEvents.add(0, event);
        recreateCalendar();
    }

    public void removeItem(final Event event){
        MainActivity.dbHelper.removeEvent(event);

        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        anim.setDuration(500);
        int i;
        for(i = 0; i < eventsForDay.size(); i++){
            if(eventsForDay.get(i).getId() == event.getId())
                break;
        }
        eventsListView.getChildAt(i - eventsListView.getFirstVisiblePosition()).startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                allEvents.remove(event);
                recreateCalendar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void updateItem(Event event){
        MainActivity.dbHelper.updateEvent(event);
        for(int i = 0; i < allEvents.size(); i++){
            if(allEvents.get(i).getId() == event.getId()){
                allEvents.remove(i);
                allEvents.add(i, event);
            }
        }
        recreateCalendar();
    }

    public void recreateCalendar(){
        firstDraw = true;
        createCalendar();
    }

    public void createCalendar(){
        setRightHeaderTitle();

        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
        if(dayOfWeek == -1)
            dayOfWeek += 7;

        int childCount = 5;
        if(daysInMonth == 28 && dayOfWeek == 0){
            mainLayout.getChildAt(4).setVisibility(View.GONE);
            childCount = 4;
        }
        else
            mainLayout.getChildAt(4).setVisibility(View.VISIBLE);
        if((dayOfWeek == 5 && daysInMonth == 31) || (dayOfWeek == 6 && daysInMonth >= 30)){
            mainLayout.getChildAt(5).setVisibility(View.VISIBLE);
            childCount = 6;
        }
        else{
            mainLayout.getChildAt(5).setVisibility(View.GONE);
        }

        c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
        TextView tv;
        View v;
        for(int i = 0; i < childCount; i++){
            for(int j = 0; j < 7; j++){
                CalendarCellView cv = (CalendarCellView) ((LinearLayout) mainLayout.getChildAt(i)).getChildAt(j);
                cv.setDay(c.get(Calendar.DAY_OF_MONTH));
                cv.setMonth(c.get(Calendar.MONTH));
                cv.setYear(c.get(Calendar.YEAR));

                tv = (TextView) cv.getChildAt(0);
                tv.setText("" + c.get(Calendar.DAY_OF_MONTH));

                v = cv.getChildAt(1);

                if(c.get(Calendar.YEAR) == currentYear && c.get(Calendar.DAY_OF_YEAR) == selectedDay) {
                    selectedView = cv;
                    refreshView();
                }
                if(c.get(Calendar.MONTH) != currentMonth){
                    if(existsEventWithDate(c))
                        v.setBackgroundResource(R.drawable.calendar_cell_has_event_out_month);
                    else
                        removeCurrentBackground(v);
                    if(isSameDay(c, today)){
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        tv.setTextColor(ContextCompat.getColor(this, R.color.calendar_cell_text_today_out_month));
                    }
                    else{
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        if(c.get(Calendar.DAY_OF_WEEK) == 1)
                            tv.setTextColor(ContextCompat.getColor(this, R.color.calendar_cell_text_sunday_out_month));
                        else
                            tv.setTextColor(ContextCompat.getColor(this, R.color.calendar_cell_text_out_month));
                    }
                }
                else{
                    if(existsEventWithDate(c))
                        v.setBackgroundResource(R.drawable.calendar_cell_has_event);
                    else
                        removeCurrentBackground(v);
                    if(isSameDay(c, today)){
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
                        tv.setTextColor(ContextCompat.getColor(this, R.color.calendar_cell_text_today));
                    }
                    else{
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        if(c.get(Calendar.DAY_OF_WEEK) == 1)
                            tv.setTextColor(ContextCompat.getColor(this, R.color.calendar_cell_text_sunday));
                        else
                            tv.setTextColor(ContextCompat.getColor(this, R.color.wines_title));
                    }
                }
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        if(c.get(Calendar.MONTH) != currentMonth)
            c.add(Calendar.MONTH, -1);
    }

    private void setRightHeaderTitle() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");

        String month = format.format(c.getTime());
        String first = "" + month.charAt(0);
        first = first.toUpperCase();
        month = first + month.substring(1);

        TextView monthText = (TextView) findViewById(R.id.calendar_month_text);
        monthText.setText(month);
    }

    private boolean existsEventWithDate(Calendar c){
        Calendar dateToCompare = Calendar.getInstance();
        for(Event event:allEvents){
            dateToCompare.setTime(event.getDateStart());
            for(int i = 0; i < event.getDuration(); i++){
                if(isSameDay(c, dateToCompare)){
                    return true;
                }
                dateToCompare.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return false;
    }

    private void setUpButtons(){

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarCellView cv = (CalendarCellView) v;
                if(!cv.equals(selectedView)) {
                    Calendar c1 = Calendar.getInstance();
                    c1.set(cv.getYear(), cv.getMonth(), cv.getDay());
                    selectedDay = c1.get(Calendar.DAY_OF_YEAR);
                    if (cv.getMonth() != currentMonth) {
                        removeCurrentBackground(selectedView);
                        if (cv.getYear() < currentYear || (cv.getYear() == currentYear && cv.getMonth() < currentMonth))
                            decrementMonth();
                        else if (cv.getYear() > currentYear || (cv.getYear() == currentYear && cv.getMonth() > currentMonth))
                            incrementMonth();
                    } else {
                        removeCurrentBackgroundAnimation(selectedView);
                        CalendarActivity.this.selectedView = cv;
                        refreshView();
                    }
                }
            }
        };

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                CalendarCellView cv = (CalendarCellView) ((LinearLayout) mainLayout.getChildAt(i)).getChildAt(j);
                cv.setOnClickListener(listener);
            }
        }

        RelativeLayout backToMain = (RelativeLayout) findViewById(R.id.calendar_button_back);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.this.onBackPressed();
            }
        });

        RelativeLayout monthBack = (RelativeLayout) findViewById(R.id.calendar_top_row_back);
        monthBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCorrectSelectedDay(false);
                decrementMonth();
            }
        });

        RelativeLayout monthForward = (RelativeLayout) findViewById(R.id.calendar_top_row_forward);
        monthForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCorrectSelectedDay(true);
                incrementMonth();
            }
        });

        RelativeLayout addButton = (RelativeLayout) findViewById(R.id.calendar_activity_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAddDialog(CalendarActivity.this, R.style.Dialog).show();
            }
        });

        RelativeLayout todayButton = (RelativeLayout) findViewById(R.id.events_today_button);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(today.get(Calendar.YEAR) != currentYear || (today.get(Calendar.YEAR) == currentYear && today.get(Calendar.MONTH) != currentMonth)){
                    c.setTime(today.getTime());
                    currentYear = c.get(Calendar.YEAR);
                    currentMonth = c.get(Calendar.MONTH);
                    selectedDay = c.get(Calendar.DAY_OF_YEAR);
                    removeCurrentBackground(selectedView);
                    recreateCalendar();
                }
            }
        });
    }

    private boolean isSameDay(Calendar c1, Calendar c2){
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
    }

    private void setCorrectSelectedDay(boolean increment){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(c.getTime());
        if(increment)
            c1.add(Calendar.MONTH, 1);
        else
            c1.add(Calendar.MONTH, -1);
        int dayOfMonth = c1.get(Calendar.DAY_OF_MONTH);
        c1.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
        selectedDay = c1.get(Calendar.DAY_OF_YEAR);
        removeCurrentBackground(selectedView);
    }

    private void removeCurrentBackground(View v){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(null);
        } else {
            v.setBackground(null);;
        }
    }

    private void removeCurrentBackgroundAnimation(final View v){
        v.setBackgroundResource(R.drawable.calendar_cell_erase);
        AnimationDrawable cellAnimation = (AnimationDrawable) v.getBackground();
        int totalDuration = 0;
        for(int i = 0; i < cellAnimation.getNumberOfFrames(); i++){
            totalDuration += cellAnimation.getDuration(i);
        }
        cellAnimation.start();

        Handler mAnimationHandler = new Handler();
        mAnimationHandler.postDelayed(new Runnable() {

            public void run() {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(null);
                } else {
                    v.setBackground(null);
                    ;
                }
            }
        }, totalDuration);
    }

    private void decrementMonth(){
        c.add(Calendar.MONTH, -1);
        currentMonth = c.get(Calendar.MONTH);
        currentYear = c.get(Calendar.YEAR);
        recreateCalendar();
    }

    private void incrementMonth(){
        c.add(Calendar.MONTH, 1);
        currentMonth = c.get(Calendar.MONTH);
        currentYear = c.get(Calendar.YEAR);
        recreateCalendar();
    }

    private void refreshView(){
        Calendar dateSelected = Calendar.getInstance();
        dateSelected.set(selectedView.getYear(), selectedView.getMonth(), selectedView.getDay());

        eventsListAdapter.clearIdList();
        refreshList();

        selectedView.setBackgroundResource(R.drawable.calendar_cell);
        AnimationDrawable cellAnimation = (AnimationDrawable) selectedView.getBackground();

        if(firstDraw) {
            selectedView.setBackgroundResource(R.drawable.calendar_cell_selected_9);
            firstDraw = false;
        }
        else
            cellAnimation.start();
    }

    private void refreshList(){
        eventsForDay.clear();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(selectedView.getYear(), selectedView.getMonth(), selectedView.getDay());
        Calendar dateToCompare = Calendar.getInstance();
        for(Event event:allEvents){
            dateToCompare.setTime(event.getDateStart());
            for(int i = 0; i < event.getDuration(); i++){
                if(isSameDay(selectedDate, dateToCompare)){
                    eventsForDay.add(event);
                    break;
                }
                dateToCompare.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        eventsListAdapter.notifyDataSetChanged();
        if(eventsForDay.isEmpty()) {
            noEvents.setVisibility(View.VISIBLE);
            eventsListView.setVisibility(View.GONE);
        }
        else {
            eventsListView.setVisibility(View.VISIBLE);
            noEvents.setVisibility(View.GONE);
        }
    }

    public void startModifyDialog(Event event){
        MyModifyDialog dialog = new MyModifyDialog(this, R.style.Dialog);
        dialog.show();
        dialog.setValues(event);
    }

    private class MyAddDialog extends Dialog {

        protected final Calendar c = Calendar.getInstance();

        public MyAddDialog(Context context, int themeResId) {
            super(context, themeResId);
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.events_add_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setCanceledOnTouchOutside(true);

            c.set(selectedView.getYear(), selectedView.getMonth(), selectedView.getDay());

            TextView title = (TextView) findViewById(R.id.dialog_add_title);
            title.setText(DateFormat.getDateInstance().format(c.getTime()));

            setValueHolders();
            setCancelButton();
        }

        public void setCancelButton(){
            RelativeLayout cancel = (RelativeLayout) findViewById(R.id.dialog_button_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        public void setValueHolders(){

            View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        AddWineActivity.hideKeyboard(CalendarActivity.this, v);
                    }
                }
            };

            final EditText nameEdit = (EditText) findViewById(R.id.events_dialog_add_name);
            nameEdit.setOnFocusChangeListener(listener);
            final EditText countryEdit = (EditText) findViewById(R.id.events_dialog_add_country);
            countryEdit.setOnFocusChangeListener(listener);
            final EditText cityEdit = (EditText) findViewById(R.id.events_dialog_add_city);
            cityEdit.setOnFocusChangeListener(listener);
            final EditText addressEdit = (EditText) findViewById(R.id.events_dialog_add_address);
            addressEdit.setOnFocusChangeListener(listener);
            final EditText websiteEdit = (EditText) findViewById(R.id.events_dialog_add_website);
            websiteEdit.setOnFocusChangeListener(listener);
            final EditText durationEdit = (EditText) findViewById(R.id.events_dialog_add_duration);
            durationEdit.setOnFocusChangeListener(listener);

            durationEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (durationEdit.getText().length() != 0) {
                        int value = Integer.parseInt(durationEdit.getText().toString());
                        if (value < 1) {
                            durationEdit.setText("1");
                            durationEdit.setSelection(durationEdit.getText().length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final RadioButton radioExpo = (RadioButton) findViewById(R.id.events_dialog_radio_exposition);

            RelativeLayout add = (RelativeLayout) findViewById(R.id.dialog_button_add);
            add.setOnClickListener(new View.OnClickListener() {

                private int type;
                private String name;
                private String country;
                private String city;
                private String address;
                private String website;
                private int duration;
                private String durationString;

                @Override
                public void onClick(View v) {
                    if(radioExpo.isChecked())
                        type = Event.EXPO;
                    else
                        type = Event.TASTING;

                    name = nameEdit.getText().toString();
                    country = countryEdit.getText().toString();
                    city = cityEdit.getText().toString();
                    website = websiteEdit.getText().toString();
                    address = addressEdit.getText().toString();

                    durationString = durationEdit.getText().toString();
                    if(durationString.length() == 0)
                        duration = 1;
                    else
                        duration = Integer.parseInt(durationString);

                    if(checkValues()){
                        Event event = new Event(name, country, city, address, website, c.getTime(), duration, type);
                        closeDialog(event);
                    }
                }

                private boolean checkValues(){
                    TextView nameError = (TextView) findViewById(R.id.dialog_add_name_error);

                    boolean error = false;

                    if(name == null || name.length() == 0){
                        nameError.setVisibility(View.VISIBLE);
                        error = true;
                    }
                    else{
                        nameError.setVisibility(View.GONE);
                    }

                    return !error;
                }
            });
        }

        protected void closeDialog(Event event){
            addItem(event);
            dismiss();
        }

    }

    private class MyModifyDialog extends MyAddDialog{

        private long id;
        private Date dateStart;

        public MyModifyDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            TextView title = (TextView) findViewById(R.id.dialog_add_title);
            title.setText(getResources().getString(R.string.dialog_modify_title_type));

            TextView modify = (TextView) findViewById(R.id.dialog_add_save);
            modify.setText(getResources().getString(R.string.dialog_modify));

            RelativeLayout dateRow = (RelativeLayout) findViewById(R.id.dialog_add_linear_date);
            dateRow.setVisibility(View.VISIBLE);
        }

        public void setValues(Event event){

            id = event.getId();
            dateStart = event.getDateStart();

            RadioButton radioExpo = (RadioButton) findViewById(R.id.events_dialog_radio_exposition);
            RadioButton radioTasting = (RadioButton) findViewById(R.id.events_dialog_radio_tasting);

            switch(event.getType()){
                case Event.TASTING:
                    radioTasting.setChecked(true);
                    radioExpo.setChecked(false);
                    break;
            }
            EditText nameEdit = (EditText) findViewById(R.id.events_dialog_add_name);
            nameEdit.setText(event.getName());
            EditText countryEdit = (EditText) findViewById(R.id.events_dialog_add_country);
            countryEdit.setText(event.getCountry());
            EditText cityEdit = (EditText) findViewById(R.id.events_dialog_add_city);
            cityEdit.setText(event.getCity());
            EditText addressEdit = (EditText) findViewById(R.id.events_dialog_add_address);
            addressEdit.setText(event.getAddress());
            EditText websiteEdit = (EditText) findViewById(R.id.events_dialog_add_website);
            websiteEdit.setText(event.getSite());
            EditText durationEdit = (EditText) findViewById(R.id.events_dialog_add_duration);
            durationEdit.setText("" + event.getDuration());


            String dateString = DateFormat.getDateInstance().format(dateStart);

            final TextView dateSelected = (TextView) findViewById(R.id.dialog_add_date_text);
            dateSelected.setText(dateString);

            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

            Button dateChange = (Button) findViewById(R.id.dialog_add_button_calendar);
            dateChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.setTime(dateStart);
                    MyDatePickerDialog dialog = new MyDatePickerDialog(CalendarActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            c.set(year, monthOfYear, dayOfMonth);
                            setDate(year, monthOfYear, dayOfMonth, c);
                            dateSelected.setText(DateFormat.getDateInstance().format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                    //dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                    dialog.setPermanentTitle(CalendarActivity.this.getResources().getString(R.string.events_date_picker_title));
                    dialog.show();
                    dialog.setTypeFace();
                }
            });
        }

        private void setDate(int year, int month, int day, Calendar c){
            c.set(year, month, day);
            dateStart = c.getTime();
        }

        @Override
        protected void closeDialog(Event event){
            event.setId(id);
            event.setDateStart(dateStart);
            updateItem(event);
            dismiss();
        }
    }
}