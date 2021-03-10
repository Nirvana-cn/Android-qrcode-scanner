package com.example.qrcode.manager;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.util.Rational;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.example.qrcode.listener.CameraStatusListener;
import com.example.qrcode.utils.ImageUtils;

class VideoStreamManager {
    private static final String TAG = "VideoStreamManager";

    private static VideoStreamManager mInstance;

    private LifecycleRegistry mLifecycleRegistry;
    private LifecycleOwner mLifecycleOwner;
    private CameraStatusListener mListener;

    private VideoStreamManager() {

    }


    public static VideoStreamManager getInstance() {
        if (mInstance == null) {
            mInstance = new VideoStreamManager();
        }
        return mInstance;
    }

    public void initVideoStream(CameraStatusListener listener) {
        mListener = listener;
        initLifecycleOwner();
        resumeVideoStream();
        openCamera(mLifecycleOwner);
    }

    private void initLifecycleOwner() {
        // 自定义LifecycleOwner管理CameraX
        mLifecycleOwner = () -> mLifecycleRegistry;
        mLifecycleRegistry = new LifecycleRegistry(mLifecycleOwner);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    public void resumeVideoStream() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    public void pauseVideoStream() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    public void release() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    private void openCamera(LifecycleOwner mLifecycleOwner) {
        // 1. preview
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();

        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> {
            // 保存相机SurfaceTexture对象
            SurfaceTexture cameraSurfaceTexture = output.getSurfaceTexture();
            int outputWidth = output.getTextureSize().getWidth();
            int outputHeight = output.getTextureSize().getHeight();
            if (mListener != null) {
                if (cameraSurfaceTexture != null) {
                    mListener.onCameraIsReady(cameraSurfaceTexture, outputWidth, outputHeight);
                } else {
                    mListener.onCameraNotReady();
                }
            }
        });

        // 2. ImageAnalysis
        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetAspectRatio(new Rational(1, 1))
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer(new PhotoAnalyzer());

        CameraX.bindToLifecycle(mLifecycleOwner, preview, imageAnalysis);
    }


    // 获取相机帧数据
    private class PhotoAnalyzer implements ImageAnalysis.Analyzer {

        @Override
        public void analyze(ImageProxy imageProxy, int rotationDegrees) {
            final Image image = imageProxy.getImage();
            if (image == null) {
                return;
            }

            Bitmap bitmapImage = ImageUtils.yuv420ToBitmap(image);

            QRCodeRecognition.getInstance().recognizeQRCode(bitmapImage);

        }
    }
}
