package com.hzshang.faceunlock;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.common.CheckPinProtect;
import com.hzshang.faceunlock.common.Dialog;
import com.hzshang.faceunlock.common.SetPinProtect;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.service.ManagerService;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CODE_ENABLE = 11;
    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initView();
    }

    public boolean serviceIsRunning(Class<?> serviceClass) {
        final ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE:
                Storage.removeFirstSetPwd(this);
                break;
        }
    }

    private void initView() {
        LinearLayout managerUser = (LinearLayout) findViewById(R.id.manager_user);
        managerUser.setOnClickListener(this);
        boolean isrunning = serviceIsRunning(ManagerService.class);
        //stop service if user is empty
        if (Storage.userIsEmpty(this) && isrunning) {
            isrunning = false;
            Intent managerService = new Intent(MainActivity.this, ManagerService.class);
            stopService(managerService);
        }
        Switch faceUnlockSwitch = (Switch) findViewById(R.id.switch_face);
        faceUnlockSwitch.setChecked(isrunning);
        faceUnlockSwitch.setOnCheckedChangeListener(this);
        LinearLayout pinSetting = (LinearLayout) findViewById(R.id.pin_set);
        pinSetting.setOnClickListener(this);
        LinearLayout pinProtect = (LinearLayout) findViewById(R.id.pin_protect_set);
        pinProtect.setOnClickListener(this);
        LinearLayout testView = (LinearLayout) findViewById(R.id.testView);
        testView.setOnClickListener(this);
    }

    private void openManagerUser() {
        Intent intent = new Intent(MainActivity.this, ManagerUser.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (!checkPermission()) {
            return;
        }
        switch (view.getId()) {
            case R.id.manager_user:
                openManagerUser();
                break;
            case R.id.pin_set:
                EnablePin();
                break;
            case R.id.pin_protect_set:
                readyChangePinProtect();
                break;
            case R.id.testView:
                Log.i("MainActivity", "disable Pin");
                Intent intent = new Intent(MainActivity.this, LockActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                startActivity(intent);
                break;
        }
    }

    private void readyChangePinProtect() {
        if(Storage.hasPinProtect(this)){
            CheckPinProtect checkPinProtect=new CheckPinProtect(this){
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.confirm_pin_protected_check:
                            if(isAnswerIsCorrect()){
                                dismiss();
                                changePinProtect();
                            }else{
                                Dialog.showDialog(getString(R.string.pin_protect_input_wrong),MainActivity.this);
                                dismiss();
                            }
                            break;
                        case R.id.cancle_pin_protected_check:
                            dismiss();
                            break;
                    }
                }
            };
            checkPinProtect.show();
        }else{
            changePinProtect();
        }
    }
    private void changePinProtect(){
        SetPinProtect setPinProtect = new SetPinProtect(MainActivity.this) {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.set_pin_protection_confirm:
                        String answerText = answer.getText().toString();
                        int index = questions.getSelectedItemPosition();
                        if (answerText.equals("")) {
                            Dialog.showDialog(getString(R.string.pin_protect_empty_error), MainActivity.this);
                        } else {
                            Dialog.showDialog(getString(R.string.pin_protect_set_success), MainActivity.this);
                            Storage.setPinProtect(MainActivity.this, questions.getSelectedItem().toString(), answer.getText().toString());
                            dismiss();
                        }
                        break;
                    case R.id.set_pin_protection_cancle:
                        dismiss();
                        break;
                }
            }
        };
        setPinProtect.show();
    }

    private void EnablePin() {
        Intent intent = new Intent(MainActivity.this, LockActivity.class);
        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
        startActivityForResult(intent,REQUEST_CODE_ENABLE);
    }

    private boolean checkPermission() {
        boolean ret = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!ret) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    REQUEST_PERMISSION_CODE);
        }
        return ret;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Intent managerService = new Intent(MainActivity.this, ManagerService.class);
        managerService.putExtra(App.bootIntent, false);
        if (isChecked) {
            if (checkPermission()) {//检查权限
                if (!Storage.userIsEmpty(this)) {//检查用户是否为空
                    if (Storage.hasPinProtect(this)) {//检查是否设置密保
                        if(!Storage.firstSetPwd(this)){//检查是否设置密码
                            startService(managerService);//检测完成，开启服务
                            Dialog.showDialog(getString(R.string.service_start), this);
                        }else{//引导设置密码
                            Dialog.showDialog(getString(R.string.lock_is_disable), this);
                            compoundButton.setChecked(false);
                            EnablePin();
                        }
                    } else {//引导设置密保
                        compoundButton.setChecked(false);
                        Dialog.showDialog(getString(R.string.should_set_pin_protect),this);
                        readyChangePinProtect();
                    }
                } else {//引导添加用户
                    Dialog.showDialog(getString(R.string.empty_user), this);
                    compoundButton.setChecked(false);
                    openManagerUser();
                }
            } else {//权限不足
                Dialog.showDialog(getString(R.string.permission_limited),this);
                compoundButton.setChecked(false);
            }
        } else {//关闭服务
            if (stopService(managerService)) {
                Dialog.showDialog(getString(R.string.service_stop), this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                boolean t = true;
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            Dialog.showDialog(permissions[i] + "授权失败", this);
                    }
                }
                break;
            default:
                break;
        }
    }
}