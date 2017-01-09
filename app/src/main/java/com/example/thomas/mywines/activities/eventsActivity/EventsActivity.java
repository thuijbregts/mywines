package com.example.thomas.mywines.activities.eventsActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import com.example.thomas.mywines.activities.overrides.MyDatePickerDialog;
import com.example.thomas.mywines.activities.winesActivity.AddWineActivity;
import com.example.thomas.mywines.informationclasses.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsActivity extends Activity{

    private ArrayList<Event> allEvents = MainActivity.dbHelper.getAllEventsList();

    private ArrayList<Event> eventsActive = new ArrayList<>();
    private ListView listActive;
    private EventsListAdapter listActiveAdapter;

    private ArrayList<Event> eventsPast = new ArrayList<>();
    private ListView listPast;
    private EventsListAdapter listPastAdapter;

    private ArrayList<Event> eventsUpcoming = new ArrayList<>();
    private ListView listUpcoming;
    private EventsListAdapter listUpcomingAdapter;

    private RelativeLayout selectedTab;
    private int selectedTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);

        setLists();
        setButtons();
    }

    public void addItem(Event event, boolean changeDb){
        if(changeDb)
            MainActivity.dbHelper.addEvent(event);
        if(isUpcoming(event)){
            boolean added = false;
            for(int i = 0; i < eventsUpcoming.size(); i++){
                if(!eventsUpcoming.get(i).getDateStart().before(event.getDateStart())){
                    eventsUpcoming.add(i, event);
                    added = true;
                    break;
                }
            }
            if(!added)
                eventsUpcoming.add(eventsUpcoming.size(), event);
            listUpcomingAdapter.notifyDataSetChanged();
        }
        else if(!isPast(event)){
            eventsActive.add(0, event);
            listActiveAdapter.notifyDataSetChanged();
        }
        else{
            boolean added = false;
            for(int i = 0; i < eventsPast.size(); i++){
                if(eventsPast.get(i).getDateStart().before(event.getDateStart())){
                    eventsPast.add(i, event);
                    added = true;
                    break;
                }
            }
            if(!added)
                eventsPast.add(eventsPast.size(), event);
            listPastAdapter.notifyDataSetChanged();
        }
    }

    public void removeItem(final Event event){
        MainActivity.dbHelper.removeEvent(event);

        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        anim.setDuration(500);

        ArrayList<Event> list = null;
        ListView lv = null;
        EventsListAdapter adpt = null;
        switch (selectedTabIndex){
            case 0:
                list = eventsPast;
                lv = listPast;
                adpt = listPastAdapter;
                break;
            case 1:
                list = eventsActive;
                lv = listActive;
                adpt = listActiveAdapter;
                break;
            case 2:
                list = eventsUpcoming;
                lv = listUpcoming;
                adpt = listUpcomingAdapter;
                break;
        }
        int i;
        for(i = 0; i < list.size(); i++){
            if(list.get(i).getId() == event.getId())
                break;
        }
        lv.getChildAt(i - lv.getFirstVisiblePosition()).startAnimation(anim);

        final ArrayList<Event> l = list;
        final EventsListAdapter a = adpt;
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                l.remove(event);
                a.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void updateItem(Event event){
        MainActivity.dbHelper.updateEvent(event);

        ArrayList<Event> list = null;
        EventsListAdapter adpt = null;
        switch (selectedTabIndex){
            case 0:
                list = eventsPast;
                adpt = listPastAdapter;
                break;
            case 1:
                list = eventsActive;
                adpt = listActiveAdapter;
                break;
            case 2:
                list = eventsUpcoming;
                adpt = listUpcomingAdapter;
                break;
        }

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getId() == event.getId()){
                list.remove(i);
                addItem(event, false);
                break;
            }
        }
        adpt.notifyDataSetChanged();
    }

    private void setLists(){
        listActive = (ListView) findViewById(R.id.events_list_active);
        listActive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = eventsActive.get(position);
                Intent intent = new Intent(EventsActivity.this, WinesActivityForEvent.class);
                Bundle bundle = new Bundle();
                bundle.putString("eventName", e.getName());
                bundle.putBoolean("isExpo", e.getType() == Event.EXPO);
                bundle.putBoolean("isEventPast", isPast(e));
                bundle.putLong("eventID", e.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });

        listPast = (ListView) findViewById(R.id.events_list_past);
        listPast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = eventsPast.get(position);
                Intent intent = new Intent(EventsActivity.this, WinesActivityForEvent.class);
                Bundle bundle = new Bundle();
                bundle.putString("eventName", e.getName());
                bundle.putBoolean("isExpo", e.getType() == Event.EXPO);
                bundle.putBoolean("isPastEvent", isPast(e));
                bundle.putLong("eventID", e.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listUpcoming = (ListView) findViewById(R.id.events_list_upcoming);
        listUpcoming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = eventsUpcoming.get(position);

                Calendar c = Calendar.getInstance();
                Calendar c1 = Calendar.getInstance();
                c1.setTime(e.getDateStart());

                int daysCount = 0;
                while(c.get(Calendar.YEAR) < c1.get(Calendar.YEAR)){
                    daysCount += c.getMaximum(Calendar.DAY_OF_YEAR);
                    c.add(Calendar.YEAR, 1);
                }

                while (c.get(Calendar.DAY_OF_YEAR) < c1.get(Calendar.DAY_OF_YEAR)) {
                    daysCount++;
                    c.add(Calendar.DAY_OF_YEAR, 1);
                }

                while (c.get(Calendar.DAY_OF_YEAR) > c1.get(Calendar.DAY_OF_YEAR)) {
                    daysCount--;
                    c.add(Calendar.DAY_OF_YEAR, -1);
                }

                final Dialog dialog = new Dialog(EventsActivity.this, R.style.Dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.events_upcoming_dialog);

                TextView days = (TextView) dialog.findViewById(R.id.events_upcoming_dialog_days);
                days.setText("" + daysCount + " " + getResources().getString(R.string.events_upcoming_text_day) + (daysCount == 1?"":"S"));

                String str;
                if(e.getType() == Event.EXPO) {
                    str = daysCount == 1 ? getResources().getString(R.string.events_upcoming_text_expo) : getResources().getString(R.string.events_upcoming_text_expo_plur);
                }
                else{
                    str = daysCount == 1 ? getResources().getString(R.string.events_upcoming_text_tasting) : getResources().getString(R.string.events_upcoming_text_tasting_plur);
                }
                TextView sent = (TextView) dialog.findViewById(R.id.events_upcoming_dialog_sentence);
                sent.setText(str);

                TextView event = (TextView) dialog.findViewById(R.id.events_upcoming_dialog_name);
                event.setText(e.getName());

                Button close = (Button) dialog.findViewById(R.id.events_upcoming_dialog_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        for(int i = 0; i < allEvents.size(); i++){
            if(isUpcoming(allEvents.get(i)))
                eventsUpcoming.add(allEvents.get(i));
            else
                break;
        }

        for(int i = eventsUpcoming.size(); i < allEvents.size(); i++){
            if(!isPast(allEvents.get(i)))
                eventsActive.add(allEvents.get(i));
            else
                break;
        }

        for(int i = eventsUpcoming.size() + eventsActive.size(); i < allEvents.size(); i++)
            eventsPast.add(allEvents.get(i));

        listActiveAdapter = new EventsListAdapter(this, eventsActive);
        listActive.setAdapter(listActiveAdapter);

        listPastAdapter = new EventsListAdapter(this, eventsPast);
        listPast.setAdapter(listPastAdapter);

        listUpcomingAdapter = new EventsListAdapter(this, eventsUpcoming);
        listUpcoming.setAdapter(listUpcomingAdapter);
    }

    public static boolean isPast(Event event){
        Calendar today = Calendar.getInstance();
        Calendar dateEvent = Calendar.getInstance();
        dateEvent.setTime(event.getDateStart());
        dateEvent.add(Calendar.DAY_OF_MONTH, event.getDuration() - 1);
        return(dateEvent.get(Calendar.YEAR) < today.get(Calendar.YEAR) ||
                (dateEvent.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && dateEvent.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isUpcoming(Event event){
        Calendar today = Calendar.getInstance();
        Calendar dateEvent = Calendar.getInstance();
        dateEvent.setTime(event.getDateStart());
        return(dateEvent.get(Calendar.YEAR) > today.get(Calendar.YEAR) ||
                (dateEvent.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && dateEvent.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR));
    }

    private void setButtons(){
        RelativeLayout backToMain = (RelativeLayout) findViewById(R.id.events_button_back);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsActivity.this.onBackPressed();
            }
        });

        RelativeLayout add = (RelativeLayout) findViewById(R.id.events_button_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAddDialog(EventsActivity.this, R.style.Dialog).show();
            }
        });

        setTabButtons();
    }

    private void setTabButtons(){
        final LinearLayout tabRow = (LinearLayout) findViewById(R.id.events_tab_row);

        if(getIntent().getBooleanExtra("fromNotification", false))
            selectedTab = (RelativeLayout) tabRow.getChildAt(2);
        else
            selectedTab = (RelativeLayout) tabRow.getChildAt(1);
        refreshTabs(tabRow);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!v.equals(selectedTab)) {
                    selectedTab = (RelativeLayout) v;
                    refreshTabs(tabRow);
                }
            }
        };

        for(int i = 0; i < tabRow.getChildCount(); i++){
            tabRow.getChildAt(i).setOnClickListener(listener);
        }
    }

    private void refreshTabs(LinearLayout tabRow){
        int margin = MainActivity.convertDpToPixel(5, this);

        for(int i = 0; i < tabRow.getChildCount(); i++){
            RelativeLayout rl = (RelativeLayout) tabRow.getChildAt(i);
            TextView tv = (TextView) rl.getChildAt(0);
            if(rl.equals(selectedTab)){
                selectedTabIndex = i;
                switch(i){
                    case 0:
                        listPast.setVisibility(View.VISIBLE);
                        listActive.setVisibility(View.GONE);
                        listUpcoming.setVisibility(View.GONE);
                        break;
                    case 1:
                        listPast.setVisibility(View.GONE);
                        listActive.setVisibility(View.VISIBLE);
                        listUpcoming.setVisibility(View.GONE);
                        break;
                    case 2:
                        listPast.setVisibility(View.GONE);
                        listActive.setVisibility(View.GONE);
                        listUpcoming.setVisibility(View.VISIBLE);
                        break;
                }

                LinearLayout.LayoutParams lpSelected = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lpSelected.weight = 1;
                lpSelected.setMargins(margin, margin, margin, 0);
                rl.setLayoutParams(lpSelected);

                rl.setBackgroundResource(R.drawable.events_tab_shape);

                tv.setTextColor(ContextCompat.getColor(EventsActivity.this, R.color.tab_selected_text));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            }
            else{
                LinearLayout.LayoutParams lpDefault = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lpDefault.weight = 1;
                lpDefault.setMargins(margin, margin*2, margin, margin*2);
                rl.setLayoutParams(lpDefault);

                rl.setBackgroundResource(R.drawable.events_tab_default);

                tv.setTextColor(ContextCompat.getColor(EventsActivity.this, R.color.tab_default_text));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            }
        }
    }

    public void startModifyDialog(Event event){
        MyModifyDialog dialog = new MyModifyDialog(this, R.style.Dialog);
        dialog.show();
        dialog.setValues(event);
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getBooleanExtra("fromNotification",false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }

    private class MyAddDialog extends Dialog {

        protected Calendar c = Calendar.getInstance();

        public MyAddDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.events_add_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setCanceledOnTouchOutside(true);

            RelativeLayout dateRow = (RelativeLayout) findViewById(R.id.dialog_add_linear_date);
            dateRow.setVisibility(View.VISIBLE);

            setCalendarButton();
            setValueHolders();
            setCancelButton();
        }

        public void setCalendarButton(){
            String dateString = DateFormat.getDateInstance().format(c.getTime());

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
                    MyDatePickerDialog dialog = new MyDatePickerDialog(EventsActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            setDate(year, monthOfYear, dayOfMonth, c);
                            dateSelected.setText(DateFormat.getDateInstance().format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                    dialog.setPermanentTitle(EventsActivity.this.getResources().getString(R.string.events_date_picker_title));
                    dialog.show();
                    dialog.setTypeFace();
                }
            });
        }

        protected void setDate(int year, int month, int day, Calendar c){
            c.set(year, month, day);
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
                        AddWineActivity.hideKeyboard(EventsActivity.this, v);
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
            addItem(event, true);
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

            c.setTime(dateStart);
            super.setCalendarButton();

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
        }

        @Override
        protected void setDate(int year, int month, int day, Calendar c){
            super.setDate(year, month, day, c);
            dateStart = c.getTime();
        }

        @Override
        protected void closeDialog(Event event){
            event.setId(id);
            updateItem(event);
            dismiss();
        }
    }
}
