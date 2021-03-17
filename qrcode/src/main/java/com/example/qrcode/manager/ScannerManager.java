package com.example.qrcode.manager;

import android.util.DisplayMetrics;

import com.example.qrcode.listener.CameraStatusListener;
import com.example.qrcode.listener.QRCodeListener;

public class ScannerManager {
    private static final String TAG = "ScannerManager";

    private static ScannerManager mInstance;

    private ScannerManager() {

    }


    public static ScannerManager getInstance() {
        if (mInstance == null) {
            mInstance = new ScannerManager();
        }
        return mInstance;
    }

    public void init(CameraStatusListener cameraStatusListener, QRCodeListener qrCodeListener) {
        VideoStreamManager.getInstance().registerListener(cameraStatusListener);
        VideoStreamManager.getInstance().initVideoStream();

        QRCodeRecognition.getInstance().registerListener(qrCodeListener);
    }

    // constraints = [left, top, width, height]
    public void setConstraints(int[] constraints, DisplayMetrics displayMetrics) {
        float[] newConstraints = {0, 0, 1, 1};
        int widthPixel = displayMetrics.widthPixels;
        int heightPixel = displayMetrics.heightPixels;
        float density = displayMetrics.density;
        newConstraints[0] = constraints[0] * density / widthPixel;
        newConstraints[1] = constraints[1] * density / heightPixel;
        newConstraints[2] = constraints[2] * density / widthPixel;
        newConstraints[3] = constraints[3] * density / widthPixel;

        QRCodeRecognition.getInstance().setConstraints(newConstraints);
    }

    public void resume() {
        VideoStreamManager.getInstance().resumeVideoStream();
    }

    public void pause() {
        VideoStreamManager.getInstance().pauseVideoStream();
    }

    public void release() {
        VideoStreamManager.getInstance().release();
        QRCodeRecognition.getInstance().release();
    }
}
