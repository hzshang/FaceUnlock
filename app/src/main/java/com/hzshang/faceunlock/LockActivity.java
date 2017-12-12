package com.hzshang.faceunlock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.hzshang.faceunlock.common.Message;
import com.hzshang.faceunlock.dialog.CheckPinProtect;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.hzshang.faceunlock.dialog.DialogMessage;
import com.hzshang.faceunlock.lib.Storage;
import org.greenrobot.eventbus.EventBus;

public class LockActivity extends AppLockActivity {
    private boolean unlock=false;
    @Override
    public void showForgotDialog() {
        if (Storage.hasPinProtect(this)) {
            final CheckPinProtect checkPinProtect = new CheckPinProtect(this) {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.confirm_pin_protected_check:
                            if(isAnswerIsCorrect()){
                                LockManager<LockActivity> lockManager=LockManager.getInstance();
                                lockManager.getAppLock().setPasscode("1234");
                                DialogMessage.showDialog("密码已重设为1234,请及时修改",LockActivity.this);
                            }else{
                                DialogMessage.showDialog(getString(R.string.pin_protect_input_wrong),LockActivity.this);
                            }
                            dismiss();
                            break;
                        case R.id.cancle_pin_protected_check:
                            dismiss();
                            break;
                        default:
                            dismiss();
                            break;
                    }
                    LockActivity.this.hideNavigation();
                }
            };
            checkPinProtect.show();
        } else {
            DialogMessage.showDialog(getString(R.string.pin_protect_not_set), this);
        }
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
        if(!unlock){
            EventBus.getDefault().post(Message.USER_EXCEPT);
        }
        super.onPause();
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
