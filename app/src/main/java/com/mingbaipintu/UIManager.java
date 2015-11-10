package com.mingbaipintu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.mingbaipintu.customLayout.FlowImage;
import com.mingbaipintu.customLayout.GameView;
import com.mingbaipintu.customLayout.GameWinView;
import com.mingbaipintu.customLayout.MyImageView;
import com.mingbaipintu.customLayout.SettingList;
import com.mingbaipintu.customLayout.TitleLayout;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by DanDan on 2015/11/9.
 */
public class UIManager {
    private static UIManager mUIManager=null;
    private Context mContext;
    private TitleLayout mTitleView;
    private GameView mGameView;
    private GameWinView mGameWinView;
    private SettingList mSettingListView;
    private int mTitleOffY;
    private MyImageView[] mImageFragmentViews = new MyImageView[100];
    private HashMap<Integer, FlowImage> mFlowImageMap = new HashMap<>();

    private UIManager() {
        mContext = MyApplication.getContextObject();
    }

    public static UIManager getInstance() {
        if (mUIManager == null) {
            mUIManager = new UIManager();
        }
        return mUIManager;
    }

    public void operateSettingList() {
        if(mSettingListView.getVisibility()==View.INVISIBLE) {
            mSettingListView.setListContent(GameManager.getInstance().IsCustom());
            mSettingListView.setVisibility(View.VISIBLE);
        }else
        {
            mSettingListView.setVisibility(View.INVISIBLE);
        }
    }
    public void hideSettingList()
    {
        mSettingListView.setVisibility(View.INVISIBLE);
    }
    public boolean isSettingListViewVisible()
    {
        if(mSettingListView.getVisibility()==View.VISIBLE)
            return true;
        else
            return false;
    }
    public void showDialog() {
        final Activity mainActivity= GameManager.getInstance().getMainActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setMessage("确认退出吗?");
        builder.setTitle("提示");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mainActivity.finish();
                System.exit(0);
            }
        });
        builder.create().show();
    }
    public int getmTitleOffY() {
        return mTitleOffY;
    }

    public void setmTitleOffY(int mTitleOffY) {
        this.mTitleOffY = mTitleOffY;
    }

    public void findMajorView(Activity activity) {
        mTitleView = (TitleLayout) activity.findViewById(R.id.titleView);
        mGameView = (GameView) activity.findViewById(R.id.gameView);
        mGameWinView = (GameWinView) activity.findViewById(R.id.gameWinView);
        mSettingListView = (SettingList) activity.findViewById(R.id.settingListView);
    }

    public void showGamingView() {
        mGameView.showGamingView();
    }

    public void showGameWillBeginView(Bitmap currentImage) {
        mGameView.showGameWillBeginView(currentImage);
    }
    public void showGameWinView(Bitmap currentImage)
    {
        mGameWinView.setGameReviewViewImage(currentImage);
        mGameWinView.setVisibility(View.VISIBLE);
        mGameView.setVisibility(View.INVISIBLE);
    }
    public void showGameView(Bitmap currentImage)
    {
        mGameView.setVisibility(View.VISIBLE);
        mGameWinView.setVisibility(View.INVISIBLE);
        mGameView.showGameWillBeginView(currentImage);
        initImageFragmentViews();
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
        mTitleView.setTitleText(ts);
    }

    public void setTitleText(String str) {
        mTitleView.setTitleText(str);
    }

    public void initGamingView(int diff) {
        mGameView.clearGamingView();
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < diff; ++i) {
            TableRow tableRow = new TableRow(MyApplication.getContextObject());
            tableRow.setLayoutParams(rowParams);
            tableRow.setLayoutParams(rowParams);
            for (int j = 0; j < diff; ++j) {
                int index = i * diff + j;
                mImageFragmentViews[index] = new MyImageView(MyApplication.getContextObject());
                tableRow.addView(mImageFragmentViews[i * diff + j]);
            }
            mGameView.addItemToGamingView(tableRow);
        }

    }

    public void exchangeImage(int id, float rawX, float rawY, int width, int height) {
        int diff = GameManager.getInstance().getmDiff();
        int x = (int) rawX;
        int y = (int) rawY - mTitleOffY;
        int index_X = x / width;
        int index_Y = y / height;
        if (index_X > diff - 1)
            index_X = diff - 1;
        if (index_Y > diff - 1)
            index_Y = diff - 1;
        int index = index_Y * diff + index_X;

        GameManager gameManager = GameManager.getInstance();
        int bitmapIndex1 = mImageFragmentViews[id].getmBitmapIndex();
        int bitmapIndex2 = mImageFragmentViews[index].getmBitmapIndex();
        mImageFragmentViews[id].setImage(gameManager.getBitmapChip(bitmapIndex2));
        mImageFragmentViews[id].setmBitmapIndex(bitmapIndex2);
        mImageFragmentViews[index].setImage(gameManager.getBitmapChip(bitmapIndex1));
        mImageFragmentViews[index].setmBitmapIndex(bitmapIndex1);

        FlowImage flowImage1 = mFlowImageMap.get(id);
        FlowImage flowImage2 = mFlowImageMap.get(index);
        if (flowImage1 != null) {
            flowImage1.setImage(gameManager.getBitmapChip(bitmapIndex2));
        }
        if (flowImage2 != null) {
            flowImage2.setImage(gameManager.getBitmapChip(bitmapIndex1));
        }
        checkIsWin(gameManager.getmDiff());
    }
    private void checkIsWin(int diff) {
        boolean is = true;
        for (int i = 0; i < diff * diff; ++i) {
            if (mImageFragmentViews[i].getmBitmapIndex() != i) {
                is = false;
                break;
            }
        }
        if (is) {
           GameManager.getInstance().gameWin();
        }
    }
    public void setGameWinMarkText(int time,String str) {
        mGameWinView.setMarkText(time, str);
    }
    public void setGameWinAgainButtonVisibility(boolean visibility)
    {
        mGameWinView.setAgainButtonVisibility(visibility);
    }
    public void setGameWinNextButtonText(String str)
    {
        mGameWinView.setNextButtonText(str);
    }

    public void moveFlowImage(int id, float rawX, float rawY) {
        FlowImage flowImage = mFlowImageMap.get(id);
        if (flowImage != null && flowImage.getVisibility() == View.VISIBLE) {
            float x = rawX;
            float y = rawY;
            x = x - flowImage.getWidth() / 2;
            y = y - flowImage.getHeight() / 2;
            flowImage.setLocation((int) x, (int) y);
        }
    }

    public void addFloawImageInGameView(int x, int y, int id) {
        FlowImage flowImage = new FlowImage(mContext);
        int index=mImageFragmentViews[id].getmBitmapIndex();
        flowImage.setImage(GameManager.getInstance().getBitmapChip(index));
        flowImage.setLocation(x, y);
        flowImage.setSelected(true);
        mGameView.addView(flowImage);
        mFlowImageMap.put(id, flowImage);
    }

    public void removeFloawImageFromGameView(int id) {
        mGameView.removeView(mFlowImageMap.get(id));
    }
    public void initImageFragmentViews() {
        GameManager gameManager = GameManager.getInstance();
        int count=gameManager.getmDiff();
        int[] m = new int[count * count];
        for (int i = 0; i < count * count; ++i)
            m[i] = 0;
        for (int i = 0; i < count * count; i++) {
            int index;
            Random rand = new Random();
            index = rand.nextInt(count * count);
            while (m[index] == 1) {
                index = rand.nextInt(count * count);
            }
            m[index] = 1;
            mImageFragmentViews[i].setImageBitmap(gameManager.getBitmapChip(index));
            mImageFragmentViews[i].setId(i);
            mImageFragmentViews[i].setmBitmapIndex(index);
        }
    }
    public void releaseBitmapResourse(int diff)
    {
        for(int i=0;i<100;i++)
        {
            if(mImageFragmentViews[i]!=null) {
                mImageFragmentViews[i].setImageResource(0);
            }
        }
        for(FlowImage flowImage :mFlowImageMap.values() )
        {
            if(flowImage!=null) {
                flowImage.setImageResource(0);
            }
        }
        mGameView.releaseBitmapResourse();
        mGameWinView.releaseBitmapResourse();
        System.gc();
    }
}
