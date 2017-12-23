package com.hzshang.faceunlock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.hzshang.faceunlock.common.Message;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import org.greenrobot.eventbus.EventBus;

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
    public void onCreate(Bundle b){
        super.onCreate(b);
    }

    @Override
    public void onResume() {
        hideNavigation();
        super.onResume();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Log.i("LockActivity", "pinSuccess");
        EventBus.getDefault().post(Message.PIN_PASS);
    }
    //有问题？
    @Override
    public void onPause(){
        EventBus.getDefault().post(Message.LOCK_EXIT);
        Log.i("LockActivity","onPause");
        super.onPause();
    }
    @Override
    public void onDestroy(){
        Log.i("LockActivity","Lock Destroy");
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
}
