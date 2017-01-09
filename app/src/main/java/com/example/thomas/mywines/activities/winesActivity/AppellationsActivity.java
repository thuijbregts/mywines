package com.example.thomas.mywines.activities.winesActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;

import java.util.ArrayList;

public class AppellationsActivity extends Activity {

    private String appellation;
    private ArrayList<String[]> list = MainActivity.dbHelper.getAllAppellations();
    private AppellationsListAdapter listAdapter;
    private ListView listView;

    private ArrayList<String[]> listQuery = new ArrayList<>();
    private AppellationsListAdapter listAdapterQuery;
    private ListView listViewQuery;

    private EditText search;
    private TextView selected;

    private boolean query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appellations_activity);

        final ListView listView = (ListView) findViewById(R.id.appellations_list_view);
        this.listView = listView;
        listAdapter = new AppellationsListAdapter(this, list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] str = list.get(position);
                appellation = str[0];
                selected.setText(appellation);
                AddWineActivity.hideKeyboard(AppellationsActivity.this, search);
            }
        });

        final ListView listViewQuery = (ListView) findViewById(R.id.appellations_list_view_query);
        this.listViewQuery = listViewQuery;
        listAdapterQuery = new AppellationsListAdapter(this, listQuery);
        listViewQuery.setAdapter(listAdapterQuery);

        listViewQuery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] str = listQuery.get(position);
                appellation = str[0];
                selected.setText(appellation);
                AddWineActivity.hideKeyboard(AppellationsActivity.this, search);
            }
        });

        selected = (TextView) findViewById(R.id.appellations_selected);

        setButtons();
        setSearchBar();
    }

    private void setSearchBar(){
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AddWineActivity.hideKeyboard(AppellationsActivity.this, v);
                }
            }
        };
        final EditText search = (EditText) findViewById(R.id.appellations_search_bar);
        this.search = search;
        search.setOnFocusChangeListener(listener);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search.length() == 0){
                    listViewQuery.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    query = false;
                }
                else{
                    if(!query){
                        listViewQuery.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        query = true;
                    }
                    listQuery.clear();
                    listQuery.addAll(MainActivity.dbHelper.getAllAppellationsQuery(search.getText().toString()));
                    listAdapterQuery.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setButtons(){
        final View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AddWineActivity.hideKeyboard(AppellationsActivity.this, v);
                }
            }
        };
        RelativeLayout back = (RelativeLayout) findViewById(R.id.settings_button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppellationsActivity.this.onBackPressed();
            }
        });

        RelativeLayout add = (RelativeLayout) findViewById(R.id.appellations_activity_add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AppellationsActivity.this, R.style.Dialog) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.appellations_add_dialog);
                        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        setCanceledOnTouchOutside(true);
                        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.appellations_dialog_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismiss();
                            }
                        });

                        final EditText name = (EditText) findViewById(R.id.appellations_dialog_name);
                        name.setOnFocusChangeListener(listener);
                        final EditText country = (EditText) findViewById(R.id.appellations_dialog_country);
                        country.setOnFocusChangeListener(listener);

                        final TextView errorName = (TextView) findViewById(R.id.appellations_dialog_error_name);
                        final TextView errorCountry = (TextView) findViewById(R.id.appellations_dialog_error_country);

                        RelativeLayout confirm = (RelativeLayout) findViewById(R.id.appellations_dialog_confirm);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (name.length() == 0 || country.length() == 0) {
                                    if (name.length() == 0) {
                                        errorName.setVisibility(View.VISIBLE);
                                    } else {
                                        errorName.setVisibility(View.GONE);
                                    }
                                    if (country.length() == 0) {
                                        errorCountry.setVisibility(View.VISIBLE);
                                    } else {
                                        errorCountry.setVisibility(View.GONE);
                                    }
                                } else {
                                    String[] tab = new String[]{name.getText().toString(), country.getText().toString()};
                                    MainActivity.dbHelper.addAppellation(tab);
                                    appellation = tab[0];
                                    selected.setText(appellation);
                                    list.add(tab);
                                    listAdapter.notifyDataSetChanged();
                                    if (query) {
                                        listQuery.add(tab);
                                        listAdapterQuery.notifyDataSetChanged();
                                    }
                                    dismiss();
                                }
                            }
                        });
                    }
                };
                dialog.show();
            }
        });

        RelativeLayout validate = (RelativeLayout) findViewById(R.id.appellation_button_validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                getIntent().putExtra("appellation", appellation);
                finish();
            }
        });
    }
}
