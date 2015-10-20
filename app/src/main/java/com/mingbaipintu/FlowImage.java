package com.mingbaipintu;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by DanDan on 2015/10/8.
 */
public class FlowImage extends ImageView {
    private FrameLayout.LayoutParams mParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private int titleOffY;
    private int ScreenWidthPixel;
    private int ScreenHeightPixel;

    public FlowImage(Context context,int titleOffY) {
        super(context);
        ScreenWidthPixel = MainActivity.mWidthPixel;
        ScreenHeightPixel = MainActivity.mHeightPixel;
        this.titleOffY=titleOffY;
    }

    public void setImage(Bitmap bm) {
        setImageBitmap(bm);
    }

    public void setLocation(int x, int y) {
        if (x < 0)
            x = 0;
        if (x > ScreenWidthPixel - getWidth())
            x = ScreenWidthPixel - getWidth();
        if (y < titleOffY)
            y = titleOffY;
        if (y > ScreenHeightPixel - getHeight()-1) //-1后效果理想，可能是因为titleOffY不准确
            y = ScreenHeightPixel - getHeight()-1;
        mParams.leftMargin = x;
        mParams.topMargin = y - titleOffY;
        setLayoutParams(mParams);
    }
}