package com.example.hzshang.faceunlock;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.hzshang.faceunlock.common.Dialog;
import com.example.hzshang.faceunlock.lib.SetPinProtect;
import com.example.hzshang.faceunlock.lib.Storage;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CODE_ENABLE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        super.onActivityResult(requestCode, resultCode, data);
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
        boolean isrunning = serviceIsRunning(DetectService.class.getName());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manager_user:
                Intent intent = new Intent(MainActivity.this, ManagerUser.class);
                startActivity(intent);
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
                Intent intent1 = new Intent(MainActivity.this, testActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Intent detectService = new Intent(MainActivity.this, DetectService.class);
        Intent scanFace=new Intent(MainActivity.this,ScanFace.class);
        if (isChecked) {
            startService(detectService);
            startService(scanFace);
            Dialog.showDialog("start service success", this);
        } else {
            if (stopService(detectService) &&stopService(scanFace)) {
                Dialog.showDialog("stop service success", this);
            } else {
                Dialog.showDialog("stop service fail", this);
            }
        }
    }
}