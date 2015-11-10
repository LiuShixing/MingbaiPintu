package com.mingbaipintu.customLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingbaipintu.GameManager;
import com.mingbaipintu.R;

/**
 * Created by DanDan on 2015/11/9.
 */
public class GameWinView extends RelativeLayout implements View.OnClickListener {
    private ImageView mGameReviewView;
    private TextView mMarkText;
    private Button mAgainButton;
    private Button mNextButton;
    public GameWinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.game_win_view, this);

        mGameReviewView= (ImageView) findViewById(R.id.gameReviewView);
        mMarkText= (TextView) findViewById(R.id.markText);
        mAgainButton= (Button) findViewById(R.id.againButton);
        mNextButton= (Button) findViewById(R.id.nextButton);
        mAgainButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        GameManager gameManager =GameManager.getInstance();
        switch (v.getId()) {
            case R.id.againButton:
                gameManager.gameReady();
                break;
            case R.id.nextButton:
                if (gameManager.IsCustom()) {
                    gameManager.startChooseActivity();
                } else {
                    gameManager.setCurrentImageFromResource(gameManager.getmLevel());
                    gameManager.splitBitmap();
                    gameManager.gameReady();
                }
                break;

        }
    }
    public void setMarkText(int time,String str) {
        int temp_m = time / 60;
        int temp_s = time % 60;
        String ts_m = String.valueOf(temp_m);
        String ts_s = String.valueOf(temp_s);

        str += "用时";
        if (temp_m > 0)
            str += ts_m + "分";
        if (temp_s > 0)
            str += ts_s + "秒";
        mMarkText.setText(str);
    }
    public void setGameReviewViewImage(Bitmap image)
    {
        mGameReviewView.setImageBitmap(image);
    }
    public void releaseBitmapResourse()
    {
        if(mGameReviewView!=null) {
            mGameReviewView.setImageResource(0);
        }
    }
    public void setAgainButtonVisibility(boolean visibility)
    {
        if(visibility) {
            mAgainButton.setVisibility(VISIBLE);
        }else
        {
            mAgainButton.setVisibility(INVISIBLE);
        }
    }
    public void setNextButtonText(String str)
    {
        mNextButton.setText(str);
    }
}
