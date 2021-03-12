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
    private int[] constraints;
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

    public void setConstraints(int[] constraints) {
        this.constraints = constraints;
    }

    public void recognizeQRCode(Bitmap origin, int rotationDegrees) {
        if (executor.getQueue().size() < 2) {
            executor.submit(() -> {
                Bitmap image = BitmapUtils.rotate(origin, rotationDegrees);
                String result = QRCodeUtils.syncDecodeQRCode(image);

                if (mListener != null) {
                    mListener.onReceiveMessage(result, image);
                }
            });
        }
    }
}
