package com.hzshang.faceunlock;

import android.view.View;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.hzshang.faceunlock.common.CheckPinProtect;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.hzshang.faceunlock.common.Dialog;
import com.hzshang.faceunlock.lib.Storage;

public class LockActivity extends AppLockActivity {
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
                                Dialog.showDialog("密码已重设为1234,请及时修改",LockActivity.this);
                            }else{
                                Dialog.showDialog(getString(R.string.pin_protect_input_wrong),LockActivity.this);
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
                }
            };
            checkPinProtect.show();
        } else {
            Dialog.showDialog(getString(R.string.pin_protect_not_set), this);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_lock;
    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }


}
