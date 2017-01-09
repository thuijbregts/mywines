package com.example.thomas.mywines.activities.eventsActivity;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;

import com.example.thomas.mywines.activities.winesActivity.WinesActivity;

import com.example.thomas.mywines.informationclasses.Wine;

public class WinesActivityForEvent extends WinesActivity {

    private boolean eventPast;
    private String eventName;
    private boolean isExpo;
    private long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bdl = getIntent().getExtras();
        eventID = bdl.getLong("eventID");
        allItems = MainActivity.dbHelper.getAllWinesListForEvent(eventID);
        super.onCreate(savedInstanceState);
        activity = this;

        eventPast = bdl.getBoolean("isPastEvent");
        eventName = bdl.getString("eventName");
        isExpo = bdl.getBoolean("isExpo");

        TextView title = (TextView) findViewById(R.id.wines_title);
        title.setText(eventName);

        setButtonAdd();
    }

    @Override
    protected void setButtonAdd() {
        RelativeLayout add = (RelativeLayout) findViewById(R.id.wines_button_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WinesActivityForEvent.this, AddWineForEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("eventName", eventName);
                bundle.putBoolean("isExpo", isExpo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void addInDb(Wine wine){
        MainActivity.dbHelper.addWineForEvent(wine, eventID);
    }

    @Override
    public void removeDb(final Wine wine){
        MainActivity.dbHelper.removeWineForEvent(wine.getId(), eventID);
    }

    @Override
    protected void updateListForQuery(String column, String query){
        setQuery(true);
        for(int i = 0; i < 3; i++) {
            modifiedWinesCollection.get(parentList.get(i)).clear();
            modifiedWinesCollection.get(parentList.get(i)).addAll(MainActivity.dbHelper.getWineListForEvent(column, query, currentOrder, i+1, eventID));
        }

        if(listView.getVisibility() == View.VISIBLE){
            listView.setVisibility(View.GONE);
            listViewModified.setVisibility(View.VISIBLE);
        }

        notifyAdapterModified();
    }

}
