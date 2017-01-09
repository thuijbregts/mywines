package com.example.thomas.mywines.activities.eventsActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.overrides.ConfirmDialog;
import com.example.thomas.mywines.informationclasses.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EventsListAdapter extends ArrayAdapter {

    private final EventsActivity activity;
    private final ArrayList<Event> list;

    public EventsListAdapter(EventsActivity activity, ArrayList<Event> list) {
        super(activity, R.layout.events_event_row , list);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.events_event_row, null);
        }

        final Event event = list.get(position);

        TextView name = (TextView) view.findViewById(R.id.events_name);
        name.setText(event.getName());

        TextView type = (TextView) view.findViewById(R.id.events_type);
        switch(event.getType()){
            case Event.EXPO:
                type.setText(activity.getResources().getString(R.string.events_add_exposition));
                break;
            case Event.TASTING:
                type.setText(activity.getResources().getString(R.string.events_add_tasting));
                break;
        }

        String localStr = (event.getAddress().length() == 0 ? "":event.getAddress() + ", ") + (event.getCity().length() == 0 ? "":event.getCity() + ", ") + event.getCountry();
        TextView localisation = (TextView) view.findViewById(R.id.events_localisation);
        localisation.setText(localStr);

        String dateStr;
        Calendar c = Calendar.getInstance();
        c.setTime(event.getDateStart());
        if(event.getDuration() > 1){
            dateStr = activity.getResources().getString(R.string.events_row_date_from)
                    + " " + DateFormat.getDateInstance().format(c.getTime())
                    + " " + activity.getResources().getString(R.string.events_row_date_to);

            c.add(Calendar.DAY_OF_MONTH, event.getDuration()-1);

            dateStr += " " + DateFormat.getDateInstance().format(c.getTime());
        }
        else{
            dateStr = activity.getResources().getString(R.string.events_row_date_on) + " " + DateFormat.getDateInstance().format(c.getTime());
        }

        TextView date = (TextView) view.findViewById(R.id.events_date);
        date.setText(dateStr);

        TextView site = (TextView) view.findViewById(R.id.events_site);
        site.setText(event.getSite());

        RelativeLayout delete = (RelativeLayout) view.findViewById(R.id.events_button_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(activity, R.style.Dialog);
                dialog.setObjectToDelete(event);
                dialog.show();
            }
        });

        RelativeLayout modify = (RelativeLayout) view.findViewById(R.id.events_button_modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startModifyDialog(event);
            }
        });

        return view;
    }

}