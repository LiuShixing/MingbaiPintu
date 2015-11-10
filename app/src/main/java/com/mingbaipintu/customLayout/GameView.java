package com.mingbaipintu.customLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.mingbaipintu.GameManager;
import com.mingbaipintu.R;

/**
 * Created by DanDan on 2015/11/9.
 */
public class GameView extends FrameLayout implements View.OnClickListener{
    private TableLayout mGamingView;
    private ImageView mGameWillBeginView;


    public GameView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.game_view, this);

        mGamingView= (TableLayout) findViewById(R.id.gamingView);
        mGameWillBeginView= (ImageView) findViewById(R.id.gameWillBeginView);
        mGameWillBeginView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gameWillBeginView:
                GameManager.getInstance().gameBegin();
                break;
        }
    }
    public void clearGamingView()
    {
        mGamingView.removeAllViews();
    }
    public void addItemToGamingView(View v)
    {
        mGamingView.addView(v);
    }

    public void showGamingView()
    {
        mGamingView.setVisibility(VISIBLE);
        mGameWillBeginView.setVisibility(INVISIBLE);
    }
    public void showGameWillBeginView(Bitmap currentImage)
    {
        mGameWillBeginView.setImageBitmap(currentImage);
        mGameWillBeginView.setVisibility(VISIBLE);
        mGamingView.setVisibility(INVISIBLE);
    }


}
