package com.example.thomas.mywines.activities.calendarActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.overrides.ConfirmDialog;
import com.example.thomas.mywines.activities.eventsActivity.EventsActivity;
import com.example.thomas.mywines.informationclasses.Event;

public class CalendarListAdapter extends ArrayAdapter {
    private final CalendarActivity activity;
    private final ArrayList<Event> list;
    private final ArrayList<Long> ids = new ArrayList<>();

    public CalendarListAdapter(CalendarActivity activity, ArrayList<Event> list) {
        super(activity, R.layout.calendar_event_row , list);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.calendar_event_row, null);
        }

        final RelativeLayout rowContent = (RelativeLayout) view.findViewById(R.id.events_row_content);
        final Event event = list.get(position);
        final long id = event.getId();
        if(ids.contains(id))
            rowContent.setVisibility(View.VISIBLE);
        else
            rowContent.setVisibility(View.GONE);

        final ImageView image = (ImageView) view.findViewById(R.id.events_top_indicator);
        image.setImageResource(R.drawable.calendar_indactor_up);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rowContent.getVisibility() == View.VISIBLE) {
                    rowContent.setVisibility(View.GONE);
                    image.setImageResource(R.drawable.calendar_indactor_up);
                    ids.remove(id);
                } else {
                    rowContent.setVisibility(View.VISIBLE);
                    image.setImageResource(R.drawable.calendar_indactor_down);
                    ids.add(id);
                }
            }
        });

        if(position %2 == 0)
            view.setBackgroundResource(R.drawable.wines_row_odd);
        else
            view.setBackgroundResource(R.drawable.wines_row_even);

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

        TextView country = (TextView) view.findViewById(R.id.events_country);
        country.setText(event.getCountry());

        TextView city = (TextView) view.findViewById(R.id.events_city);
        city.setText(event.getCity());

        TextView address = (TextView) view.findViewById(R.id.events_address);
        address.setText(event.getAddress());

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

    public void clearIdList(){
        ids.clear();
    }
}