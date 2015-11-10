package com.mingbaipintu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by DanDan on 2015/10/12.
 */
public class Util {
    public static int getStatusBarHeight(Context context) {
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

    public static void MyLog_e_int(String name, int value) {
        String msg = name + "= " + value;
        Log.e("e", msg);
    }

    public static void showMemoryInformation() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.e("TAG", "Max memory is " + maxMemory + "KB");
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        Log.e("TAG", "totalMemory is " + totalMemory + "KB");

        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        Log.e("TAG", "freeMemory is " + freeMemory + "KB");

        int total_sub_freeMemory = totalMemory - freeMemory;
        Log.e("TAG", "total_sub_freeMemory is " + total_sub_freeMemory + "KB");
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream is, int reqWidth, int reqHeight) throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        byte[] bytes = toByteArray(is);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap cropBitmap(Bitmap bm, int width, int height) {
        int hW = bm.getWidth() / 2;
        int hH = bm.getHeight() / 2;
        int x = hW - width / 2;
        int y = hH - height / 2;

        return Bitmap.createBitmap(bm, x, y, width, height);
    }

    public static Bitmap enlargeBitmap(Bitmap bm, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    public static Bitmap normalizeImage(Bitmap bm, int width, int height) {
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


}
