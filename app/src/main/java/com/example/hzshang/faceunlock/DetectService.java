package com.example.hzshang.faceunlock;


import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


public class DetectService extends Service implements SensorEventListener {

    private SensorManager sManager;
    boolean resetPosition;
    private MyReceiver myReceiver;//listen screen on/off
    private PowerManager.WakeLock wl;
    private ScanFace.MyBinder myBinder;
    private Sensor mSensorOrientation;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBinder = (ScanFace.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myBinder = null;
        }
    };

    private class MyReceiver extends BroadcastReceiver {

        public MyReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                handleScreenOn();
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                handleScreenOff();
            }
        }
    }

    private void handleScreenOff() {
        sManager.registerListener((SensorEventListener) this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
        Log.i("wake screen", "off");
    }

    private void handleScreenOn() {
        sManager.unregisterListener(this);
        Log.i("wake screen", "success");
        //scan face
        if (myBinder != null) {
            myBinder.startTakePicAndUnlock();
        } else {
            Log.i("Detect Service", "myBinder is null");
        }
    }


    @Override
    public void onCreate() {
        Log.i("Detect Service", "onCreate excute");
        super.onCreate();
        resetPosition = true;
        //bind scanFace service
        Intent intent = new Intent(this, ScanFace.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        //get sensor device
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation= sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //DISABLE KEYGUARD
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
        keyguardLock.disableKeyguard();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(myReceiver, intentFilter);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sManager.unregisterListener(this);
        unregisterReceiver(myReceiver);
        if (mConnection != null)
            unbindService(mConnection);
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float angle2, angle3;
        float[] values = event.values;
        angle2 = (float) (Math.round(values[1] * 100)) / 100;
        angle3 = (float) (Math.round(values[2] * 100)) / 100;
        boolean unlock = checkAngle(angle2, angle3);
        if (resetPosition && unlock) {
            wakeScreen();
        }
        resetPosition = !unlock;
    }

    private void wakeScreen() {
        wl.acquire();
        wl.release();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }


    private boolean checkAngle(float angle2, float angle3) {
        return angle2 < -20 && angle2 > -60 && angle3 > -40 && angle3 < 40;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
