package com.hzshang.faceunlock.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.hzshang.faceunlock.LockActivity;
import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.Message;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
//人脸解锁失败后，开启锁屏
public class LockService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams upPms;
    private WindowManager.LayoutParams downPms;
    private FloatView upView = null;
    private FloatView downView = null;


    @Override
    public IBinder onBind(Intent intent) {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getRealSize(p);

        upPms = new WindowManager.LayoutParams();
        upPms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        upPms.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |//允许任何在该窗口之外的触摸事件传递到该窗口以下的控件
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |//弹出的View收不到Back键的事件
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;//全屏幕布局, 不受状态栏影响
        upPms.format = PixelFormat.TRANSLUCENT;
        upPms.x = 0;
        upPms.y = 0;
        upPms.width = p.x;
        upPms.height = 300;
        upPms.gravity = Gravity.LEFT | Gravity.TOP;

        downPms = new WindowManager.LayoutParams();
        downPms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        downPms.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |//允许任何在该窗口之外的触摸事件传递到该窗口以下的控件
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |//弹出的View收不到Back键的事件
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |//全屏幕布局, 不受状态栏影响
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        downPms.format = PixelFormat.TRANSLUCENT;
        downPms.gravity = Gravity.LEFT | Gravity.TOP;
        downPms.x = 0;
        downPms.y = p.y - 200;
        downPms.width = p.x;
        downPms.height = 200;

        EventBus.getDefault().register(this);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        EventBus.getDefault().unregister(this);
        removeLockView();
        return super.onUnbind(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        switch (event) {
            case Message.START_LOCK:
                startLock();
                break;
            case Message.TOO_MANY_ATTEMPTS:
                lockScreen();
                break;

            case Message.LOCK_EXIT:
                removeLockView();
                break;
            default:
                break;
        }
    }

    void startLock() {
        if (upView == null && downView == null) {
            upView = new FloatView(this);
            downView = new FloatView(this);
            wm.addView(upView, upPms);
            wm.addView(downView, downPms);
            Intent intent = new Intent(this, LockActivity.class);
            startActivity(intent);
        }
    }

    void removeLockView() {
        if (upView != null && downView != null) {
            EventBus.getDefault().post(Message.LOCK_EXIT_FROM_SERVICE);
            wm.removeView(upView);
            wm.removeView(downView);
            upView = null;
            downView = null;
        }
    }

    private class FloatView extends LinearLayout {
        public FloatView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.lock_float_layout, this);
        }
    }

    private void lockScreen() {
        removeLockView();
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.lockNow();
    }
}
