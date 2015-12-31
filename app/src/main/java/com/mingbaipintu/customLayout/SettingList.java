package com.mingbaipintu.customLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mingbaipintu.GameManager;
import com.mingbaipintu.ListViewAdapter;
import com.mingbaipintu.MyApplication;
import com.mingbaipintu.R;
import com.mingbaipintu.UIManager;
import com.mingbaipintu.UpdateTitleTimer;
import com.mingbaipintu.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanDan on 2015/10/13.
 */
public class SettingList extends ListView {

    public static final String CUSTOM = "自定义";
    public static final String FINGHT = "闯关";
    private static final String[] DIFFICULTY = {" 简单", " 普通", " 困难", " 大师", " 大神"};//3，5，7，9，10

    public SettingList(Context context) {
        super(context);
        init();
    }

    public SettingList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setVisibility(INVISIBLE);
        setAlpha(0.8f);
        setListContent(false);
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setVisibility(INVISIBLE);
                GameManager gameManager = GameManager.getInstance();
                int diff=0;
                switch (position) {
                    case 0:
                        gameManager.startChooseActivity();
                        break;
                    case 1:
                        if (gameManager.getmLevel() > 25) {
                            Toast.makeText(gameManager.getMainActivity(), "已通关", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            gameManager.setmIsCustom(false);
                            UpdateTitleTimer.getInstance().concelTimer();
                            gameManager.setCurrentImageFromResource(gameManager.getmLevel());
                            gameManager.caculatemDiff();
                            UIManager.getInstance().initGamingView(gameManager.getmDiff());
                            gameManager.splitBitmap();
                            gameManager.gameReady();
                        }
                        break;
                    case 2:
                        diff=3;
                        break;
                    case 3:
                        diff=5;
                        break;
                    case 4:
                        diff=7;
                        break;
                    case 5:
                        diff=9;
                        break;
                    case 6:
                        diff=10;
                        break;
                }
                if(diff!=0) {
                    if (diff > gameManager.getmCurrentMaxDiff()) {
                        if (diff == 7) {
                            Toast.makeText(gameManager.getMainActivity(), "该难度将在第11关解锁", Toast.LENGTH_LONG).show();
                        }
                        if (diff == 9) {
                            Toast.makeText(gameManager.getMainActivity(), "该难度将在第16关解锁", Toast.LENGTH_LONG).show();
                        }
                        if (diff == 10) {
                            Toast.makeText(gameManager.getMainActivity(), "该难度将在第21关解锁", Toast.LENGTH_LONG).show();
                        }
                        Util.MyLog_e_int("diff", diff);
                        return;
                    }
                    if (diff > 9) {
                        Toast.makeText(gameManager.getMainActivity(), "你是人才(^_^)", Toast.LENGTH_SHORT).show();
                    }
                    gameManager.setmDiff(diff);
                    UpdateTitleTimer.getInstance().concelTimer();
                    UIManager.getInstance().initGamingView(diff);
                    gameManager.splitBitmap();
                    gameManager.gameReady();
                }
            }
        });
    }

    public void setListContent(boolean isCustom) {
        List<String> dataList;
        if (isCustom) {
            dataList = new ArrayList<>();
            dataList.add(CUSTOM);
            dataList.add(FINGHT);
            dataList.add(DIFFICULTY[0]);
            dataList.add(DIFFICULTY[1]);
            dataList.add(DIFFICULTY[2]);
            dataList.add(DIFFICULTY[3]);
            dataList.add(DIFFICULTY[4]);
        } else {
            dataList = new ArrayList<>();
            dataList.add(CUSTOM);
        }
        ListViewAdapter adapter = new ListViewAdapter(MyApplication.getContextObject(), R.layout.option_item, dataList);
        setAdapter(adapter);
    }
}
