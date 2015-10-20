package com.mingbaipintu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


/**
 * Created by DanDan on 2015/10/6.
 */
public class MyImageView extends ImageView implements View.OnTouchListener {
    private int mTitleOffY;
    private int mIndex;
    private int mBitmapIndex;
    public FlowImage mFlowImage;

    public MyImageView(Context context, int titleOffY) {
        super(context);
        setOnTouchListener(this);
        this.mTitleOffY = titleOffY;
        mFlowImage=new FlowImage(MainActivity.mContext,titleOffY);
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getmBitmapIndex() {
        return mBitmapIndex;
    }

    public void setmBitmapIndex(int bitmapIndex) {
        mBitmapIndex = bitmapIndex;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setAlpha(0.5f);
                    addFloawImage();
                    break;
                case MotionEvent.ACTION_UP:
                    MainActivity.mFrameLayout.removeView(mFlowImage);
                    exchangeImage(event.getRawX(), event.getRawY());
                    setAlpha(1f);
                    MainActivity.check();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mFlowImage != null && mFlowImage.getVisibility() == VISIBLE) {
                        float x = event.getRawX();
                        float y = event.getRawY();
                        x = x - mFlowImage.getWidth() / 2;
                        y = y - mFlowImage.getHeight() / 2;
                        mFlowImage.setLocation((int) x, (int) y);
                    }
                    break;
                default:
                    break;
            }
        return true;
    }

    private void exchangeImage(float rawX, float rawY) {
        int x = (int) rawX;
        int y = (int) rawY - mTitleOffY;
        int index_X = x / getWidth();
        int index_Y = y / getHeight();
        if(index_X>MainActivity.mSize-1)
            index_X=MainActivity.mSize-1;
        if(index_Y>MainActivity.mSize-1)
            index_Y=MainActivity.mSize-1;
        int index = index_Y * MainActivity.mSize + index_X;
        MainActivity.exchangeImage(mIndex, index);
    }

    private void addFloawImage() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        mFlowImage.setImage(MainActivity.mBitmaps[mBitmapIndex]);
        mFlowImage.setLocation(location[0], location[1]);
        mFlowImage.setSelected(true);
        MainActivity.mFrameLayout.addView(mFlowImage);
    }
    public void setImage(Bitmap bm)
    {
        setImageBitmap(bm);
        mFlowImage.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画边框
        Rect rec = canvas.getClipBounds();
        rec.bottom--;
        rec.right--;
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rec, paint);
    }

}