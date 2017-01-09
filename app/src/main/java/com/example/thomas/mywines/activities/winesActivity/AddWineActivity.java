package com.example.thomas.mywines.activities.winesActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.overrides.MyDatePickerDialog;
import com.example.thomas.mywines.informationclasses.Currency;
import com.example.thomas.mywines.informationclasses.Score;
import com.example.thomas.mywines.informationclasses.Wine;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddWineActivity extends Activity {

    protected RelativeLayout wineTypeSelected;
    protected RelativeLayout red;
    protected RelativeLayout rose;
    protected RelativeLayout white;

    protected Date dateToSet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wines_add_dialog);

        setWineSelectionComponents();
        setValueHolders();
        setCancelButton();
    }

    public void setWineSelectionComponents(){

        final RelativeLayout red = (RelativeLayout) findViewById(R.id.dialog_add_type_red);
        this.red = red;

        final RelativeLayout rose = (RelativeLayout) findViewById(R.id.dialog_add_type_rose);
        this.rose = rose;

        final RelativeLayout white = (RelativeLayout) findViewById(R.id.dialog_add_type_white);
        this.white = white;

        wineTypeSelected = red;
        red.setBackgroundResource(R.drawable.dialog_add_type_selected);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wineTypeSelected == null) {
                    wineTypeSelected = red;
                    red.setBackgroundResource(R.drawable.dialog_add_type_selected);
                } else if (wineTypeSelected != red) {
                    if (wineTypeSelected.equals(rose))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }

                    else if (wineTypeSelected.equals(white))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }
                    wineTypeSelected = red;
                    red.setBackgroundResource(R.drawable.dialog_add_type_selected);
                }
            }
        });
        rose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wineTypeSelected == null) {
                    wineTypeSelected = rose;
                    rose.setBackgroundResource(R.drawable.dialog_add_type_selected);
                } else if (wineTypeSelected != rose) {
                    if (wineTypeSelected.equals(red))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }
                    else if (wineTypeSelected.equals(white))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }
                    wineTypeSelected = rose;
                    rose.setBackgroundResource(R.drawable.dialog_add_type_selected);
                }
            }
        });
        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wineTypeSelected == null) {
                    wineTypeSelected = white;
                    white.setBackgroundResource(R.drawable.dialog_add_type_selected);
                } else if (wineTypeSelected != white) {
                    if (wineTypeSelected.equals(rose))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }
                    else if (wineTypeSelected.equals(red))
                        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
                            wineTypeSelected.setBackground(null);
                        } else {
                            wineTypeSelected.setBackgroundDrawable(null);
                        }
                    wineTypeSelected = white;
                    white.setBackgroundResource(R.drawable.dialog_add_type_selected);
                }
            }
        });
    }
    public void setCancelButton(){
        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.dialog_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setDate(int year, int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1);
        dateToSet = c.getTime();
    }

    public void setValueHolders(){

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AddWineActivity.hideKeyboard(AddWineActivity.this, v);
                }
            }
        };

        final SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        final Calendar c = Calendar.getInstance();
        dateToSet = c.getTime();

        final TextView dateText = (TextView) findViewById(R.id.dialog_add_date_text);
        dateText.setText(format.format(dateToSet));

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));

        Button dateChange = (Button) findViewById(R.id.dialog_add_button_calendar);
        dateChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setTime(dateToSet);
                MyDatePickerDialog dialog = new MyDatePickerDialog(AddWineActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDate(year, monthOfYear);
                        c.set(year, monthOfYear, 1);
                        dateText.setText(format.format(c.getTime()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.setPermanentTitle(AddWineActivity.this.getResources().getString(R.string.date_picker_title));
                dialog.hideDaysRow();
                dialog.show();
                dialog.setTypeFace();

            }
        });

        final EditText nameEdit = (EditText) findViewById(R.id.dialog_add_name);
        nameEdit.setOnFocusChangeListener(listener);

        final TextView appellationText = (TextView) findViewById(R.id.dialog_add_appellation_text);
        Button appellationButton = (Button) findViewById(R.id.dialog_add_appellation_button);
        appellationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddWineActivity.this, AppellationsActivity.class);
                startActivityForResult(intent, 50);
            }
        });

        TextView note = (TextView) findViewById(R.id.dialog_add_note_label);
        note.setText(MainActivity.preferences.getString("score", Score.TWENTY));

        final EditText noteEdit = (EditText) findViewById(R.id.dialog_add_note);
        noteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AddWineActivity.hideKeyboard(AddWineActivity.this, v);

                    if (noteEdit.length() != 0) {
                        if (noteEdit.getText().charAt(noteEdit.length() - 1) == '.') {
                            noteEdit.setText(noteEdit.getText().subSequence(0, noteEdit.length() - 1));
                        }
                    }
                }
            }
        });
        final EditText commentEdit = (EditText) findViewById(R.id.dialog_add_comment);
        commentEdit.setOnFocusChangeListener(listener);
        final EditText sellerEdit = (EditText) findViewById(R.id.dialog_add_seller);
        sellerEdit.setOnFocusChangeListener(listener);
        final EditText yearEdit = (EditText) findViewById(R.id.dialog_add_year);
        yearEdit.setOnFocusChangeListener(listener);

        yearEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (yearEdit.getText().length() != 0) {
                    int yearValue = Integer.parseInt(yearEdit.getText().toString());
                    Calendar c = Calendar.getInstance();
                    if (yearValue > c.get(Calendar.YEAR)) {
                        yearEdit.setText("" + c.get(Calendar.YEAR));
                        yearEdit.setSelection(yearEdit.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        noteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = noteEdit.getText().length();
                if (length != 0) {
                    if (noteEdit.getText().charAt(0) == '.') {
                        noteEdit.setText("0.");
                        noteEdit.setSelection(noteEdit.getText().length());
                    } else {
                        float note = Float.parseFloat(noteEdit.getText().toString());
                        if(MainActivity.preferences.getString("score", Score.TWENTY).equals(Score.TWENTY)){
                            if (note > 20) {
                                noteEdit.setText("20");
                                noteEdit.setSelection(noteEdit.getText().length());
                            } else if (length == 4 && noteEdit.getText().charAt(1) == '.') {
                                noteEdit.setText(noteEdit.getText().subSequence(0, 3));
                                noteEdit.setSelection(noteEdit.getText().length());
                            }
                        }
                        else{
                            if (note > 100) {
                                noteEdit.setText("100");
                                noteEdit.setSelection(noteEdit.getText().length());
                            } else if (length == 5 && noteEdit.getText().charAt(2) == '.') {
                                noteEdit.setText(noteEdit.getText().subSequence(0, 3));
                                noteEdit.setSelection(noteEdit.getText().length());
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView price = (TextView) findViewById(R.id.dialog_add_price_label);
        price.setText(Currency.getCurrentCurrency());

        final EditText priceEdit = (EditText) findViewById(R.id.dialog_add_price);
        priceEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AddWineActivity.hideKeyboard(AddWineActivity.this, v);
                    int length = priceEdit.getText().length();
                    if (length != 0) {
                        if (priceEdit.getText().charAt(priceEdit.length() - 1) == '.') {
                            priceEdit.setText(priceEdit.getText().subSequence(0, priceEdit.length() - 1));
                        }
                    }
                }
            }
        });

        priceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = priceEdit.getText().length();
                if (length != 0) {
                    if (priceEdit.getText().charAt(0) == '.') {
                        priceEdit.setText("0.");
                        priceEdit.setSelection(priceEdit.getText().length());
                    }
                    else if (length >= 5 && priceEdit.getText().charAt(length-4) == '.') {
                        priceEdit.setText(priceEdit.getText().subSequence(0, length-1));
                        priceEdit.setSelection(priceEdit.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RelativeLayout add = (RelativeLayout) findViewById(R.id.dialog_button_add);
        add.setOnClickListener(new View.OnClickListener() {

            private int type;
            private String name;
            private String appellation;

            private int year;
            private String yearString;

            private String note;
            private String comment;
            private String seller;

            private float price;
            private String priceString;

            @Override
            public void onClick(View v) {
                if (wineTypeSelected == red)
                    type = Wine.RED;
                else if (wineTypeSelected == rose)
                    type = Wine.ROSE;
                else if (wineTypeSelected == white)
                    type = Wine.WHITE;
                else
                    type = 0;

                name = nameEdit.getText().toString();
                appellation = appellationText.getText().toString();
                note = noteEdit.getText().toString();
                comment = commentEdit.getText().toString();
                seller = sellerEdit.getText().toString();
                yearString = yearEdit.getText().toString();
                priceString = priceEdit.getText().toString();
                if (yearString == null || yearString.length() == 0)
                    year = 0;
                else
                    year = Integer.parseInt(yearString);

                if (priceString == null || priceString.length() == 0)
                    price = -1;
                else
                    price = Float.parseFloat(priceString);
                if (checkValues()) {
                    if(MainActivity.preferences.getString("score", Score.TWENTY).equals(Score.TWENTY)){
                        float score = Float.parseFloat(note);
                        score *= 5;
                        note = "" + score;
                        if (note.charAt(note.length() - 1) == '0') {
                            note = note.substring(0, note.length() - 1);
                        }
                        if (note.charAt(note.length() - 1) == '.') {
                            note = note.substring(0, note.length() - 1);
                        }
                    }
                    Wine wine = new Wine(type, name, appellation, year, note, dateToSet, comment, seller, price);
                    closeActivity(wine);
                    finish();
                }
            }

            private boolean checkValues() {
                TextView typeError = (TextView) findViewById(R.id.dialog_add_type_error);
                TextView nameError = (TextView) findViewById(R.id.dialog_add_name_error);
                TextView appellationError = (TextView) findViewById(R.id.dialog_add_appellation_error);
                TextView yearError = (TextView) findViewById(R.id.dialog_add_year_error);
                TextView noteError = (TextView) findViewById(R.id.dialog_add_note_error);

                boolean error = false;
                if (type == 0) {
                    typeError.setVisibility(View.VISIBLE);
                    error = true;
                } else {
                    typeError.setVisibility(View.GONE);
                }

                if (name == null || name.length() == 0) {
                    nameError.setVisibility(View.VISIBLE);
                    error = true;
                } else {
                    nameError.setVisibility(View.GONE);
                }

                if (year == 0) {
                    yearError.setVisibility(View.VISIBLE);
                    error = true;
                } else {
                    yearError.setVisibility(View.GONE);
                }

                if (appellation == null || appellation.length() == 0) {
                    appellationError.setVisibility(View.VISIBLE);
                    error = true;
                } else {
                    appellationError.setVisibility(View.GONE);
                }

                if (note == null || note.length() == 0) {
                    noteError.setVisibility(View.VISIBLE);
                    error = true;
                } else {
                    noteError.setVisibility(View.GONE);
                }

                return !error;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 50 && resultCode == RESULT_OK && data != null) {
            String appellation = data.getStringExtra("appellation");
            TextView appellationText = (TextView) findViewById(R.id.dialog_add_appellation_text);
            appellationText.setText(appellation);
        }
    }


    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void closeActivity(Wine wine){
        WinesActivity.activity.addItem(wine, true);
        finish();
    }
}
