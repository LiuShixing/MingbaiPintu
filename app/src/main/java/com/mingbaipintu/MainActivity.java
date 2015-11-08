package com.mingbaipintu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
  px=dp * DisplayMetrics.density/160
 */
public class MainActivity extends Activity {

    private static final String LAST_IMAGE__FROM_APP = "lastImageFromApp";
    private static final String DIFFICULTY = "difficulty";
    private static final String GAME_THROUGHT_BROADCAST = "com.mingbaipintu.GAME_THROUGHT_BROADCAST";
    private static final int MAX_IMAGE = 100;
    private static final String NULL_STR = " ";
    private final int CHOOSE_FROME_PHOTO_ALBUM = 1;

    public static final int CHOOSE_IMAGE = 2;
    public static final String CONFIG_FILE_NAME = "setting";
    public static final String LAST_IMAGE_ADDR = "lastImageAddr";
    public static final String TIMER_BROADCAST = "com.mingbaipintu.TIMER_BROADCAST";
    public static final String CURRENT_MAX_DIFF = "currenMaxDiff";
    private static final String LEVEL = "level";
    public static int mSize = 5;

    public static Context mContext;
    public static Bitmap[] mBitmaps = new Bitmap[MAX_IMAGE];
    public static MyImageView[] mImageViews = new MyImageView[MAX_IMAGE];
    public static FrameLayout mFrameLayout;
    public static int mWidthPixel;
    public static int mHeightPixel;

    private int mGameViewWidth;
    private int mGameViewHeight;
    private int mTitleOffY;
    private long mExitTime;

    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalReceiver mLocalReceiver;
    private Uri mImageUri;
    private SharedPreferences mSharedPreferences;
    private TableLayout mTableLayout;
    private TitleLayout mTitleLayout;
    private ImageView mPre_image;
    private MyListView mMyListView;
    private GameThrought mGameThrought;
    private MyTimerTask myTimerTarsk;

    private boolean mIs_option = false;
    private Bitmap mCurrentImage;
    private Timer mTimer = new Timer();

    private int mCurrentMaxDiff;
    public static int mLevel;
    private int mDiff;
    private boolean mIsCustom;
    private int mUsereTime=0;
    private boolean mIsInGame=false;

    class GameThrought implements View.OnClickListener {
        RelativeLayout relativeLayoutBig;
        ImageView image_gameThrought;
        Button again;
        Button next;
        TextView showMark;
        static final String SHOW_TEXT = "用时";
        boolean is_showGameThroughtView = false;

        GameThrought() {
            relativeLayoutBig = (RelativeLayout) findViewById(R.id.relative_gameThrought);
            image_gameThrought = (ImageView) findViewById(R.id.image_gameThrought);
            showMark = (TextView) findViewById(R.id.showMark);
            again = (Button) findViewById(R.id.again);
            next = (Button) findViewById(R.id.next);
            again.setOnClickListener(this);
            next.setOnClickListener(this);
        }

        void setShowMarkText(int time) {
            int temp_m = time / 60;
            int temp_s = time % 60;
            String ts_m = String.valueOf(temp_m);
            String ts_s = String.valueOf(temp_s);
            String ts = "";
            if (mIsCustom) {
                next.setText("换一张");
                again.setVisibility(View.VISIBLE);
            } else {
                ts = "第" + mLevel + "关";
                next.setText("下一关");
                again.setVisibility(View.INVISIBLE);
            }

            ts += SHOW_TEXT;
            if (temp_m > 0)
                ts += ts_m + "分";
            if (temp_s > 0)
                ts += ts_s + "秒";
            showMark.setText(ts);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.again:
                    concelTimer();
                    mTitleLayout.setIsStart(true, mIsCustom);
                    mPre_image.setVisibility(View.VISIBLE);
                    mTableLayout.setVisibility(View.INVISIBLE);
                    initImageViews();
                    mFrameLayout.setVisibility(View.VISIBLE);
                    relativeLayoutBig.setVisibility(View.INVISIBLE);
                    is_showGameThroughtView = false;
                    break;
                case R.id.next:
                    if (mIsCustom) {
                        startChooseActivity();
                    } else {
                        mTitleLayout.setIsStart(true, mIsCustom);
                        mPre_image.setVisibility(View.VISIBLE);
                        mTableLayout.setVisibility(View.INVISIBLE);

                        Resources res = getResources();
                        int resourceId = ChooseImageActivity.mImagesId[mLevel];
                        Bitmap choosedPic = BitmapFactory.decodeResource(res, resourceId);
                        createGame(choosedPic);
                        mFrameLayout.setVisibility(View.VISIBLE);
                        relativeLayoutBig.setVisibility(View.INVISIBLE);
                        is_showGameThroughtView = false;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_game);

        mContext = this;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixel = dm.widthPixels;
        mHeightPixel = dm.heightPixels;

        int statusHeight = Util.getStatusBarHeight(MyApplication.getContextObject());
        mTitleOffY = (TitleLayout.TITLE_OFF_DP * dm.densityDpi / 160) + statusHeight;
        mGameViewWidth = mWidthPixel;
        mGameViewHeight = mHeightPixel - mTitleOffY;

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.mingbaipintu.TitleLayout.SETTING_BROADCAST);
        intentFilter.addAction(com.mingbaipintu.TitleLayout.QUIT_BROADCAST);
        intentFilter.addAction(TitleLayout.TITLE_ACTION_BROADCAST);
        intentFilter.addAction(MyListView.OPTION_BROADCAST);
        intentFilter.addAction(GAME_THROUGHT_BROADCAST);
        intentFilter.addAction(TIMER_BROADCAST);
        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);

        mTableLayout = (TableLayout) findViewById(R.id.tableLayout);
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mTitleLayout = (TitleLayout) findViewById(R.id.title);
        mPre_image = (ImageView) findViewById(R.id.pre_image);
        mMyListView = (MyListView) findViewById(R.id.listView);

        mGameThrought = new GameThrought();
        mGameThrought.relativeLayoutBig.setVisibility(View.INVISIBLE);

        mMyListView.setVisibility(View.INVISIBLE);

        mSharedPreferences = MyApplication.getContextObject().getSharedPreferences(CONFIG_FILE_NAME, MODE_PRIVATE);
        mLevel = mSharedPreferences.getInt(LEVEL, 1);

        if (mLevel < 26) {
            mIsCustom = false;
            mTitleLayout.setIsStart(true, mIsCustom);
            setDiff(mLevel);
            setSize(mDiff);

            Resources res = getResources();
            int resourceId = ChooseImageActivity.mImagesId[mLevel];
            Bitmap choosedPic = BitmapFactory.decodeResource(res, resourceId);
            createGame(choosedPic);
        } else {
            mLevel = 26;  ////-------------------------------------------
            mIsCustom = true;
            mCurrentMaxDiff = 10;
            mMyListView.setCurrentMaxDiff(mCurrentMaxDiff);
            mMyListView.change(mIsCustom);
            int diff = mSharedPreferences.getInt(DIFFICULTY, 5);
            setSize(diff);

            int lastImageFromApp = mSharedPreferences.getInt(LAST_IMAGE__FROM_APP, -1);
            String lastImageAddr = mSharedPreferences.getString(LAST_IMAGE_ADDR, NULL_STR);
            if (lastImageFromApp == -1 && lastImageAddr.equals(NULL_STR)) {
                Resources res = getResources();
                Bitmap choosedPic = BitmapFactory.decodeResource(res, ChooseImageActivity.mImagesId[mLevel - 1]);
                createGame(choosedPic);
            } else {
                if (lastImageFromApp == -1) {
                    if (!lastImageAddr.equals(NULL_STR)) {
                        File file = new File(lastImageAddr);
                        mImageUri = Uri.fromFile(file);
                        InputStream is = null;
                        try {
                            is = getContentResolver().openInputStream(mImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap choosedPic = BitmapFactory.decodeStream(is);
                        createGame(choosedPic);
                    }
                } else {
                    Resources res = getResources();
                    Bitmap choosedPic = BitmapFactory.decodeResource(res, lastImageFromApp);
                    createGame(choosedPic);
                }
            }
        }

        mPre_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGameThrought.is_showGameThroughtView) {
                    mPre_image.setVisibility(View.INVISIBLE);
                    mTableLayout.setVisibility(View.VISIBLE);
                    startTimer(0);
                    mTitleLayout.setIsStart(false, mIsCustom);
                }
            }
        });
        mPre_image.setVisibility(View.VISIBLE);
        mTableLayout.setVisibility(View.INVISIBLE);
    }

    private void setSize(int size) {
        mSize = size;
        initTableLayout();
    }

    private void setDiff(int level) {
        switch ((level - 1) / 5) {
            case 0:
                mDiff = 3;
                mCurrentMaxDiff = 5;
                break;
            case 1:
                mDiff = 5;
                mCurrentMaxDiff = 5;
                break;
            case 2:
                mDiff = 7;
                mCurrentMaxDiff = 7;
                break;
            case 3:
                mDiff = 9;
                mCurrentMaxDiff = 9;
                break;
            case 4:
                mDiff = 10;
                mCurrentMaxDiff = 10;
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIs_option = false;
        if(mIsInGame) {
            startTimer(mUsereTime);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myTimerTarsk!=null) {
            mUsereTime=concelTimer();
            mIsInGame=true;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(DIFFICULTY, mSize);
        editor.putInt(LEVEL, mLevel);
        editor.commit();
    }

    private void initTableLayout() {
        mTableLayout.removeAllViews();
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mSize; ++i) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(rowParams);
            tableRow.setLayoutParams(rowParams);
            for (int j = 0; j < mSize; ++j) {
                mImageViews[i * mSize + j] = new MyImageView(this, mTitleOffY);
                tableRow.addView(mImageViews[i * mSize + j]);
            }
            mTableLayout.addView(tableRow);
        }
    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case com.mingbaipintu.TitleLayout.SETTING_BROADCAST:
                    if (!mIs_option) {
                        mIs_option = true;
                        mMyListView.setCurrentMaxDiff(mCurrentMaxDiff);
                        mMyListView.setVisibility(View.VISIBLE);
                    } else {
                        mIs_option = false;
                        mMyListView.setVisibility(View.INVISIBLE);
                    }
                    break;
                case com.mingbaipintu.TitleLayout.QUIT_BROADCAST:
                    showDialog();
                    break;
                case MyListView.OPTION_BROADCAST:
                    mIs_option = false;
                    String type = intent.getStringExtra(MyListView.TYPE);
                    if (type != null) {
                        if (type.equals(MyListView.CUSTOM)) {
                            startChooseActivity();
                        }
                        if (type.equals(MyListView.FINGHT)) {
                            if (mLevel > 25) {
                                Toast.makeText(MainActivity.this, "已通关", Toast.LENGTH_LONG).show();
                                return;
                            }
                            mIsCustom = false;
                            concelTimer();
                            mMyListView.change(mIsCustom);

                            mGameThrought.relativeLayoutBig.setVisibility(View.INVISIBLE);
                            mGameThrought.is_showGameThroughtView = false;

                            mTitleLayout.setIsStart(true, mIsCustom);
                            mFrameLayout.setVisibility(View.VISIBLE);
                            mPre_image.setVisibility(View.VISIBLE);
                            mTableLayout.setVisibility(View.INVISIBLE);

                            Resources res = getResources();
                            int resourceId = ChooseImageActivity.mImagesId[mLevel];
                            Bitmap choosedPic = BitmapFactory.decodeResource(res, resourceId);
                            setSize(mDiff);
                            createGame(choosedPic);
                        } else if (type.equals(MyListView.DIFF)) {
                            int diff = intent.getIntExtra(MyListView.DIFF, 0);
                            if (diff != 0) {
                                if (diff > mCurrentMaxDiff) {
                                    if (diff == 7) {
                                        Toast.makeText(MainActivity.this, "该难度将在第11关解锁", Toast.LENGTH_LONG).show();
                                    }
                                    if (diff == 9) {
                                        Toast.makeText(MainActivity.this, "该难度将在第16关解锁", Toast.LENGTH_LONG).show();
                                    }
                                    if (diff == 10) {
                                        Toast.makeText(MainActivity.this, "该难度将在第21关解锁", Toast.LENGTH_LONG).show();
                                    }
                                    Util.MyLog_e_int("diff", diff);
                                    return;
                                }
                                if (diff > 9) {
                                    Toast.makeText(MainActivity.this, "你是人才(^_^)", Toast.LENGTH_SHORT).show();
                                }
                                setSize(diff);
                                concelTimer();
                                if (!mGameThrought.is_showGameThroughtView) {
                                    mTitleLayout.setIsStart(true, mIsCustom);
                                }
                                mPre_image.setImageBitmap(mCurrentImage);
                                splitBitmap(mCurrentImage);
                                initImageViews();
                                mPre_image.setVisibility(View.VISIBLE);
                                mTableLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    break;
                case GAME_THROUGHT_BROADCAST:
                    if (myTimerTarsk != null) {
                        mFrameLayout.setVisibility(View.INVISIBLE);
                        {
                            int time = concelTimer();
                            if(mLevel==25)
                            {
                                mIsCustom = true;
                            }
                            mGameThrought.setShowMarkText(time);
                            if (mLevel <= 25 && !mIsCustom) {
                                mLevel++;
                                int oldDiff = mDiff;
                                setDiff(mLevel);
                                if (mDiff != oldDiff) {
                                    setSize(mDiff);
                                }
                            } else {     //level=26
                                mIsCustom = true;
                            }
                        }
                        mGameThrought.image_gameThrought.setImageBitmap(mCurrentImage);
                        mGameThrought.relativeLayoutBig.setVisibility(View.VISIBLE);
                        mGameThrought.is_showGameThroughtView = true;
                    }
                    break;
                case TIMER_BROADCAST:
                    if (myTimerTarsk != null) {
                        mTitleLayout.setTitleTime(myTimerTarsk.time);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void startChooseActivity() {
        Intent chooseImageIntent = new Intent(MainActivity.this, ChooseImageActivity.class);
        chooseImageIntent.putExtra(ChooseImageActivity.CURRENT_LEVEL,mLevel);
        startActivityForResult(chooseImageIntent, CHOOSE_IMAGE);
    }

    class MyTimerTask extends TimerTask {
        public int time = 0;

        public MyTimerTask(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            time++;
            mLocalBroadcastManager.sendBroadcast(new Intent(TIMER_BROADCAST));
        }
    }

    private int concelTimer() {
        int time=0;
        if(myTimerTarsk!=null) {
            time=myTimerTarsk.time;
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
        myTimerTarsk = null;
        mIsInGame=false;
        return time;
    }

    private void startTimer(int time) {
        mTimer = new Timer();
        myTimerTarsk = new MyTimerTask(time);
        mTimer.schedule(myTimerTarsk, 1000, 1000);
        mIsInGame=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_FROME_PHOTO_ALBUM:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    if (mImageUri != null) {
                        InputStream is = null;
                        try {
                            is = getContentResolver().openInputStream(mImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        mIsCustom = true;
                        mMyListView.change(mIsCustom);
                        Bitmap choosedPic = BitmapFactory.decodeStream(is);
                        createGame(choosedPic);
                        concelTimer();
                        mTitleLayout.setIsStart(true, mIsCustom);
                        mPre_image.setVisibility(View.VISIBLE);
                        mTableLayout.setVisibility(View.INVISIBLE);
                        mFrameLayout.setVisibility(View.VISIBLE);
                        mGameThrought.relativeLayoutBig.setVisibility(View.INVISIBLE);
                        mGameThrought.is_showGameThroughtView = false;
                    } else {
                        Toast.makeText(this, "图片没找到！", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String addr = getImagePathFromUri(mImageUri);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(LAST_IMAGE_ADDR, addr);
                    editor.putInt(LAST_IMAGE__FROM_APP, -1);
                    editor.commit();
                }
                break;
            case CHOOSE_IMAGE:
                if (resultCode == RESULT_OK) {

                    Integer imageId = data.getIntExtra(ChooseImageActivity.IMAGE_SOURCE_ID, -1);
                    int position = data.getIntExtra(ChooseImageActivity.CHOOSE_FROM, -1);
                    if (position == 0) {
                        choosePicFromAlbum();
                    } else {
                        if (imageId != -1) {
                            mIsCustom = true;
                            mMyListView.change(mIsCustom);
                            concelTimer();
                            Util.MyLog_e_int("choosePic",1);
                            mTitleLayout.setIsStart(true, mIsCustom);

                            mFrameLayout.setVisibility(View.VISIBLE);
                            mPre_image.setVisibility(View.VISIBLE);
                            mTableLayout.setVisibility(View.INVISIBLE);

                            Resources res = getResources();
                            Bitmap choosedPic = BitmapFactory.decodeResource(res, imageId);
                            createGame(choosedPic);

                            mGameThrought.relativeLayoutBig.setVisibility(View.INVISIBLE);
                            mGameThrought.is_showGameThroughtView = false;

                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(LAST_IMAGE__FROM_APP, imageId);
                            editor.commit();
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

    public static void exchangeImage(int objectIndex1, int objectIndex2) {
        int bitmapIndex1 = mImageViews[objectIndex1].getmBitmapIndex();
        int bitmapIndex2 = mImageViews[objectIndex2].getmBitmapIndex();
        mImageViews[objectIndex1].setImage(mBitmaps[bitmapIndex2]);
        mImageViews[objectIndex1].setmBitmapIndex(bitmapIndex2);
        mImageViews[objectIndex2].setImage(mBitmaps[bitmapIndex1]);
        mImageViews[objectIndex2].setmBitmapIndex(bitmapIndex1);
    }

    public static void check() {
        boolean is = true;
        for (int i = 0; i < mSize * mSize; ++i) {
            if (mImageViews[i].getmBitmapIndex() != i) {
                is = false;
                break;
            }
        }
        is = true;
        if (is) {
            LocalBroadcastManager.getInstance(MainActivity.mContext).sendBroadcast(new Intent(GAME_THROUGHT_BROADCAST));
        }
    }

    private String getImagePathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        return actualimagecursor.getString(actual_image_column_index);
    }

    private void createGame(Bitmap chooseImage) {
        chooseImage = enlargeImage(chooseImage, mGameViewWidth, mGameViewHeight);
        recycleBitmap(mCurrentImage);
        mCurrentImage = cropBitmap(chooseImage);
        recycleBitmap(chooseImage);
        mPre_image.setImageBitmap(mCurrentImage);
        splitBitmap(mCurrentImage);
        initImageViews();
    }

    private void recycleBitmap(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
            //    System.gc();
        }
    }

    private void initImageViews() {
        int[] m = new int[mSize * mSize];
        for (int i = 0; i < mSize * mSize; ++i)
            m[i] = 0;
        for (int i = 0; i < mSize * mSize; i++) {
            int index;
            Random rand = new Random();
            index = rand.nextInt(mSize * mSize);
            while (m[index] == 1) {
                index = rand.nextInt(mSize * mSize);
            }
            m[index] = 1;
            mImageViews[i].setImageBitmap(mBitmaps[index]);
            mImageViews[i].setIndex(i);
            mImageViews[i].setmBitmapIndex(index);
        }
    }

    private Bitmap cropBitmap(Bitmap bm) {
        int hW = bm.getWidth() / 2;
        int hH = bm.getHeight() / 2;
        int x = hW - mGameViewWidth / 2;
        int y = hH - mGameViewHeight / 2;

        return Bitmap.createBitmap(bm, x, y, mGameViewWidth, mGameViewHeight);
    }


    private Bitmap enlargeImage(Bitmap bm, int width, int height) {
        if (bm.getWidth() < width) {
            float scale = width / (float) bm.getWidth();
            bm = Util.enlargeBitmap(bm, scale);
        }
        if (bm.getHeight() < height) {
            float scale = (float) height / (float) bm.getHeight();
            bm = Util.enlargeBitmap(bm, scale);
        }
        return bm;
    }

    private void splitBitmap(Bitmap bm) {
        int f = mSize;
        int width = bm.getWidth();
        int height = bm.getHeight();
        int w = width / f;
        int h = height / f;


        for (int i = 0; i < mSize; ++i) {
            for (int j = 0; j < mSize; j++) {
                recycleBitmap(mBitmaps[i * mSize + j]);
                mBitmaps[i * mSize + j] = Bitmap.createBitmap(bm, j * w, i * h, w, h);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMyListView.getVisibility() == View.VISIBLE) {
                mMyListView.setVisibility(View.INVISIBLE);
                mIs_option = false;
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
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                MainActivity.this.finish();
            }
        });
        builder.create().show();
    }
}
