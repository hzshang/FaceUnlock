package com.hzshang.faceunlock.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.common.Message;
import com.hzshang.faceunlock.lib.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ManagerService extends Service {
    private SerCon sensorCon;
    private SerCon scanCon;
    private SerCon lockCon;
    private static final int NOTIFICATION_ID=8329;
    private boolean pinPass=true;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorCon = new SerCon();
        scanCon = new SerCon();
        lockCon = new SerCon();
        Bundle bundle=intent.getExtras();

        Log.i("ManagerService", "background service start");
        //bind sensor service
        Intent sensorService = new Intent(this, SensorService.class);
        boolean enableGravity=bundle.getBoolean(App.ENABLE_GRAVITY,Storage.getGravitySwitch(this));
        sensorService.putExtra(App.ENABLE_GRAVITY,enableGravity);
        bindService(sensorService, sensorCon, BIND_AUTO_CREATE);
        //bind scan service
        Intent scanService = new Intent(this, ScanService.class);
        bindService(scanService, scanCon, BIND_AUTO_CREATE);
        //bind lock service
        Intent lockService = new Intent(this, LockService.class);
        bindService(lockService, lockCon, BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);
        disableKeyGuard();

        Notification notification = new Notification.Builder(this.getApplicationContext())
                .setContentText(getString(R.string.service_running))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();

        startForeground(NOTIFICATION_ID, notification);

        //开机启动需输入密码
        if(bundle.getBoolean(App.bootIntent,false)){
            handleFaceFail();
        }

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
        stopForeground(true);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        switch (event) {
            case Message.SCREEN_ON:
                if(pinPass){//如果上次人脸解锁失败，下次直接启用密码锁
                    EventBus.getDefault().post(Message.SCAN_FACE);
                }else{
                    Log.i("ManagerService","No more scan face chance");
                    handleFaceFail();
                }
                break;
            case Message.FACE_PASS:
                handleFaceSuccess();
                break;
            case Message.FACE_FAIL:
                handleFaceFail();
                break;
            case Message.LOCK_EXIT:
                if(!pinPass){
                    lockPhone();
                }
                break;
            case Message.PIN_PASS://使用密码解锁成功后
                pinPass=true;
                break;
            default:
                break;
        }
    }
    private void lockPhone(){
        DevicePolicyManager dpm=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        dpm.lockNow();
    }
    private void handleFaceSuccess() {
        //nothing to do
    }

    private void handleFaceFail() {
        //show lock activity
        pinPass=false;
        EventBus.getDefault().post(Message.START_LOCK);
    }

    private class SerCon implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    private void enableKeyguard() {
        //enable keyguard
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.reenableKeyguard();
    }

    private void disableKeyGuard() {
        //disable keyguard
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.disableKeyguard();
    }
}
