package com.example.qrcode.utils;

import android.graphics.Bitmap;
import android.media.Image;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import static androidx.core.math.MathUtils.clamp;

public class ImageUtils {
    public static Bitmap yuv420ToBitmap(Image image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int[] argbArray = new int[imageWidth * imageHeight];
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        yBuffer.position(0);

        ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();
        uvBuffer.position(0);
        int r, g, b;
        int yValue, uValue, vValue;

        for (int y = 0; y < imageHeight - 2; y++) {
            for (int x = 0; x < imageWidth - 2; x++) {
                int yIndex = y * imageWidth + x;
                // Y plane should have positive values belonging to [0...255]
                yValue = (yBuffer.get(yIndex) & 0xff);

                int uvx = x / 2;
                int uvy = y / 2;
                // Remember UV values are common for four pixel values.
                // So the actual formula if U & V were in separate plane would be:
                // `pos (for u or v) = (y / 2) * (width / 2) + (x / 2)`
                // But since they are in single plane interleaved the position becomes:
                // `u = 2 * pos`
                // `v = 2 * pos + 1`, if the image is in NV12 format, else reverse.
                int uIndex = uvy * imageWidth + 2 * uvx;
                // ^ Note that here `uvy = y / 2` and `uvx = x / 2`
                int vIndex = uIndex + 1;

                uValue = (uvBuffer.get(uIndex) & 0xff) - 128;
                vValue = (uvBuffer.get(vIndex) & 0xff) - 128;
                r = (int) (yValue + 1.370705f * vValue);
                g = (int) (yValue - (0.698001f * vValue) - (0.337633f * uValue));
                b = (int) (yValue + 1.732446f * uValue);
                r = clamp(r, 0, 255);
                g = clamp(g, 0, 255);
                b = clamp(b, 0, 255);
                // Use 255 for alpha value, no transparency. ARGB values are
                // positioned in each byte of a single 4 byte integer
                // [AAAAAAAARRRRRRRRGGGGGGGGBBBBBBBB]
                argbArray[yIndex] = (255 << 24) | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
            }
        }

        return Bitmap.createBitmap(argbArray, imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
    }
}
