package com.mingbaipintu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseImageActivity extends Activity {

    public static final String IMAGE_SOURCE_ID = "imageID";
    public static final String CHOOSE_FROM = "choose_from";
    private Integer[] mImagesId = {
            R.drawable.cat_144_album_4, R.drawable.fate, R.drawable.dmc5, R.drawable.lily
            , R.drawable.meng_na_lis_ha, R.drawable.lily, R.drawable.lily, R.drawable.lily
            , R.drawable.lily, R.drawable.lily, R.drawable.lily, R.drawable.lily
            , R.drawable.lily, R.drawable.lily, R.drawable.lily, R.drawable.lily
            , R.drawable.lily, R.drawable.lily, R.drawable.lily, R.drawable.lily
            , R.drawable.lily, R.drawable.lily, R.drawable.lily, R.drawable.lily
            , R.drawable.lily, R.drawable.lily, R.drawable.lily, R.drawable.lily
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_image);


        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_SOURCE_ID, mImagesId[position]);
                intent.putExtra(CHOOSE_FROM, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        TextView back = (TextView)findViewById(R.id.back);
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
        }

        @Override
        public int getCount() {
            return mImagesId.length;
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
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(mImagesId[position]);
            return imageView;
        }
    }


}
