package com.hzshang.faceunlock.service;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.common.Message;
import com.hzshang.faceunlock.lib.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ManagerService extends Service {
    private SerCon sensorCon ;
    private SerCon scanCon;
    private SerCon lockCon;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorCon= new SerCon();
        scanCon=new SerCon();
        lockCon=new SerCon();

        Log.i("ManagerService", "background service start");
        //bind detect service,scan service
        Intent sensorService = new Intent(this, SensorService.class);
        bindService(sensorService, sensorCon, BIND_AUTO_CREATE);
        Intent scanService = new Intent(this, ScanService.class);
        bindService(scanService, scanCon, BIND_AUTO_CREATE);
        Intent lockService = new Intent(this,LockService.class);
        bindService(lockService,lockCon,BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);
        disableKeyGuard();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unbindService(sensorCon);
        unbindService(scanCon);
        unbindService(lockCon);
        enableKeyguard();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        switch (event) {
            case Message.SCREEN_ON:
                EventBus.getDefault().post(Message.SCAN_FACE);
                break;
            case Message.FACE_PASS:
                handleSuccess();
                break;
            case Message.FACE_FAIL:
                handleFail();
                break;
            default:
                break;
        }
    }

    private void handleSuccess() {
        //nothing to do
    }

    private void handleFail() {
        //show lock activity
        EventBus.getDefault().post(Message.START_LOCK);

    }
    private class SerCon implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }
    private void enableKeyguard(){
        //enable keyguard
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.reenableKeyguard();
    }
    private void disableKeyGuard(){
        //disable keyguard
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.disableKeyguard();
    }
}
