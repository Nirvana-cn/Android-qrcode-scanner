package com.example.qrcode.listener;

import android.graphics.SurfaceTexture;

public interface CameraStatusListener {
    void onCameraIsReady(SurfaceTexture surfaceTexture, int width, int height);

    void onCameraNotReady();
}
