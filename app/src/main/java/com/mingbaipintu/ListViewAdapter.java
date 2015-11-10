package com.mingbaipintu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DanDan on 2015/11/8.
 */
public class ListViewAdapter extends ArrayAdapter<String> {
    private int mResourceId;
    private List<String> mData;

    public ListViewAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        mResourceId = textViewResourceId;
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(mData.get(position));
        int temp = 3;
        switch (GameManager.getInstance().getmCurrentMaxDiff()) {
            case 7:
                temp = 4;
                break;
            case 9:
                temp = 5;
                break;
            case 10:
                temp = 6;
                break;
        }
        if (position > temp) {
            textView.setTextColor(Color.RED);
        }
        return view;
    }
}