package com.example.hzshang.faceunlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class DetectService extends Service implements SensorEventListener {
    public static final int NOTIFICATION_ID=0x11;
    //private ServiceThread thread;
    private SensorManager sManager;
    private Sensor mSensorOrientation;
    boolean unLock = false;
    private  PowerManager.WakeLock wl = null;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        //to do
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sManager.registerListener((SensorEventListener) this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);

        super.onCreate();
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            //API 18以上，发送Notification并将其置为前台后，启动InnerService
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, InnerService.class));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float angle1,angle2,angle3;
        float[] values = event.values;
        switch (event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION:
                angle1 = (float) (Math.round(values[0] * 100)) / 100;
                angle2 = (float) (Math.round(values[1] * 100)) / 100;
                angle3 = (float) (Math.round(values[2] * 100)) / 100;

                unLock= checkAngle(angle2,angle3);
                break;
        }

        PowerManager p = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        boolean isOn = p.isScreenOn();
        if(!isOn){
            if(unLock) {
                //debug.setText("true");
                if (wl == null) {
                    PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
                    wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "wakeUp");
                }
                wl.acquire();
                wakeScreenLock();

            }
            else if(wl != null)
            {
                wl.release();
                wl = null;
            }
            //debug.setText("false");
        }
        else {
            if(wl != null)
            {
                wl.release();
                wl = null;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public  void wakeScreenLock(){

    }

    private boolean checkAngle(float angle2,float angle3){
        return angle2<-20&&angle2>-60&&angle3>-40&&angle3<40;
    }


    public  static class  InnerService extends Service{

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            },100);
        }
    }

}
