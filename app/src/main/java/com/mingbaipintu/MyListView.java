package com.mingbaipintu;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by DanDan on 2015/10/13.
 */
public class MyListView extends ListView {

    public static final String CHANCE = "换一张";
    private static final String[] DIFFICULTY = {" 3X3", " 4X4", " 5X5", " 6X6", " 7X7", " 8X8", " 9X9", "10X10"};
    public static final String TYPE = "type";
    public static final String DIFF = "diffculty";
    public static final String OPTION_BROADCAST = "com.mingbaipintu.OPTION_BROADCAST";

    public MyListView(Context context) {
        super(context);
        init();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setAlpha(0.8f);
        final String[] data = {CHANCE, DIFFICULTY[0], DIFFICULTY[1], DIFFICULTY[2], DIFFICULTY[3],
                DIFFICULTY[4], DIFFICULTY[5], DIFFICULTY[6], DIFFICULTY[7]};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MyApplication.getContextObject(), android.R.layout.simple_list_item_1, data);
        setAdapter(adapter);

        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setVisibility(INVISIBLE);
                if (position == 0) {
                    Intent intentChance = new Intent(OPTION_BROADCAST);
                    intentChance.putExtra(TYPE, CHANCE);
                    localBroadcastManager.sendBroadcast(intentChance);
                } else if (position > 0 && position <= DIFFICULTY.length) {
                    int diff = position + 2;
                    Intent intentDifficulty = new Intent(OPTION_BROADCAST);
                    intentDifficulty.putExtra(TYPE, DIFF);
                    intentDifficulty.putExtra(DIFF, diff);
                    localBroadcastManager.sendBroadcast(intentDifficulty);
                }
            }
        });
    }
}
