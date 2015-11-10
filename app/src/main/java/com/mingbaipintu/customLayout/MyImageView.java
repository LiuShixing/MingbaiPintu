package com.mingbaipintu.customLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.mingbaipintu.MyApplication;
import com.mingbaipintu.UIManager;


/**
 * Created by DanDan on 2015/10/6.
 */
public class MyImageView extends ImageView implements View.OnTouchListener {
    private int mId;
    private int mBitmapIndex;
    public FlowImage mFlowImage;

    public MyImageView(Context context) {
        super(context);
        setOnTouchListener(this);
        mFlowImage=new FlowImage(MyApplication.getContextObject());
    }
    public void setId(int id)
    {
        this.mId=id;
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
                    int[] location = new int[2];
                    getLocationOnScreen(location);
                    UIManager.getInstance().addFloawImageInGameView(location[0],location[1],mId);
                    break;
                case MotionEvent.ACTION_UP:
                    UIManager.getInstance().removeFloawImageFromGameView(mId);
                    UIManager.getInstance().exchangeImage(mId, event.getRawX(), event.getRawY(), getWidth(), getHeight());
                    setAlpha(1f);
                    break;
                case MotionEvent.ACTION_MOVE:
                    UIManager.getInstance().moveFlowImage(mId,event.getRawX(),event.getRawY());
                    break;
                default:
                    break;
            }
        return true;
    }
    public void setImage(Bitmap bm)
    {
        setImageBitmap(bm);
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