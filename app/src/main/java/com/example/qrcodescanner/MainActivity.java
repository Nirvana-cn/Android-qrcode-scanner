package com.example.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import com.example.qrcode.listener.QRCodeListener;
import com.example.qrcode.manager.ScannerManager;
import com.example.qrcode.listener.CameraStatusListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextureView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initQRCodeScanner();
    }

    private void initView() {
        preview = findViewById(R.id.container);
    }

    private void initQRCodeScanner() {
        CameraStatusListener cameraStatusListener = new CameraStatusListener() {
            @Override
            public void onCameraIsReady(SurfaceTexture surfaceTexture, int width, int height) {
                preview.setSurfaceTexture(surfaceTexture);
            }

            @Override
            public void onCameraNotReady() {

            }
        };

        QRCodeListener qrCodeListener = (message) -> {
            Log.d(TAG, message);
            ScannerManager.getInstance().pause();
        };

        int[] constraints = {100, 100, 300, 300};

        ScannerManager.getInstance().init(cameraStatusListener, qrCodeListener, constraints);
    }

}