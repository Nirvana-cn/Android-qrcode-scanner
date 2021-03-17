package com.example.qrcode.listener;

import android.graphics.Bitmap;

public interface QRCodeListener {
    void onReceiveMessage(String message);

    void onReceiveImage(Bitmap image);
}
