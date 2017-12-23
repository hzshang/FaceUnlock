package com.hzshang.faceunlock.camera;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/*
*   this is a camera service for taking picture background
*/
public class TakePicture extends Service {
    private Camera mCamera;
    private ResultReceiver mReceiver;
    private static final String RESULT_RECEIVER = "RESULT_RECEIVER";
    public static final int RESULT_OK = 0;
    public static final String BITMAP = "BITMAP";


    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void StartTakePicture(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, TakePicture.class);
        intent.putExtra(RESULT_RECEIVER, receiver);
        context.startService(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            throw new IllegalStateException("Must start the service with intent");
        }
        mReceiver = (ResultReceiver) intent.getParcelableExtra(RESULT_RECEIVER);
        beginTakePic(intent);
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void beginTakePic(Intent intent) {
        mCamera = Camera.open(1);
        if (mCamera != null) {
            SurfaceView sv = new SurfaceView(this);
            SurfaceHolder sh = sv.getHolder();
            sv.setZOrderOnTop(true);
            sh.setFormat(PixelFormat.TRANSPARENT);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    1, 1,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSPARENT);
            params.height = 1;
            params.width = 1;
            wm.addView(sv, params);
            sh.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        mCamera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                    try {
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                camera.takePicture(null, null, new Camera.PictureCallback() {
                                    @Override
                                    public void onPictureTaken(byte[] bytes, Camera camera) {
                                        Bitmap face=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        face=rotateBitmap(face,270);
                                        Bundle bundle = new Bundle();
//                                        bundle.putByteArray(BITMAP,face);
                                        bundle.putParcelable(BITMAP,face);
                                        mReceiver.send(RESULT_OK, bundle);
                                        Log.i("TakePicture", "send face");
                                        stopPreview();
                                        stopSelf();
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        Log.i("TakePicture", "AutoFocus Fail");
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });
        }
    }
}
