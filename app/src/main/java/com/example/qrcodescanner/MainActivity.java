package com.example.qrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;

import com.example.qrcode.listener.QRCodeListener;
import com.example.qrcode.manager.ScannerManager;
import com.example.qrcode.listener.CameraStatusListener;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final Handler mHandler = new Handler(Looper.myLooper());

    private TextureView preview;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initScreenSize();
        initQRCodeScanner();
    }

    private void initView() {
        preview = findViewById(R.id.container);
        imageView = findViewById(R.id.qrcode);
        imageView.setOnClickListener(view -> ScannerManager.getInstance().resume());
    }

    private void initScreenSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);

        int[] constraints = {100, 100, 300, 300};
        ScannerManager.getInstance().setConstraints(constraints, outMetrics);
    }

    private void initQRCodeScanner() {
        CameraStatusListener cameraStatusListener = new CameraStatusListener() {
            @Override
            public void onCameraIsReady(SurfaceTexture surfaceTexture, int width, int height) {
                preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                        preview.setSurfaceTexture(surfaceTexture);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

                    }
                });

            }

            @Override
            public void onCameraNotReady() {
                Log.d(TAG, "Camera is not ready");
            }
        };

        QRCodeListener qrCodeListener = new QRCodeListener() {
            @Override
            public void onReceiveMessage(String message) {
                Log.d(TAG, message);
                ScannerManager.getInstance().pause();
            }

            @Override
            public void onReceiveImage(Bitmap image) {
                mHandler.post(() -> {
                    imageView.setImageBitmap(image);
                });
            }
        };

        ScannerManager.getInstance().init(cameraStatusListener, qrCodeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScannerManager.getInstance().release();
    }
}