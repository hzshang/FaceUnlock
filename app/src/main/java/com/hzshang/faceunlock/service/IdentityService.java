package com.hzshang.faceunlock.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import com.hzshang.faceunlock.camera.TakePicture;
import com.hzshang.faceunlock.common.Message;
import com.hzshang.faceunlock.lib.Async;
import com.hzshang.faceunlock.lib.Identify;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.ByteArrayOutputStream;

/*
*this service is for scan face and identity
*/
public class IdentityService extends Service {
    private boolean busy;
    private ResultReceiver receiver;
    private final double threshold=40.0;

    private void takePic() {
        if (!busy) {
            busy = true;
            TakePicture.StartTakePicture(getApplicationContext(), receiver);
        } else {
            Log.i("IdentityService", "TooBusy!");
            EventBus.getDefault().post(Message.FACE_FAIL);
        }
    }

    @Override
    public void onCreate() {
        busy = false;
        receiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                busy = false;
                Bitmap face = resultData.getParcelable(TakePicture.BITMAP);
                handleFace(face);
            }
        };
    }

    private void handleFace(Bitmap face) {
        Log.i("IdentityService", "getFace!");
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            face.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            new Identify(this, new Async.interFace<Double,String>() {
                @Override
                public void processFinish(Double out) {
                    handleConfidence(out);
                }
                @Override
                public void processPre() {}
                @Override
                public void processRunning(String progress) {
                    return;
                }
            }).execute(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleConfidence(Double out) {
        Log.i("IdentityService","confidence is "+out.toString());
        if(out<threshold){
            EventBus.getDefault().post(Message.FACE_FAIL);
        }else{
            EventBus.getDefault().post(Message.FACE_PASS);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        EventBus.getDefault().register(this);
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent){
        EventBus.getDefault().unregister(this);
        return super.onUnbind(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        if(event.equals(Message.SCAN_FACE)){
            Log.i("IdentityService","begin to scan face");
            takePic();
        }
    }
}
