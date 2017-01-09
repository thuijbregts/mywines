package com.example.thomas.mywines.activities.winesActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.database.WinesFeedContract;
import com.example.thomas.mywines.informationclasses.Wine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WinesActivity extends Activity {

    public static final int THUMBNAIL_REQUEST = 1;
    public static final int NAME_DESC = 0;
    public static final int NAME_ASC = 1;
    public static final int YEAR_DESC = 2;
    public static final int YEAR_ASC = 3;
    public static final int NOTE_DESC = 4;
    public static final int NOTE_ASC = 5;

    public static WinesActivity activity;
    protected ArrayList<String> parentList;

    protected Map<String, ArrayList<Wine>> modifiedWinesCollection = new LinkedHashMap<>();
    protected ExpandableListView listViewModified;
    protected WinesListAdapter adapterModified;

    protected Map<String, ArrayList<Wine>> allWinesCollection = new LinkedHashMap<>();
    protected ArrayList<Wine> allItems = MainActivity.dbHelper.getAllWinesList();
    protected WinesListAdapter adapter;
    protected ExpandableListView listView;

    protected boolean query = false;
    protected static ArrayList<String> searchItems;
    protected int selectedItem = 0;
    protected int currentOrder = NAME_ASC;
    protected int previousOrder = NAME_ASC;

    protected TextView allItemsLabel;
    protected TextView queryEntriesLabel;
    protected TextView queryEntriesCountLabel;
    protected EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wines_activity);

        activity = this;

        setParentList();
        setWinesCollection();

        searchItems = new ArrayList<>();
        searchItems.add(getResources().getString(R.string.wines_top_list_name));
        searchItems.add(getResources().getString(R.string.wines_top_list_year));
        searchItems.add(getResources().getString(R.string.wines_top_list_note));
        searchItems.add(getResources().getString(R.string.wines_top_list_appellation));
        searchItems.add(getResources().getString(R.string.wines_label_comment));
        searchItems.add(getResources().getString(R.string.wines_label_seller));
        searchItems.add(getResources().getString(R.string.wines_label_price));

        RelativeLayout back = (RelativeLayout) findViewById(R.id.wines_button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WinesActivity.this.onBackPressed();
            }
        });

        LinearLayout wineNameOrder = (LinearLayout) findViewById(R.id.wines_name);
        wineNameOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentOrder == NAME_ASC)
                    currentOrder = NAME_DESC;
                else
                    currentOrder = NAME_ASC;
                sortList();
            }
        });

        LinearLayout yearOrder = (LinearLayout) findViewById(R.id.wines_year);
        yearOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentOrder == YEAR_ASC)
                    currentOrder = YEAR_DESC;
                else
                    currentOrder = YEAR_ASC;
                sortList();
            }
        });

        LinearLayout noteOrder = (LinearLayout) findViewById(R.id.wines_note);
        noteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentOrder == NOTE_ASC) {
                    currentOrder = NOTE_DESC;
                }
                else {
                    currentOrder = NOTE_ASC;
                }
                sortList();
            }
        });
        search = (EditText) findViewById(R.id.wines_search_text_field);

        setButtonAdd();

        listView = (ExpandableListView) findViewById(R.id.wines_listview);
        adapter = new WinesListAdapter(this, parentList, allWinesCollection);
        listView.setAdapter(adapter);

        listViewModified = (ExpandableListView) findViewById(R.id.wines_listview_modified);
        adapterModified = new WinesListAdapter(this, parentList, modifiedWinesCollection);
        listViewModified.setAdapter(adapterModified);

        allItemsLabel = (TextView) findViewById(R.id.wines_footer_all_entries);
        allItemsLabel.setText("" + allItems.size());
        queryEntriesLabel = (TextView) findViewById(R.id.wines_footer_query_entries);
        queryEntriesCountLabel = (TextView) findViewById(R.id.wines_footer_query_entries_count);
        setSpinner();

        for(int i = 0; i < 3; i++){
            expand(i);
        }
    }

    protected void setButtonAdd(){
        RelativeLayout add = (RelativeLayout) findViewById(R.id.wines_button_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WinesActivity.this, AddWineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setParentList(){
        parentList = new ArrayList<>();
        parentList.add(getResources().getString(R.string.wines_expandable_parent_red));
        parentList.add(getResources().getString(R.string.wines_expandable_parent_rose));
        parentList.add(getResources().getString(R.string.wines_expandable_parent_white));
    }

    private void setWinesCollection(){
        ArrayList<Wine> red = new ArrayList<>();
        ArrayList<Wine> rose = new ArrayList<>();
        ArrayList<Wine> white = new ArrayList<>();

        for(Wine wine : allItems){
            switch(wine.getType()){
                case Wine.RED:
                    red.add(wine);
                    break;
                case Wine.ROSE:
                    rose.add(wine);
                    break;
                case Wine.WHITE:
                    white.add(wine);
                    break;
            }
        }

        allWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_red), red);
        allWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_rose), rose);
        allWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_white), white);

        modifiedWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_red), new ArrayList<Wine>());
        modifiedWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_rose), new ArrayList<Wine>());
        modifiedWinesCollection.put(getResources().getString(R.string.wines_expandable_parent_white), new ArrayList<Wine>());
    }

    public void addItem(Wine wine, boolean changeDb){
        ArrayList<Wine> listAll = allWinesCollection.get(parentList.get(wine.getType() - 1));
        int count = listAll.size();
        if(count == 0){
            listAll.add(wine);
            notifyAdapter();
        }
        else {
            int start = 0;
            int end = count - 1;
            int middle;
            switch (currentOrder) {
                case NAME_ASC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (wine.getName().compareTo(listAll.get(middle).getName()) == 0) {
                            do{
                                middle--;
                            }while(middle >= 0 && wine.getName().compareTo(listAll.get(middle).getName()) == 0);
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (wine.getName().compareTo(listAll.get(middle).getName()) < 0) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                listAll.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
                case NAME_DESC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (wine.getName().compareTo(listAll.get(middle).getName()) == 0) {
                            do{
                                middle--;
                            }while (middle >= 0 && wine.getName().compareTo(listAll.get(middle).getName()) == 0);
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (wine.getName().compareTo(listAll.get(middle).getName()) > 0) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                allItems.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
                case NOTE_ASC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (Float.parseFloat(wine.getNote()) == Float.parseFloat(listAll.get(middle).getNote())) {
                            do{
                                middle--;
                            }while(middle >= 0 && Float.parseFloat(wine.getNote()) == Float.parseFloat(listAll.get(middle).getNote()));
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (Float.parseFloat(wine.getNote()) < Float.parseFloat(listAll.get(middle).getNote())) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                listAll.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
                case NOTE_DESC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (Float.parseFloat(wine.getNote()) == Float.parseFloat(listAll.get(middle).getNote())) {
                            do{
                                middle--;
                            }while(middle >= 0 && Float.parseFloat(wine.getNote()) == Float.parseFloat(listAll.get(middle).getNote()));
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (Float.parseFloat(wine.getNote()) > Float.parseFloat(listAll.get(middle).getNote())) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                listAll.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
                case YEAR_ASC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (wine.getYear() == listAll.get(middle).getYear()) {
                            do{
                                middle--;
                            }while(middle >= 0 && wine.getYear() == listAll.get(middle).getYear());
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (wine.getYear() < listAll.get(middle).getYear()) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                listAll.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
                case YEAR_DESC:
                    while (true) {
                        middle = (int) Math.floor((start + end) / 2);
                        if (wine.getYear() == listAll.get(middle).getYear()) {
                            do{
                                middle--;
                            }while(middle >= 0 && wine.getYear() == listAll.get(middle).getYear());
                            middle++;
                            listAll.add(middle, wine);
                            notifyAdapter();
                            break;
                        } else if (wine.getYear() > listAll.get(middle).getYear()) {
                            end = middle - 1;
                            if (start > end) {
                                listAll.add(middle, wine);
                                notifyAdapter();
                                break;
                            }
                        } else {
                            start = middle + 1;
                            if (start > end) {
                                listAll.add(middle + 1, wine);
                                notifyAdapter();
                                break;
                            }
                        }
                    }
                    break;
            }
        }

        if(query){
            boolean ok = false;
            String fieldString = search.getText().toString().toLowerCase();
            switch(selectedItem){
                case 0:
                    if(wine.getName().toLowerCase().contains(fieldString))
                        ok = true;
                    break;
                case 1:
                    if((""+wine.getYear()).contains(fieldString))
                        ok = true;
                    break;
                case 2:
                    if(wine.getNote().contains(fieldString))
                        ok = true;
                    break;
                case 3:
                    if(wine.getAppellation().contains(fieldString))
                        ok = true;
                    break;
                case 4:
                    if(wine.getComment().contains(fieldString))
                        ok = true;
                    break;
                case 5:
                    if(wine.getSeller().contains(fieldString))
                        ok = true;
                    break;
                case 6:
                    if((""+wine.getPrice()).contains(fieldString))
                        ok = true;
                    break;
            }
            if(ok) {
                ArrayList<Wine> listModified = modifiedWinesCollection.get(parentList.get(wine.getType() - 1));
                count = listModified.size();
                if (count == 0) {
                    listModified.add(wine);
                    notifyAdapterModified();
                } else {
                    int start = 0;
                    int end = count - 1;
                    int middle;
                    switch (currentOrder) {
                        case NAME_ASC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (wine.getName().compareTo(listModified.get(middle).getName()) == 0) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && wine.getName().compareTo(listModified.get(middle).getName()) == 0);
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (wine.getName().compareTo(listModified.get(middle).getName()) < 0) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                        case NAME_DESC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (wine.getName().compareTo(listModified.get(middle).getName()) == 0) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && wine.getName().compareTo(listModified.get(middle).getName()) == 0);
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (wine.getName().compareTo(listModified.get(middle).getName()) > 0) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                        case NOTE_ASC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (Float.parseFloat(wine.getNote()) == Float.parseFloat(listModified.get(middle).getNote())) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && Float.parseFloat(wine.getNote()) == Float.parseFloat(listModified.get(middle).getNote()));
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (Float.parseFloat(wine.getNote()) < Float.parseFloat(listModified.get(middle).getNote())) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                        case NOTE_DESC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (Float.parseFloat(wine.getNote()) == Float.parseFloat(listModified.get(middle).getNote())) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && Float.parseFloat(wine.getNote()) == Float.parseFloat(listModified.get(middle).getNote()));
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (Float.parseFloat(wine.getNote()) > Float.parseFloat(listModified.get(middle).getNote())) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                        case YEAR_ASC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (wine.getYear() == listModified.get(middle).getYear()) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && wine.getYear() == listModified.get(middle).getYear());
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (wine.getYear() < listModified.get(middle).getYear()) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                        case YEAR_DESC:
                            while (true) {
                                middle = (int) Math.floor((start + end) / 2);
                                if (wine.getYear() == listModified.get(middle).getYear()) {
                                    do {
                                        middle--;
                                    }
                                    while (middle >= 0 && wine.getYear() == listModified.get(middle).getYear());
                                    middle++;
                                    listModified.add(middle, wine);
                                    notifyAdapterModified();
                                    break;
                                } else if (wine.getYear() > listModified.get(middle).getYear()) {
                                    end = middle - 1;
                                    if (start > end) {
                                        listModified.add(middle, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                } else {
                                    start = middle + 1;
                                    if (start > end) {
                                        listModified.add(middle + 1, wine);
                                        notifyAdapterModified();
                                        break;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        if(changeDb)
            addInDb(wine);
    }

    public void addInDb(Wine wine){
        MainActivity.dbHelper.addWine(wine);
    }

    public void removeItem(final Wine wine, int id){
        allWinesCollection.get(parentList.get(wine.getType() - 1)).remove(wine);
        notifyAdapter();
        if(query){
            modifiedWinesCollection.get(parentList.get(wine.getType() - 1)).remove(wine);
            notifyAdapterModified();
        }
        removeDb(wine);
    }

    public void removeDb(Wine wine){
        MainActivity.dbHelper.removeWine(wine);
    }

    public void updateItem(Wine wine){
        if(query){
            ArrayList<Wine> list = modifiedWinesCollection.get(parentList.get(wine.getType() - 1));
            for(int i = 0; i < list.size(); i++){
                if (list.get(i).getId() == wine.getId()){
                    list.remove(i);
                    break;
                }
            }
        }

        ArrayList<Wine> list = allWinesCollection.get(parentList.get(wine.getType() - 1));
        for(int i = 0; i < list.size(); i++){
            if (list.get(i).getId() == wine.getId()){
                list.remove(i);
                addItem(wine, false);
                break;
            }
        }

        MainActivity.dbHelper.updateWine(wine);
    }

    public void updateWineImages(long id, int type, String imagePath, String thumbPath) {
        MainActivity.dbHelper.updateImages(id, imagePath, thumbPath);
        if(query) {
            for (Wine wine : modifiedWinesCollection.get(parentList.get(type-1))) {
                if (wine.getId() == id) {
                    wine.setImage(imagePath);
                    wine.setThumbnail(thumbPath);
                    notifyAdapterModified();
                }
            }
        }
        for(Wine wine: allWinesCollection.get(parentList.get(type-1))){
            if (wine.getId() == id) {
                wine.setImage(imagePath);
                wine.setThumbnail(thumbPath);
                notifyAdapter();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == THUMBNAIL_REQUEST && resultCode == RESULT_OK) {
            long id = data.getExtras().getLong("id");
            updateWineImages(id, data.getExtras().getInt("type"), data.getExtras().getString("image"), data.getExtras().getString("thumbnail"));
        }
    }

    public void setSpinner(){

        RelativeLayout spinner = (RelativeLayout) findViewById(R.id.wines_search_spinner);
        final ListView searchList = (ListView) findViewById(R.id.wines_search_spinner_list);

        final TextView spinnerText = (TextView) findViewById(R.id.wines_search_spinner_selected);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, searchItems);
        searchList.setAdapter(spinnerAdapter);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                spinnerText.setText(searchItems.get(position));
                searchList.setVisibility(View.GONE);
            }
        });

        final LinearLayout searchBar = (LinearLayout) findViewById(R.id.wines_search_bar);
        RelativeLayout searchButton = (RelativeLayout) findViewById(R.id.wines_search_bar_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBar.getVisibility() == View.GONE) {
                    searchBar.setVisibility(View.VISIBLE);
                }
                else {
                    searchBar.setVisibility(View.GONE);
                    if(query) {
                        listViewModified.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        setQuery(false);
                    }
                    searchList.setVisibility(View.GONE);
                }
            }
        });

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchList.getVisibility() == View.GONE)
                    searchList.setVisibility(View.VISIBLE);
                else
                    searchList.setVisibility(View.GONE);
            }
        });

        final EditText searchField = (EditText) findViewById(R.id.wines_search_text_field);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(searchField);
                    return true;
                }
                return false;
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchField.getText().length() == 0 && query) {
                    listViewModified.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    setQuery(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final RelativeLayout search = (RelativeLayout) findViewById(R.id.wines_search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchField);
            }

        });
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public void search(EditText searchField){
        String str = searchField.getText().toString();
        if (str.length() != 0) {
            switch (selectedItem) {
                case 0:
                    updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_NAME, str);
                    break;
                case 1:
                    if (isNumeric(str)) {
                        updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_YEAR, str);
                    }
                    break;
                case 2:
                    if (isNumeric(str)) {
                        updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_NOTE, str);
                    }
                    break;
                case 3:
                    updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_APPELLATION, str);
                    break;
                case 4:
                    updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_COMMENT, str);
                    break;
                case 5:
                    updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_SELLER, str);
                    break;
                case 6:
                    updateListForQuery(WinesFeedContract.FeedEntry.COLUMN_WINE_PRICE, str);
                    break;
            }
        }

        if(query){
            AddWineActivity.hideKeyboard(this, searchField);
        }
    }

    public void sortList(){
        ArrayList<Wine> list;
        if(query) {
            boolean notify = false;
            for(int j = 0; j < 3; j ++) {
                list = modifiedWinesCollection.get(parentList.get(j));
                int count = list.size();
                if (count == 0)
                    continue;
                notify = true;
                if ((previousOrder == 1 && currentOrder == 0) ||
                        (previousOrder == 0 && currentOrder == 1) ||
                        (previousOrder == 2 && currentOrder == 3) ||
                        (previousOrder == 3 && currentOrder == 2) ||
                        (previousOrder == 4 && currentOrder == 5) ||
                        (previousOrder == 5 && currentOrder == 4)) {
                    int operations = (int) Math.floor((float) count / 2);
                    Wine temp;
                    for (int i = 0; i < operations; i++) {
                        temp = list.get(i);
                        list.set(i, list.get(count - 1 - i));
                        list.set(count - 1 - i, temp);
                    }

                } else {
                    switch (currentOrder) {
                        case NAME_DESC:
                            quickSortNameDesc(0, count - 1, list);
                            break;
                        case NAME_ASC:
                            quickSortNameAsc(0, count - 1, list);
                            break;
                        case YEAR_DESC:
                            quickSortYearDesc(0, count - 1, list);
                            break;
                        case YEAR_ASC:
                            quickSortYearAsc(0, count - 1, list);
                            break;
                        case NOTE_DESC:
                            quickSortNoteDesc(0, count - 1, list);
                            break;
                        case NOTE_ASC:
                            quickSortNoteAsc(0, count - 1, list);
                            break;
                    }

                }
            }
            previousOrder = currentOrder;
            if(notify)
                notifyAdapterModified();
        }
        else{
            boolean notify = false;
            for(int j = 0; j < 3; j++) {
                list = allWinesCollection.get(parentList.get(j));
                int count = list.size();
                if (count == 0)
                    continue;
                notify = true;
                if ((previousOrder == 1 && currentOrder == 0) ||
                        (previousOrder == 0 && currentOrder == 1) ||
                        (previousOrder == 2 && currentOrder == 3) ||
                        (previousOrder == 3 && currentOrder == 2) ||
                        (previousOrder == 4 && currentOrder == 5) ||
                        (previousOrder == 5 && currentOrder == 4)) {
                    int operations = (int) Math.floor((float) count / 2);
                    Wine temp;
                    for (int i = 0; i < operations; i++) {
                        temp = list.get(i);
                        list.set(i, list.get(count - 1 - i));
                        list.set(count - 1 - i, temp);
                    }

                } else {
                    switch (currentOrder) {
                        case NAME_DESC:
                            quickSortNameDesc(0, count - 1, list);
                            break;
                        case NAME_ASC:
                            quickSortNameAsc(0, count - 1, list);
                            break;
                        case YEAR_DESC:
                            quickSortYearDesc(0, count - 1, list);
                            break;
                        case YEAR_ASC:
                            quickSortYearAsc(0, count - 1, list);
                            break;
                        case NOTE_DESC:
                            quickSortNoteDesc(0, count - 1, list);
                            break;
                        case NOTE_ASC:
                            quickSortNoteAsc(0, count - 1, list);
                            break;
                    }

                }
            }
            previousOrder = currentOrder;
            if(notify)
                notifyAdapter();
        }

    }

    private void quickSortNameDesc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex + (higherIndex - lowerIndex) / 2);
        while (i <= j) {
            while (list.get(i).getName().compareTo(pivot.getName()) > 0) {
                i++;
            }
            while (list.get(j).getName().compareTo(pivot.getName()) < 0) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortNameDesc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortNameDesc(i, higherIndex, list);
    }

    private void quickSortNameAsc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex+(higherIndex-lowerIndex)/2);
        while (i <= j) {
            while (list.get(i).getName().compareTo(pivot.getName()) < 0) {
                i++;
            }
            while (list.get(j).getName().compareTo(pivot.getName()) > 0) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortNameAsc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortNameAsc(i, higherIndex, list);
    }

    private void quickSortYearDesc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex+(higherIndex-lowerIndex)/2);
        while (i <= j) {
            while (list.get(i).getYear() > pivot.getYear()) {
                i++;
            }
            while (list.get(j).getYear() < pivot.getYear()) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortYearDesc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortYearDesc(i, higherIndex, list);
    }

    private void quickSortYearAsc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex+(higherIndex-lowerIndex)/2);
        while (i <= j) {
            while (list.get(i).getYear()< pivot.getYear()) {
                i++;
            }
            while (list.get(j).getYear() > pivot.getYear()) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortYearAsc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortYearAsc(i, higherIndex, list);
    }

    private void quickSortNoteDesc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex+(higherIndex-lowerIndex)/2);
        while (i <= j) {
            float pivotFloat = Float.parseFloat(pivot.getNote());
            while (Float.parseFloat(list.get(i).getNote()) > pivotFloat) {
                i++;
            }
            while (Float.parseFloat(list.get(j).getNote()) < pivotFloat) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortNoteDesc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortNoteDesc(i, higherIndex, list);
    }

    private void quickSortNoteAsc(int lowerIndex, int higherIndex, ArrayList<Wine> list) {

        int i = lowerIndex;
        int j = higherIndex;
        Wine pivot = list.get(lowerIndex+(higherIndex-lowerIndex)/2);
        while (i <= j) {
            float pivotFloat = Float.parseFloat(pivot.getNote());
            while (Float.parseFloat(list.get(i).getNote()) < pivotFloat) {
                i++;
            }
            while (Float.parseFloat(list.get(j).getNote()) > pivotFloat) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j, list);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSortNoteAsc(lowerIndex, j, list);
        if (i < higherIndex)
            quickSortNoteAsc(i, higherIndex, list);
    }

    private void exchangeNumbers(int i, int j, ArrayList<Wine> list) {
        Wine temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    protected void updateListForQuery(String column, String query){
        setQuery(true);
        for(int i = 0; i < 3; i++) {
            modifiedWinesCollection.get(parentList.get(i)).clear();
            modifiedWinesCollection.get(parentList.get(i)).addAll(MainActivity.dbHelper.getWineList(column, query, currentOrder, i+1));
        }

        if(listView.getVisibility() == View.VISIBLE){
            listView.setVisibility(View.GONE);
            listViewModified.setVisibility(View.VISIBLE);
        }

        notifyAdapterModified();
    }

    public void setQuery(boolean query){
        this.query = query;
        if(!query)
            sortList();
        updateFooterLabels();
    }
    
    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
        updateFooterLabels();
    }
    
    public void notifyAdapterModified(){
        adapterModified.notifyDataSetChanged();
        updateFooterLabels();
    }

    public void expand(int groupPosition){
        if(!listView.isGroupExpanded(groupPosition)){
            listView.expandGroup(groupPosition);
        }
        if(!listViewModified.isGroupExpanded(groupPosition)){
            listViewModified.expandGroup(groupPosition);
        }
        /*for(int i = 0; i < 3; i++)
            if(groupPosition != i)
                collapse(i);*/
    }

    public void collapse(int groupPosition){
        if(listView.isGroupExpanded(groupPosition)){
            listView.collapseGroup(groupPosition);
        }
        if(listViewModified.isGroupExpanded(groupPosition)){
            listViewModified.collapseGroup(groupPosition);
        }
    }

    private void updateFooterLabels(){
        if(allItemsLabel != null){
            int allCount = 0;
            for(int i = 0; i < 3; i++){
                allCount += allWinesCollection.get(parentList.get(i)).size();
            }
            allItemsLabel.setText("" + allCount);
            if(query){
                queryEntriesLabel.setVisibility(View.VISIBLE);
                queryEntriesCountLabel.setVisibility(View.VISIBLE);
                int modifiedCount = 0;
                for(int i = 0; i < 3; i++){
                    modifiedCount += modifiedWinesCollection.get(parentList.get(i)).size();
                }
                queryEntriesCountLabel.setText("" + modifiedCount);
            }
            else{
                queryEntriesLabel.setVisibility(View.GONE);
                queryEntriesCountLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
