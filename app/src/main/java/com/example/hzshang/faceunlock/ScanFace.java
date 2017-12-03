package com.example.hzshang.faceunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScanFace extends Service {
    private MyReceiver myReceiver;

    private class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(getString(R.string.SCAN_FACE))) {
                //TODO:OPEN CAMERA TO SCAN FACE
                Log.i("ScanFace", "open camera...");
            }
        }
    }

    public ScanFace() {
        myReceiver = new MyReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.SCAN_FACE));
        registerReceiver(myReceiver, intentFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
