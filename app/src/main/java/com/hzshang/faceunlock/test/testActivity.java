package com.hzshang.faceunlock.test;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.receiver.DeviceAdmin;

public class testActivity extends AppCompatActivity implements View.OnClickListener {
    private DevicePolicyManager dpm;
    private ComponentName admin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, DeviceAdmin.class);
        if (!dpm.isAdminActive(admin)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
            startActivityForResult(intent, 0);
        }else{
            Log.i("false","false");
        }
        findViewById(R.id.lock).setOnClickListener(this);
        findViewById(R.id.unlock).setOnClickListener(this);
        findViewById(R.id.refresh).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lock:
                startLockTask();
                break;
            case R.id.unlock:
                stopLockTask();
                break;
            case R.id.refresh:
                try {
                    dpm.setLockTaskPackages(admin, new String[]{getPackageName()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dpm.isLockTaskPermitted(getPackageName())) {
                    Log.i("testActivity", "isLockTaskPermitted:True");

                } else {
                    Log.i("testActivity", "isLockTaskPermitted:False");
                }
                break;
            case R.id.setAdmin:
                SetDeviceAdmin();
                break;
        }
    }

    public void SetDeviceAdmin() {

    }
}