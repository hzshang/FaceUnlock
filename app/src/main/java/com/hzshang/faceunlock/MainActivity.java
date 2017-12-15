package com.hzshang.faceunlock;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.dialog.DialogMessage;
import com.hzshang.faceunlock.dialog.OverLayDialog;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.receiver.MyAdmin;
import com.hzshang.faceunlock.service.ManagerService;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_ENABLE_PIN = 11;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_OVERLAY_CODE = 2;
    private static final int REQUEST_LOCKSCREEN_CODE = 3;
    private static final int REQUEST_UNLOCK_CODE = 111;
    private DevicePolicyManager dpm;
    private ComponentName admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, MyAdmin.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Storage.isSetPwd(this) && App.isTimeout()) {
            Intent intent = new Intent(this, LockActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            startActivityForResult(intent, REQUEST_UNLOCK_CODE);
        }
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
            case REQUEST_ENABLE_PIN:
                Storage.setPwd(this);
                DialogMessage.showDialog(getString(R.string.lock_set_success), this);
                break;
            case REQUEST_UNLOCK_CODE:
                App.resetTimeOut();
                break;
            case REQUEST_OVERLAY_CODE:
                if (!Settings.canDrawOverlays(this)) {
                    DialogMessage.showDialog(getString(R.string.overlay_fail), this);
                }
                break;
            case REQUEST_LOCKSCREEN_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    DialogMessage.showDialog(getString(R.string.lock_screen_fail), this);
                }
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
        LinearLayout testView = (LinearLayout) findViewById(R.id.testView);
        testView.setOnClickListener(this);
        LinearLayout licienceView = (LinearLayout) findViewById(R.id.licience_view);
        licienceView.setOnClickListener(this);
    }

    private void openManagerUser() {
        Intent intent = new Intent(MainActivity.this, ManagerUser.class);
        startActivity(intent);
    }

    private void requestForOverlay() {
        final OverLayDialog overLayDialog = new OverLayDialog(this) {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.ask_overlay_confirm) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_OVERLAY_CODE);
                }
                dismiss();
            }
        };
        overLayDialog.show();
    }

    private void requestLockScreen() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        startActivityForResult(intent, REQUEST_LOCKSCREEN_CODE);
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
            case R.id.testView:
                break;
            case R.id.licience_view:
                break;
        }
    }



    private void EnablePin() {
        Intent intent = new Intent(MainActivity.this, LockActivity.class);
        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
        startActivityForResult(intent, REQUEST_ENABLE_PIN);
    }

    private boolean checkPermission() {

        if (!Settings.canDrawOverlays(this)) {
            requestForOverlay();
            return false;
        }
        if (!dpm.isAdminActive(admin)) {
            requestLockScreen();
            return false;
        }
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        boolean ret = true;

        for (String i : permissions) {
            ret = ret && (ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_GRANTED);
        }
        if (!ret) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
        return ret;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.switch_face:
                handleSwitchFace(compoundButton,isChecked);
                break;
        }

    }


    private void handleSwitchFace(CompoundButton compoundButton,boolean isChecked) {
        Intent managerService = new Intent(MainActivity.this, ManagerService.class);
        managerService.putExtra(App.bootIntent, false);
        if (isChecked) {//准备开启服务
            if (checkPermission()) {//检查权限
                if (!Storage.userIsEmpty(this)) {//检查用户是否为空
                    if (Storage.isSetPwd(this)) {//检查是否设置密码
                        startService(managerService);//检测完成，开启服务
                        DialogMessage.showDialog(getString(R.string.service_start), this);
                    } else {//引导设置密码
                        DialogMessage.showDialog(getString(R.string.lock_is_disable), this);
                        compoundButton.setChecked(false);
                        EnablePin();
                    }
                } else {//引导添加用户
                    DialogMessage.showDialog(getString(R.string.empty_user), this);
                    compoundButton.setChecked(false);
                    openManagerUser();
                }
            } else {//权限不足
                compoundButton.setChecked(false);
            }
        } else {//关闭服务
            if (stopService(managerService)) {
                DialogMessage.showDialog(getString(R.string.service_stop), this);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            DialogMessage.showDialog(permissions[i] + "授权失败", this);
                    }
                }
                break;
            default:
                break;
        }
    }
}