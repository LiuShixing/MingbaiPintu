package com.mingbaipintu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.mingbaipintu.GameManager;
import com.mingbaipintu.MyApplication;
import com.mingbaipintu.R;
import com.mingbaipintu.UIManager;
import com.mingbaipintu.UpdateTitleTimer;
import com.mingbaipintu.Util;
import com.mingbaipintu.customLayout.TitleLayout;

import java.io.File;

public class MainActivity extends Activity {

    private static final String LAST_IMAGE_INDEX_FROM_APP = "lastImageFromApp";
    private static final String DIFFICULTY = "difficulty";
    private static final String NULL_STR = " ";
    private final int CHOOSE_FROME_PHOTO_ALBUM = 1;

    public static final int CHOOSE_IMAGE = 2;
    public static final String CONFIG_FILE_NAME = "setting";
    public static final String LAST_IMAGE_ADDR = "lastImageAddr";
    private static final String LEVEL = "level";
    private long mExitTime;

    private LocalReceiver mLocalReceiver;
    private SharedPreferences mSharedPreferences;

    private UIManager mUIManager;
    private GameManager mGameManager;

    private String mLastPicAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_game);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int statusHeight = Util.getStatusBarHeight(MyApplication.getContextObject());
        int titleOffY = (TitleLayout.TITLE_OFF_DP * dm.densityDpi / 160) + statusHeight;

        mUIManager = UIManager.getInstance();
        mUIManager.findMajorView(this);
        mUIManager.setmTitleOffY(titleOffY);

        mGameManager=GameManager.getInstance();
        mGameManager.setMainActivityContext(this);
        mGameManager.setScreenPixel(dm.widthPixels, dm.heightPixels);

//-----------
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateTitleTimer.TIMER_BROADCAST);
        mLocalReceiver = new LocalReceiver();
        mGameManager.getmLocalBroadcastManager().registerReceiver(mLocalReceiver, intentFilter);

        mSharedPreferences = MyApplication.getContextObject().getSharedPreferences(CONFIG_FILE_NAME, MODE_PRIVATE);
        int level = mSharedPreferences.getInt(LEVEL, 1);
        int diff = mSharedPreferences.getInt(DIFFICULTY, 3);
        mGameManager.setmLevel(level);
        if (level < 26) {
            mGameManager.setmIsCustom(false);
            mGameManager.setCurrentImageFromResource(mGameManager.getmLevel());
        } else {
            mGameManager.setmIsCustom(true);
            mGameManager.setmDiff(diff);
            int lastImageIndexFromApp = mSharedPreferences.getInt(LAST_IMAGE_INDEX_FROM_APP, -1);
            String lastImageAddr = mSharedPreferences.getString(LAST_IMAGE_ADDR, NULL_STR);
            if (lastImageIndexFromApp == -1 && lastImageAddr.equals(NULL_STR)) {
                mGameManager.setCurrentImageFromResource(1);
            } else {
                if (lastImageIndexFromApp == -1) {
                    if (!lastImageAddr.equals(NULL_STR)) {
                        File file = new File(lastImageAddr);
                        Uri uri = Uri.fromFile(file);
                       mGameManager.setCurrentImageFromUri(uri);
                    }
                } else {
                    mGameManager.setCurrentImageFromResource(lastImageIndexFromApp);
                }
            }
        }
        mUIManager.initGamingView(mGameManager.getmDiff());
        mGameManager.splitBitmap();
        mGameManager.gameReady();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateTitleTimer.getInstance().continueTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UpdateTitleTimer.getInstance().pause();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(DIFFICULTY, mGameManager.getmDiff());
        editor.putInt(LEVEL, mGameManager.getmLevel());
        editor.apply();
  //      editor.commit();
    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UpdateTitleTimer.TIMER_BROADCAST:
                    mUIManager.setTitleTime(intent.getIntExtra(UpdateTitleTimer.TIME,0));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_FROME_PHOTO_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mGameManager.setCurrentImageFromUri(uri);
                    UpdateTitleTimer.getInstance().concelTimer();
                    mGameManager.setmIsCustom(true);
                    mGameManager.splitBitmap();
                    mGameManager.gameReady();

                    mLastPicAddr = getImagePathFromUri(uri);
                }
                break;
            case CHOOSE_IMAGE:
                if (resultCode == RESULT_OK) {
                    Integer imageIndex = data.getIntExtra(ChooseImageActivity.IMAGE_SOURCE_ID_INDEX, -1);
                    if (imageIndex == 0) {
                        choosePicFromAlbum();
                    } else {
                        if (imageIndex != -1) {
                            mGameManager.setmIsCustom(true);
                            UpdateTitleTimer.getInstance().concelTimer();
                            mGameManager.setCurrentImageFromResource(imageIndex);
                            mGameManager.splitBitmap();
                            mGameManager.gameReady();

                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(LAST_IMAGE_INDEX_FROM_APP, imageIndex);
                            editor.apply();
                      //      editor.commit();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void choosePicFromAlbum() {
        Intent intentForPic = new Intent(Intent.ACTION_PICK);
        intentForPic.setType("image/*");
        startActivityForResult(intentForPic, CHOOSE_FROME_PHOTO_ALBUM);
    }

    private String getImagePathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        return actualimagecursor.getString(actual_image_column_index);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mUIManager.isSettingListViewVisible()) {
               mUIManager.hideSettingList();
            } else {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       mGameManager.getmLocalBroadcastManager().unregisterReceiver(mLocalReceiver);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LAST_IMAGE_ADDR, mLastPicAddr);
        editor.putInt(LAST_IMAGE_INDEX_FROM_APP, -1);
        editor.apply();
//        editor.commit();
        System.exit(0);
    }
}
