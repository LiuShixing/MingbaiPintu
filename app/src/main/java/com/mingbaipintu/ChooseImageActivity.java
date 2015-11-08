package com.mingbaipintu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class ChooseImageActivity extends Activity {

    public static final String IMAGE_SOURCE_ID = "imageID";
    public static final String CHOOSE_FROM = "choose_from";
    public static final String CURRENT_LEVEL = "current_level";
    private int mImagesCount;
    public static int[] mImagesId = {
            R.drawable.cat_144_album_4, R.drawable.lily_2, R.drawable.wuer, R.drawable.luqiya, R.drawable.wuyue,
            R.drawable.yihu_wuer, R.drawable.wuer_jiefang, R.drawable.quanxuhua, R.drawable.quanxuhua_2,
            R.drawable.yihu_wuer_0, R.drawable.yihu_wuer_1, R.drawable.yihu_wuer_2, R.drawable.yihu_wuer_3,
            R.drawable.yihu_wuer_4, R.drawable.yihu_wuer_5, R.drawable.yihu_wuer_6, R.drawable.yihu_wuer_7,
            R.drawable.yihu_wuer_8, R.drawable.yihu_wuer_9, R.drawable.yihu_wuer_10, R.drawable.yihu_wuer_11,
            R.drawable.yihu_wuer_12, R.drawable.yihu_wuer_13, R.drawable.yihu_wuer_14, R.drawable.yihu_wuer_15,
            R.drawable.yihu_wuer_16, R.drawable.yihu_wuer_17,
            R.drawable.wuyue_1, R.drawable.wuyue_2, R.drawable.wuyue_3, R.drawable.wuyue_4, R.drawable.wuyue_5,
            R.drawable.wuyue_6, R.drawable.wuyue_7, R.drawable.wuyue_8, R.drawable.wuyue_9, R.drawable.wuyue_10,
            R.drawable.wuyue_11, R.drawable.wuyue_12, R.drawable.wuyue_13, R.drawable.wuyue_14, R.drawable.wuyue_15,
            R.drawable.wuyue_16, R.drawable.wuyue_17, R.drawable.wuyue_18, R.drawable.wuyue_19, R.drawable.wuyue_20,
            R.drawable.wuyue_21, R.drawable.wuyue_22, R.drawable.wuyue_23, R.drawable.wuyue_24, R.drawable.wuyue_25,
            R.drawable.wuyue_26, R.drawable.wuyue_27, R.drawable.wuyue_28, R.drawable.wuyue_29, R.drawable.wuyue_30,
            R.drawable.wuyue_31, R.drawable.wuyue_32, R.drawable.wuyue_33, R.drawable.wuyue_34, R.drawable.wuyue_35,
            R.drawable.wuyue_36, R.drawable.wuyue_37, R.drawable.wuyue_38, R.drawable.wuyue_39, R.drawable.wuyue_40,

    };

    private LruCache mMemoryCache;
    private int mImageViewWidth;
    private int mImageViewHeight;
    private GridView mGridView;
    private Set<BitmapWorkerTask> mTaskCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_image);

        mImageViewWidth = (MainActivity.mWidthPixel - 4) / 3;
        mImageViewHeight = 14 * mImageViewWidth / 9;

        Intent intent = getIntent();
        mImagesCount=intent.getIntExtra(CURRENT_LEVEL,1);
        mImagesCount++;//忽略第一张图
        if(mImagesCount>26)
        {
            mImagesCount=26;
        }
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory /20;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return value.getByteCount() / 1024;
            }
        };

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(new ImageAdapter(this));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_SOURCE_ID, mImagesId[position]);
                intent.putExtra(CHOOSE_FROM, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context context) {
            this.mContext = context;
            mTaskCollection = new HashSet<>();
        }

        @Override
        public int getCount() {
            return mImagesCount;
        }

        @Override
        public Object getItem(int position) {
            return mImagesId[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //定义一个ImageView,显示在GridView里
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(mImageViewWidth, mImageViewHeight));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(mImagesId[position]);
            String imageKey = String.valueOf(mImagesId[position]);
            Bitmap image = getBitmapFromMemoryCache(imageKey);
            if (image != null) {
                imageView.setImageBitmap(image);
            } else {
                imageView.setImageResource(R.drawable.placeholder);

                BitmapWorkerTask task = new BitmapWorkerTask();
                mTaskCollection.add(task);
                task.execute(mImagesId[position], position);
            }

            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            Log.e("TAG", "Max memory is " + maxMemory + "KB");
            int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
            Log.e("TAG", "totalMemory is " + totalMemory + "KB");

            return imageView;
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return (Bitmap) mMemoryCache.get(key);
    }


    /*
    class  BitmapWorkerTask
     */
    class BitmapWorkerTask extends AsyncTask {
        int mId;
        int mPosition=0;

        public BitmapWorkerTask() {
        }

        public int getmPosition() {
            return mPosition;
        }

        @Override
        protected Bitmap doInBackground(Object[] params) {
            mId = (int) params[0];
            mPosition = (int) params[1];
            Bitmap bitmap = Util.decodeSampledBitmapFromResource(getResources(), mId, mImageViewWidth, mImageViewHeight);
            if (bitmap != null) {
                addBitmapToMemoryCache(String.valueOf((int) params[0]), bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ImageView imageView = (ImageView) mGridView.findViewWithTag(mId);
            if (o != null && imageView!=null) {
                imageView.setImageBitmap((Bitmap) o);
            }
            mTaskCollection.remove(this);
        }
    }
}


