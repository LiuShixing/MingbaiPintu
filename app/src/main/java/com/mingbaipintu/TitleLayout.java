package com.mingbaipintu;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by DanDan on 2015/10/5.
 */
public class TitleLayout extends LinearLayout {

    public static final String SETTING_BROADCAST = "com.mingbaipintu.SETTING_BROADCAST";
    public static final String QUIT_BROADCAST = "com.mingbaipintu.QUIT_BROADCAST";
    public static final String TITLE_ACTION_BROADCAST = "com.mingbaipintu.TITLE_ACTION_BROADCAST";
    public static final int TITLE_OFF_DP = 45;
    private boolean mIs_start = true;
    private TextView mTitle;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title, this);
        TextView quit = (TextView) findViewById(R.id.quit);
        TextView setting = (TextView) findViewById(R.id.setting);
        mTitle = (TextView) findViewById(R.id.mytitle);
        mTitle.setText("");
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        quit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QUIT_BROADCAST);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SETTING_BROADCAST);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    public void setTitleTime(int time) {
        int temp_m = time / 60;
        int temp_s = time % 60;
        String ts_m = String.valueOf(temp_m);
        String ts_s = String.valueOf(temp_s);
        if (temp_m < 10)
            ts_m = "0" + ts_m;
        if (temp_s < 10)
            ts_s = "0" + ts_s;
        String ts = ts_m + ":" + ts_s;
        mTitle.setText(ts);
    }

    public void setIsStart(boolean is,boolean isCustom) {
        this.mIs_start = is;
        if (is) {
            if(isCustom)
            {
                mTitle.setText("");
            }
            else
            {
                mTitle.setText("第"+MainActivity.mLevel+"关(共25关)");
            }

        } else {
            mTitle.setText("00:00");
        }
    }
}
