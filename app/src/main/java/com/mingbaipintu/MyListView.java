package com.mingbaipintu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanDan on 2015/10/13.
 */
public class MyListView extends ListView {

    public static final String CUSTOM = "自定义";
    public static final String FINGHT= "闯关";
    private static final String[] DIFFICULTY = {" 简单", " 普通", " 困难", " 大师", " 大神"};//3，5，7，9，10
    public static final String TYPE = "type";
    public static final String DIFF = "diffculty";
    public static final String OPTION_BROADCAST = "com.mingbaipintu.OPTION_BROADCAST";
    private int mCurrentMaxDiff = 5;
    public MyListView(Context context) {
        super(context);
        init(false);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false);
    }
    public void setCurrentMaxDiff(int maxDiff) {
        mCurrentMaxDiff = maxDiff;
    }

    private void init(boolean isCustom) {
        setAlpha(0.8f);
        List<String> dataList;
        if(isCustom) {
            dataList = new ArrayList<>();
            dataList.add(CUSTOM);
            dataList.add(FINGHT);
            dataList.add(DIFFICULTY[0]);
            dataList.add(DIFFICULTY[1]);
            dataList.add(DIFFICULTY[2]);
            dataList.add(DIFFICULTY[3]);
            dataList.add(DIFFICULTY[4]);
        }
        else
        {
            dataList = new ArrayList<>();
            dataList.add(CUSTOM);
        }
        MyAdapter adapter = new MyAdapter(MyApplication.getContextObject(), R.layout.option_item, dataList);
        setAdapter(adapter);
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setVisibility(INVISIBLE);
                if (position == 0) {
                    Intent intentChance = new Intent(OPTION_BROADCAST);
                    intentChance.putExtra(TYPE, CUSTOM);
                    localBroadcastManager.sendBroadcast(intentChance);
                } if(position == 1) {
                    Intent intentChance = new Intent(OPTION_BROADCAST);
                    intentChance.putExtra(TYPE, FINGHT);
                    localBroadcastManager.sendBroadcast(intentChance);
                }else if (position > 1 ) {
                    int diff = 3;
                    switch (position) {
                        case 2:
                            diff = 3;
                            break;
                        case 3:
                            diff = 5;
                            break;
                        case 4:
                            diff = 7;
                            break;
                        case 5:
                            diff = 9;
                            break;
                        case 6:
                            diff = 10;
                            break;
                    }
                    Intent intentDifficulty = new Intent(OPTION_BROADCAST);
                    intentDifficulty.putExtra(TYPE, DIFF);
                    intentDifficulty.putExtra(DIFF, diff);
                    localBroadcastManager.sendBroadcast(intentDifficulty);
                }
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        private int mResourceId;
        private List<String> mData;

        public MyAdapter(Context context, int textViewResourceId, List<String> objects) {
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
            switch (mCurrentMaxDiff) {
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
    public void change(boolean isCustom)
    {
        init(isCustom);
    }
}
