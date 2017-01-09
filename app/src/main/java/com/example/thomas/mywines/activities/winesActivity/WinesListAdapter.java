package com.example.thomas.mywines.activities.winesActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.overrides.ConfirmDialog;
import com.example.thomas.mywines.activities.overrides.ParentListLayout;
import com.example.thomas.mywines.informationclasses.Currency;
import com.example.thomas.mywines.informationclasses.Score;
import com.example.thomas.mywines.informationclasses.Wine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class WinesListAdapter extends BaseExpandableListAdapter {
    private final WinesActivity activity;
    private final ArrayList<String> groupParent;
    Map<String, ArrayList<Wine>> winesCollection;
    private final ArrayList<Long> ids = new ArrayList<>();

    public WinesListAdapter(WinesActivity activity, ArrayList<String> groupParent, Map<String, ArrayList<Wine>> winesCollection) {
        this.activity = activity;
        this.groupParent = groupParent;
        this.winesCollection = winesCollection;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return winesCollection.get(groupParent.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, final View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.list_row_layout, null);
        }

        final RelativeLayout rowContent = (RelativeLayout) view.findViewById(R.id.wines_row_content);
        final Wine wine = winesCollection.get(groupParent.get(groupPosition)).get(childPosition);
        final long id = wine.getId();
        if(ids.contains(id))
            rowContent.setVisibility(View.VISIBLE);
        else
            rowContent.setVisibility(View.GONE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rowContent.getVisibility() == View.VISIBLE) {
                    rowContent.setVisibility(View.GONE);
                    ids.remove(id);
                } else {
                    rowContent.setVisibility(View.VISIBLE);
                    ids.add(id);
                }
            }
        });

        if(childPosition %2 == 0)
            view.setBackgroundResource(R.drawable.wines_row_odd);
        else
            view.setBackgroundResource(R.drawable.wines_row_even);

        ImageView image = (ImageView) view.findViewById(R.id.wines_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                bundle.putInt("type", wine.getType());
                bundle.putString("image", wine.getImage());
                bundle.putString("thumbnail", wine.getThumbnail());
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, WinesActivity.THUMBNAIL_REQUEST);
            }
        });

        if(wine.getImage().compareTo(Wine.NO_IMAGE)==0) {
            image.setImageBitmap(null);
            image.setBackgroundResource(R.drawable.no_picture);
        }
        else{
            image.setImageBitmap(PictureActivity.loadImageFromStorage(wine.getThumbnail()));
        }

        TextView name = (TextView) view.findViewById(R.id.wines_name);
        name.setText(wine.getName());

        TextView year = (TextView) view.findViewById(R.id.wines_year);
        year.setText("" + wine.getYear());

        String scoring = MainActivity.preferences.getString("score", Score.TWENTY);
        String scoreString = wine.getNote();
        if(scoring.equals(Score.TWENTY)){
            float score = Float.parseFloat(wine.getNote());
            score /= 5;
            scoreString = "" + score;
            if (scoreString.charAt(scoreString.length() - 1) == '0') {
                scoreString = scoreString.substring(0, scoreString.length() - 1);
            }
            if (scoreString.charAt(scoreString.length() - 1) == '.') {
                scoreString = scoreString.substring(0, scoreString.length() - 1);
            }
        }


        TextView note = (TextView) view.findViewById(R.id.wines_note);
        note.setText(scoreString);

        TextView noteEnd = (TextView) view.findViewById(R.id.wines_note_end);
        noteEnd.setText(scoring);

        TextView appellation = (TextView) view.findViewById(R.id.wines_appellation);
        appellation.setText(wine.getAppellation());

        TextView comment = (TextView) view.findViewById(R.id.wines_comment);
        comment.setText(wine.getComment());

        TextView seller = (TextView) view.findViewById(R.id.wines_seller);
        seller.setText(wine.getSeller());

        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        String dateString = format.format(wine.getDate());
        TextView date = (TextView) view.findViewById(R.id.wines_date);
        date.setText(dateString);

        if(wine.getPrice() != -1) {
            String priceString = "" + wine.getPrice();
            if (priceString.charAt(priceString.length() - 1) == '0') {
                priceString = priceString.substring(0, priceString.length() - 1);
            }
            if (priceString.charAt(priceString.length() - 1) == '.') {
                priceString = priceString.substring(0, priceString.length() - 1);
            }
            TextView price = (TextView) view.findViewById(R.id.wines_price);
            price.setText(priceString + " " + Currency.getCurrentCurrency());
        }

        Button delete = (Button) view.findViewById(R.id.wines_button_delete);
        final View vi = view;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog dialog = new ConfirmDialog(activity, R.style.Dialog);
                dialog.setObjectToDelete(wine);
                dialog.setViewId(vi.getId());
                dialog.show();
            }
        });

        final String score = scoreString;
        Button modify = (Button) view.findViewById(R.id.wines_button_modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ModifyWineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", wine.getId());
                bundle.putInt("type", wine.getType());
                bundle.putString("name", wine.getName());
                bundle.putString("appellation", wine.getAppellation());
                bundle.putString("note", score);
                bundle.putInt("year", wine.getYear());
                bundle.putString("comment", wine.getComment());
                bundle.putString("seller", wine.getSeller());
                bundle.putFloat("price", wine.getPrice());
                bundle.putLong("date", wine.getDate().getTime());
                bundle.putString("image", wine.getImage());
                bundle.putString("thumbnail", wine.getThumbnail());
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });
        return view;

    }

    public int getChildrenCount(int groupPosition) {
        return winesCollection.get(groupParent.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return groupParent.get(groupPosition);
    }

    public int getGroupCount() {
        return groupParent.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String text = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandablelist_parent, null);
        }

        ParentListLayout layout = (ParentListLayout) convertView.findViewById(R.id.wines_expandable_parent);
        switch(groupPosition){
            case 0:
                layout.paintRed();
                break;
            case 1:
                layout.paintRose();
                break;
            case 2:
                layout.paintWhite();
                break;
        }
        TextView item = (TextView) convertView.findViewById(R.id.wines_expandable_text);
        item.setText(text);

        LinearLayout hidden = (LinearLayout) convertView.findViewById(R.id.expanded_menu_hidden);

        ImageView image = (ImageView) convertView.findViewById(R.id.wines_expandable_image);
        if(isExpanded){
            image.setImageResource(R.drawable.expandable_indactor_down);
            if(winesCollection.get(groupParent.get(groupPosition)).size() == 0)
                hidden.setVisibility(View.VISIBLE);
            else
                hidden.setVisibility(View.GONE);
        }
        else{
            image.setImageResource(R.drawable.expandable_indactor_up);
            hidden.setVisibility(View.GONE);
        }

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        activity.expand(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        activity.collapse(groupPosition);
    }
}