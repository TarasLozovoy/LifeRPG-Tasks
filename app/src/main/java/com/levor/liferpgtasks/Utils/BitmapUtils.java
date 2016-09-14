package com.levor.liferpgtasks.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public class BitmapUtils {
    public static Bitmap getScaledBitmap(String filePath, int maxSideSize) throws IOException{
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxSideSize);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null) throw new IOException();

        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int maxSideSize) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > maxSideSize || width > maxSideSize) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested maxSideSize.
            while ((halfHeight / inSampleSize) >= maxSideSize
                    && (halfWidth / inSampleSize) >= maxSideSize) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
