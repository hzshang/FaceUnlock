package com.hzshang.faceunlock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.hzshang.faceunlock.common.Message;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import org.greenrobot.eventbus.EventBus;

public class LockActivity extends AppLockActivity {
    private boolean unlock=false;
    @Override
    public void showForgotDialog() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_lock;
    }

    @Override
    public void onPinFailure(int attempts) {
        if(attempts>=3){
            EventBus.getDefault().post(Message.TOO_MANY_ATTEMPTS);
        }
    }
    @Override
    public void onResume(){
        hideNavigation();
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
    @Override
    protected void onDestroy(){
        if(!unlock){
            EventBus.getDefault().post(Message.USER_EXCEPT);
        }
        super.onDestroy();
    }
    @Override
    public void onPinSuccess(int attempts) {
        Log.i("LockActivity","pinSuccess");
        unlock=true;
        EventBus.getDefault().post(Message.PASS);
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }

    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);

    }
    private void hideNavigation(){
        int uiOptions =  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
