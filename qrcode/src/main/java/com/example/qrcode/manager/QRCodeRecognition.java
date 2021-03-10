package com.example.qrcode.manager;

import android.graphics.Bitmap;
import android.os.HandlerThread;
import android.util.Log;

import com.example.qrcode.listener.QRCodeListener;
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

    public void setListener(QRCodeListener listener) {
        this.mListener = listener;
    }

    public void setConstraints(int[] constraints) {
        this.constraints = constraints;
    }

    public void recognizeQRCode(Bitmap image) {
        if (executor.getQueue().size() < 2) {
            executor.submit(() -> {
                String result;
                if (constraints != null) {
                    result = QRCodeUtils.syncDecodeQRCode(QRCodeUtils.crop(image, constraints[0], constraints[1], constraints[2], constraints[3]));
                } else {
                    result = QRCodeUtils.syncDecodeQRCode(image);
                }

                if (mListener != null) {
                    mListener.onReceiveMessage(result);
                }
            });
        }
    }
}
