package com.hzshang.faceunlock.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.hzshang.faceunlock.common.Message;

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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ManagerService", "background service start");
        //bind detect service,scan service
        Intent sensorService = new Intent(this, SensorService.class);
        bindService(sensorService, sensorCon, BIND_AUTO_CREATE);
        Intent scanService = new Intent(this, ScanService.class);
        bindService(scanService, scanCon, BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);
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
            case Message.RETURN_CONFIDENCE:
                break;
            case Message.FACE_PASS:
                break;
            case Message.FACE_FAIL:
                break;
            default:
                break;
        }
    }

}
