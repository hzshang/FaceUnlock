package com.hzshang.faceunlock.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.Message;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LockActivity extends AppLockActivity {
    @Override
    public void showForgotDialog() {
        return;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_lock;
    }

    @Override
    public void onPinFailure(int attempts) {
        if (attempts >= 3) {
            EventBus.getDefault().post(Message.TOO_MANY_ATTEMPTS);
        }
    }

    @Override
    public void onCreate(Bundle b) {
        Log.i("LockActivity", "onCreate");
        EventBus.getDefault().register(this);
        super.onCreate(b);
    }

    @Override
    public void onResume() {
        Log.i("LockActivity", "onResume");
        hideNavigation();
        super.onResume();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Log.i("LockActivity", "pinSuccess");
        EventBus.getDefault().post(Message.PIN_PASS);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().post(Message.LOCK_EXIT);
        Log.i("LockActivity", "onPause");
        //会闪退
        //finish();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i("LockActivity", "Lock Destroy");
        super.onDestroy();
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }

    //隐藏系统导航栏
    private void hideNavigation() {
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        switch (event) {
            case Message.LOCK_EXIT_FROM_SERVICE:
                Log.i("LockActivity","get from lockservice");
                finish();
                break;
            default:
                break;
        }
    }
}
