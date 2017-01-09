package com.example.thomas.mywines.activities.settingsActivity;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.overrides.MyTimePickerDialog;
import com.example.thomas.mywines.informationclasses.Currency;
import com.example.thomas.mywines.informationclasses.Score;


public class SettingsActivity extends Activity {

    private String currencyString;
    private String scoringString;

    private String summaryTimePicker;
    private boolean notificationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setButtons();
    }

    private void setButtons(){
        RelativeLayout backToMain = (RelativeLayout) findViewById(R.id.settings_button_back);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });

        notificationEnabled = MainActivity.preferences.getBoolean("notification_enabled", true);
        final CheckBox enabledCheck = (CheckBox) findViewById(R.id.settings_notification_checkbox);
        enabledCheck.setChecked(notificationEnabled);
        enabledCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificationEnabled = isChecked;
            }
        });

        setGeneralButtons();
        setTimeView();

        RelativeLayout buttonSave = (RelativeLayout) findViewById(R.id.settings_button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = false;

                if (!MainActivity.preferences.getString("score", Score.TWENTY).equals(scoringString)) {
                    MainActivity.editor.putString("score", scoringString);
                    MainActivity.editor.apply();
                    saved = true;
                }

                if(!MainActivity.preferences.getString("currency", Currency.EUR).equals(currencyString)){
                    MainActivity.editor.putString("currency", currencyString);
                    MainActivity.editor.apply();
                    saved = true;
                }
                boolean changed = false;

                if (!MainActivity.preferences.getString("notification_time", "default").equals(summaryTimePicker)) {
                    MainActivity.editor.putString("notification_time", summaryTimePicker);
                    MainActivity.editor.commit();
                    saved = true;

                    if (notificationEnabled) {
                        changed = true;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.dbHelper.enableAllAlarms();
                            }
                        });

                        thread.start();
                    }
                }

                final boolean alreadyChanged = changed;
                if (notificationEnabled != MainActivity.preferences.getBoolean("notification_enabled", true)) {
                    MainActivity.editor.putBoolean("notification_enabled", notificationEnabled);
                    MainActivity.editor.commit();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (notificationEnabled) {
                                if (!alreadyChanged)
                                    MainActivity.dbHelper.enableAllAlarms();
                            } else
                                MainActivity.dbHelper.disableAllAlarms();
                        }
                    });

                    thread.start();

                    saved = true;
                }

                if (saved) {
                    final TextView view = (TextView) findViewById(R.id.settings_text_saved);
                    animateView(view);
                }
            }
        });
    }

    private void setGeneralButtons(){
        final TextView currency = (TextView) findViewById(R.id.settings_general_currency);
        currencyString = MainActivity.preferences.getString("currency", Currency.EUR);
        currency.setText(currencyString);

        RelativeLayout currencyButton = (RelativeLayout) findViewById(R.id.settings_currency_button);
        currencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currency.getText().equals(Currency.EUR))
                    currencyString = Currency.USD;
                else if (currency.getText().equals(Currency.USD))
                    currencyString = Currency.GBP;
                else
                    currencyString = Currency.EUR;

                currency.setText(currencyString);
            }
        });

        scoringString = MainActivity.preferences.getString("score", Score.TWENTY);
        final TextView score = (TextView) findViewById(R.id.settings_general_score);
        score.setText(scoringString);

        RelativeLayout scoreButton = (RelativeLayout) findViewById(R.id.settings_score_button);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(score.getText().equals(Score.TWENTY))
                    scoringString = Score.HUNDRED;
                else
                    scoringString = Score.TWENTY;
                score.setText(scoringString);
            }
        });
    }

    private void setTimeView(){

        summaryTimePicker = MainActivity.preferences.getString("notification_time", "12:00");
        final int hour = Integer.parseInt(summaryTimePicker.substring(0, 2));
        final int minute = Integer.parseInt(summaryTimePicker.substring(3));
        final TextView time = (TextView) findViewById(R.id.settings_notification_time);
        time.setText(summaryTimePicker);
        RelativeLayout buttonTime = (RelativeLayout) findViewById(R.id.notification_time_button);
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimePickerDialog dialog = new MyTimePickerDialog(SettingsActivity.this, R.style.DatePicker, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        summaryTimePicker = "";
                        if(hourOfDay < 10)
                            summaryTimePicker += "0";
                        if(minute < 10)
                            summaryTimePicker += hourOfDay + ":0" + minute;
                        else
                            summaryTimePicker += hourOfDay + ":" + minute;
                        time.setText(summaryTimePicker);
                    }
                }, hour, minute, true);
                dialog.setPermanentTitle(SettingsActivity.this.getResources().getString(R.string.settings_notification_time));
                dialog.show();
                dialog.setTypeFace();
            }
        });
    }

    public void animateView(final View view){
        view.setVisibility(View.VISIBLE);

        int movement = - MainActivity.convertDpToPixel(40, SettingsActivity.this);
        final TranslateAnimation slideDown = new TranslateAnimation(0, 0, movement, 0);
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, movement);
        slideUp.setDuration(1000);
        slideUp.setFillAfter(true);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.startAnimation(slideDown);
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideDown.setDuration(1000);
        slideDown.setFillAfter(true);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(slideUp);
    }
}
