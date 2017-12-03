package com.example.hzshang.faceunlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.hzshang.faceunlock.common.Dialog;

import java.util.Random;

public class testActivity extends AppCompatActivity implements View.OnClickListener{



    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    final int REQUEST_ADMIN=1;
    final int CREATE_KEY=2;
    private Button disable;
    private Button enable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button button=(Button)findViewById(R.id.testBackBtn);
        Button lock=(Button)findViewById(R.id.testLockBtn);
        enable=(Button)findViewById(R.id.testEnableBtn);
        disable=(Button)findViewById(R.id.testDisableBtn);
        Button setpwd1=(Button)findViewById(R.id.testSetPwd);
        Button setpwd2=(Button)findViewById(R.id.testClearRes);
        Button restriction=(Button)findViewById(R.id.addUserRestriction);
        restriction.setOnClickListener(this);
        setpwd1.setOnClickListener(this);
        setpwd2.setOnClickListener(this);
        enable.setOnClickListener(this);
        disable.setOnClickListener(this);
        lock.setOnClickListener(this);
        button.setOnClickListener(this);
        deviceManger = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        activityManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        compName=new ComponentName(this,MyAdmin.class);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.testBackBtn:
                finish();
                break;
            case R.id.testEnableBtn:
                if(!deviceManger.isAdminActive(compName)){
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
                    startActivityForResult(intent, REQUEST_ADMIN);
                }
                break;
            case R.id.testDisableBtn:
                deviceManger.removeActiveAdmin(compName);
                break;
            case R.id.testLockBtn:
                boolean active = deviceManger.isAdminActive(compName);
                if (active) {
                    deviceManger.lockNow();
                }
                break;
            case R.id.testSetPwd:
                if(deviceManger.isDeviceOwnerApp(getPackageName())){
                    String[] array=new String[1];
                    array[0]=getPackageName();
                    deviceManger.setLockTaskPackages(compName,array);
                }else{
                    Dialog.showDialog("not DeviceOwner",this);
                }
                break;
            case R.id.testClearRes:
                if(deviceManger.isAdminActive(compName)){
                    deviceManger.setStatusBarDisabled(compName,true);
                }
                break;
            case R.id.addUserRestriction:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                break;
        }

    }

    private void updateButtonStates() {
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            enable.setEnabled(false);
            disable.setEnabled(true);

        } else {
            enable.setEnabled(true);
            disable.setEnabled(false);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADMIN:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Admin enabled!");
                } else {
                    Log.i("DeviceAdminSample", "Admin enable FAILED!");
                }
                break;
            case CREATE_KEY:
                if(resultCode==Activity.RESULT_OK){
                    Log.i("create key","ok");
                }else{
                    Log.i("create key","fail");
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected byte[] generatorToken(){
        byte[] token=new byte[32];
        Random rand = new Random();
        rand.nextBytes(token);
        return token;
    }

}