package com.example.qrcode.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils {
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        float centerX = bitmap.getWidth() / 2f;
        float centerY = bitmap.getHeight() / 2f;
        matrix.postRotate(angle, centerX, centerY);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    public static Bitmap crop(Bitmap image, int left, int top, int width, int height) {
        return Bitmap.createBitmap(image, left, top, width, height);
    }
}
