package com.hzshang.faceunlock.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.common.Message;

import org.greenrobot.eventbus.EventBus;


public class SensorService extends Service implements SensorEventListener {

    private SensorManager sManager;
    boolean resetPosition;//prevent repeated wake-up
    private MyReceiver myReceiver;//listen screen on/off
    private PowerManager.WakeLock wl;
    private Sensor mSensorOrientation;
    private boolean gravityEnable;

    //listen screen on/off
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.e("SensorService", "action is null");
                return;
            }
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                    handleScreenOn();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    handleScreenOff();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleScreenOff() {
        if (gravityEnable) {
            sManager.registerListener((SensorEventListener) this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
        }
        Log.i("SensorService", "screen off");
    }

    private void handleScreenOn() {
        if (gravityEnable) {
            sManager.unregisterListener(this);
        }
        Log.i("SensorService", "screen on");
        //tell manager to scan face
        EventBus.getDefault().post(Message.SCREEN_ON);
    }


    @Override
    public void onCreate() {
        resetPosition = true;
        //get sensor device

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (gravityEnable) {
            sManager.unregisterListener(this);
        }
        unregisterReceiver(myReceiver);
        return super.onUnbind(intent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gravityEnable) {
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
    }

    private void wakeScreen() {
        wl.acquire(10);
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
    public IBinder onBind(Intent intent) {
        myReceiver = new MyReceiver();
        Bundle bundle = intent.getExtras();
        gravityEnable = bundle.getBoolean(App.ENABLE_GRAVITY);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(myReceiver, intentFilter);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        return null;
    }
}
