package com.example.hzshang.faceunlock;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;

import android.util.Log;

import com.example.hzshang.faceunlock.camera.TakePicture;

import java.io.File;
import java.io.FileOutputStream;

/*
*this service is for scan face
*/
public class ScanFace extends Service {
    private boolean busy;
    private ResultReceiver receiver;
    private MyBinder myBinder;

    private void takePic() {
        if (!busy) {
            busy = true;
            TakePicture.StartTakePicture(getApplicationContext(), receiver);
        } else {
            Log.i("ScanFace", "TooBusy!");
        }

    }

    @Override
    public void onCreate() {
        Log.i("ScanFace", "constructor excute");
        busy = false;
        receiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                busy = false;
                Bitmap face = resultData.getParcelable(TakePicture.BITMAP);

                handleFace(face);

            }
        };
        myBinder = new MyBinder();
    }

    private void handleFace(Bitmap face) {
        Log.i("ScanFace", "getFace!");
        //save face
        try {
            ContextWrapper cw = new ContextWrapper(this);
            File dir = cw.getDir("cacheFace", Context.MODE_PRIVATE);
            File facePath = new File(dir, "face");
            FileOutputStream fos;
            fos = new FileOutputStream(facePath);
            face.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent intent = new Intent(this, testActivity.class);
            intent.putExtra("path", facePath.getAbsolutePath());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends Binder {
        void startTakePicAndUnlock() {
            takePic();
        }
    }

}
