package com.example.qrcode.manager;

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
    public void init(CameraStatusListener cameraStatusListener, QRCodeListener qrCodeListener, int[] constraints) {
        VideoStreamManager.getInstance().registerListener(cameraStatusListener);
        VideoStreamManager.getInstance().initVideoStream();

        QRCodeRecognition.getInstance().registerListener(qrCodeListener);
        QRCodeRecognition.getInstance().setConstraints(constraints);
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
