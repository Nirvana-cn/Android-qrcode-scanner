package com.example.qrcode.manager;

import android.graphics.Bitmap;
import android.os.HandlerThread;

import com.example.qrcode.listener.QRCodeListener;
import com.example.qrcode.utils.BitmapUtils;
import com.example.qrcode.utils.QRCodeUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


class QRCodeRecognition {
    private static final String TAG = "QRCodeRecognition";

    private static QRCodeRecognition mInstance;

    private final ThreadPoolExecutor executor;
    private float[] constraints;
    private QRCodeListener mListener;

    private QRCodeRecognition() {
        HandlerThread handlerThread = new HandlerThread("QRCodeRecognition");
        handlerThread.start();

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    }


    public static QRCodeRecognition getInstance() {
        if (mInstance == null) {
            mInstance = new QRCodeRecognition();
        }
        return mInstance;
    }

    public void registerListener(QRCodeListener listener) {
        this.mListener = listener;
    }

    public void unregisterListener() {
        this.mListener = null;
    }

    public void release() {
        unregisterListener();
    }

    public void setConstraints(float[] constraints) {
        this.constraints = constraints;
    }

    public void recognizeQRCode(Bitmap origin, int rotationDegrees) {
        if (executor.getQueue().size() < 2) {
            executor.submit(() -> {
                String result;
                Bitmap image = BitmapUtils.rotate(origin, rotationDegrees);
                int width = image.getWidth();
                int height = image.getHeight();
                if (constraints != null) {
                    int left = (int) (constraints[0] * width);
                    int top = (int) (constraints[1] * height);
                    int cropWidth = (int) (constraints[2] * width);
                    int cropHeight = (int) (constraints[3] * width);

                    image = BitmapUtils.crop(image, left, top, cropWidth, cropHeight);
                }

                result = QRCodeUtils.syncDecodeQRCode(image);

                if (mListener != null && result != null) {
                    mListener.onReceiveMessage(result);
                    mListener.onReceiveImage(image);
                }
            });
        }
    }
}
