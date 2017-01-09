package com.example.thomas.mywines.activities.winesActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.informationclasses.Wine;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ModifyWineActivity extends AddWineActivity {

    private Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView title = (TextView) findViewById(R.id.dialog_add_title);
        title.setText(getResources().getString(R.string.dialog_modify_title_type));

        TextView modify = (TextView) findViewById(R.id.dialog_add_save);
        modify.setText(getResources().getString(R.string.dialog_modify));
        setValues();
    }

    @Override
    public void setWineSelectionComponents() {
        super.setWineSelectionComponents();
        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
            red.setBackground(null);
        } else {
            red.setBackgroundDrawable(null);
        }
    }

    public void setValues(){
        bundle = getIntent().getExtras();
        switch(bundle.getInt("type")){
            case Wine.RED:
                wineTypeSelected = red;
                red.setBackgroundResource(R.drawable.dialog_add_type_selected);
                break;
            case Wine.ROSE:
                wineTypeSelected = rose;
                rose.setBackgroundResource(R.drawable.dialog_add_type_selected);
                break;
            case Wine.WHITE:
                wineTypeSelected = white;
                white.setBackgroundResource(R.drawable.dialog_add_type_selected);
                break;
        }
        EditText nameEdit = (EditText) findViewById(R.id.dialog_add_name);
        nameEdit.setText(bundle.getString("name"));
        TextView appellationText = (TextView) findViewById(R.id.dialog_add_appellation_text);
        appellationText.setText(bundle.getString("appellation"));
        EditText noteEdit = (EditText) findViewById(R.id.dialog_add_note);
        noteEdit.setText(bundle.getString("note"));
        EditText commentEdit = (EditText) findViewById(R.id.dialog_add_comment);
        commentEdit.setText(bundle.getString("comment"));
        EditText sellerEdit = (EditText) findViewById(R.id.dialog_add_seller);
        sellerEdit.setText(bundle.getString("seller"));
        EditText yearEdit = (EditText) findViewById(R.id.dialog_add_year);
        yearEdit.setText("" + bundle.getInt("year"));
        EditText priceEdit = (EditText) findViewById(R.id.dialog_add_price);
        if(bundle.getFloat("price") != -1) {
            String priceString = "" + bundle.getFloat("price");
            if(priceString.length() > 1 && priceString.charAt(priceString.length()-1) == '0'){
                priceString = priceString.substring(0, priceString.length()-1);
            }
            if(priceString.charAt(priceString.length()-1) == '.'){
                priceString = priceString.substring(0, priceString.length()-1);
            }
            priceEdit.setText(priceString);
        }

        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        dateToSet.setTime(bundle.getLong("date"));
        TextView dateText = (TextView) findViewById(R.id.dialog_add_date_text);
        dateText.setText(format.format(bundle.getLong("date")));
    }

    @Override
    public void closeActivity(Wine wine){
        wine.setId(bundle.getLong("id"));
        wine.setImage(bundle.getString("image"));
        wine.setThumbnail(bundle.getString("thumbnail"));
        WinesActivity.activity.updateItem(wine);
        finish();
    }
}
