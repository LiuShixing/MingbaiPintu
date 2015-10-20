package com.mingbaipintu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by DanDan on 2015/10/12.
 */
public class Util {
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
    public static void MyLog_e_int(String name,int value)
    {
        String msg= name+"= "+value;
        Log.e("e",msg);
    }
    public static Bitmap enlargeBitmap(Bitmap bm, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
}
