package com.hzshang.faceunlock;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.hzshang.faceunlock.common.Dialog;
import com.hzshang.faceunlock.lib.SetPinProtect;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.service.ManagerService;
import com.hzshang.faceunlock.service.SensorService;

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

    private boolean serviceIsRunning(String serviceName) {
        final ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningServiceInfo> sercices = activityManager.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : sercices) {
                if (runningServiceInfo.service.getClassName().equals(serviceName)) {
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
                Dialog.showDialog("pin enabled", this);
                break;
        }
    }

    private void initView() {
        LinearLayout managerUser = (LinearLayout) findViewById(R.id.manager_user);
        managerUser.setOnClickListener(this);
        //face switch button
        boolean isrunning = serviceIsRunning(SensorService.class.getName());
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
                //TODO
                break;
            case R.id.pin_protect_set:
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
                                    Storage.setPinProtect(MainActivity.this, questions.getSelectedItemPosition(), answer.getText().toString());
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
                break;
            case R.id.testView:
//                Intent intent1 = new Intent(MainActivity.this, testActivity.class);
//                startActivity(intent1);
                break;
        }
    }

    private boolean checkPermission() {
        boolean ret = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//                ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED;
        if (!ret) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
        return ret;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Intent managerService = new Intent(MainActivity.this, ManagerService.class);
        if (isChecked) {
            if (checkPermission()) {
                if (Storage.userIsEmpty(this)) {
                    Dialog.showDialog(getString(R.string.empty_user), this);
                    compoundButton.setChecked(false);
                    openManagerUser();
                }else{
                    startService(managerService);
                    Dialog.showDialog("start service success", this);
                }
            } else {
                compoundButton.setChecked(false);
            }
        } else {
            if (stopService(managerService)) {
                Dialog.showDialog("stop service success", this);
            } else {
                Dialog.showDialog("stop service fail", this);
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