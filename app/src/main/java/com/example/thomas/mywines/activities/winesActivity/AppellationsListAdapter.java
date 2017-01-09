package com.example.thomas.mywines.activities.winesActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.eventsActivity.EventsActivity;
import com.example.thomas.mywines.activities.overrides.ConfirmDialog;
import com.example.thomas.mywines.informationclasses.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AppellationsListAdapter extends ArrayAdapter {

    private final AppellationsActivity activity;
    private final ArrayList<String[]> list;

    public AppellationsListAdapter(AppellationsActivity activity, ArrayList<String[]> list) {
        super(activity, R.layout.appellations_list_row , list);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.appellations_list_row, null);
        }

        if(position %2 == 0)
            view.setBackgroundResource(R.drawable.wines_row_odd);
        else
            view.setBackgroundResource(R.drawable.wines_row_even);

        TextView appellation = (TextView) view.findViewById(R.id.appellations_name);
        appellation.setText(list.get(position)[0]);

        TextView country = (TextView) view.findViewById(R.id.appellations_country);
        String countryString;
        if(list.get(position)[1].equals("fr"))
            countryString = activity.getResources().getString(R.string.france);
        else if(list.get(position)[1].equals("it"))
            countryString = activity.getResources().getString(R.string.italy);
        else if(list.get(position)[1].equals("es"))
            countryString = activity.getResources().getString(R.string.spain);
        else if(list.get(position)[1].equals("us"))
            countryString = activity.getResources().getString(R.string.usa);
        else
            countryString = list.get(position)[1];

        country.setText(countryString);

        return view;
    }

}