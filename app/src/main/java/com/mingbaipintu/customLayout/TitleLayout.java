package com.mingbaipintu.customLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mingbaipintu.R;
import com.mingbaipintu.UIManager;
import com.mingbaipintu.Util;


/**
 * Created by DanDan on 2015/10/5.
 */
public class TitleLayout extends LinearLayout implements View.OnClickListener {
    public static final int TITLE_OFF_DP = 45;
    private TextView mTitle;
    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title, this);
        TextView quit = (TextView) findViewById(R.id.quit);
        TextView setting = (TextView) findViewById(R.id.setting);
        quit.setOnClickListener(this);
        setting.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.mytitle);
        mTitle.setText("");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.quit:
                UIManager.getInstance().showDialog();
                break;
            case R.id.setting:
                UIManager.getInstance().operateSettingList();
                Util.showMemoryInformation();
                break;
        }

    }
    public void setTitleText(String str)
    {
        mTitle.setText(str);
    }
}
