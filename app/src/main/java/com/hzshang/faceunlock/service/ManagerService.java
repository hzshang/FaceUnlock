package com.hzshang.faceunlock.service;

import android.app.Notification;
import android.app.NotificationManager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.hzshang.faceunlock.LockActivity;
import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.common.Message;
import com.hzshang.faceunlock.lib.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ManagerService extends Service {
    private ServiceConnection sensorCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            return;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            return;
        }
    };
    private ServiceConnection scanCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            return;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //TODO:restart
            return;
        }
    };
    private NotificationManager mNotifyMgr;
    private Runnable runnable;
    private final int notiId=1;
    private Handler handler;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ManagerService", "background service start");
        //bind detect service,scan service
        Intent sensorService = new Intent(this, SensorService.class);
        bindService(sensorService, sensorCon, BIND_AUTO_CREATE);
        Intent scanService = new Intent(this, ScanService.class);
        bindService(scanService, scanCon, BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);
        mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        runnable= new Runnable(){
            public void run() {
                mNotifyMgr.cancel(notiId);
            }
        };
        handler = new Handler();
        Bundle bundle=intent.getExtras();
        if(bundle.getBoolean(App.bootIntent)){
            handleFail();
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unbindService(sensorCon);
        unbindService(scanCon);
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("通知")
                        .setContentText("欢迎回来")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        mNotifyMgr.notify(notiId, mBuilder.build());
        handler.postDelayed(runnable,500);
    }

    private void handleFail() {
        if(!Storage.firstSetPwd(this)){
            Intent intent = new Intent(this, LockActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE,AppLock.UNLOCK_PIN);
            startActivity(intent);
        }else{
            Log.i("ManagerService","password not set");
        }
    }
}
