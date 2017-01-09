package com.example.thomas.mywines.activities.winesActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.informationclasses.Wine;

import java.util.ArrayList;


public class SpinnerAdapter extends ArrayAdapter{

        private final WinesActivity activity;
        private final ArrayList<String> list;

        public SpinnerAdapter(WinesActivity activity, ArrayList<String> list) {
            super(activity, R.layout.list_row_spinner , list);
            this.activity = activity;
            this.list = list;
        }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.list_row_spinner, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.spinner_item_text);
        tv.setText(list.get(position));

        return view;
    }
}
