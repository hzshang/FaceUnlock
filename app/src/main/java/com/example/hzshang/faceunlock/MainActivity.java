package com.example.hzshang.faceunlock;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout managerUser;
    private LinearLayout smartLock;
    private Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        managerUser=(LinearLayout)findViewById(R.id.manager_user);
        managerUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,ManagerUser.class);
                startActivity(intent);
            }
        });
        boolean isrunning=serviceIsRunning(DetectService.class.getName());

        aSwitch=(Switch)findViewById(R.id.switch1);
        aSwitch.setChecked(isrunning);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent intent = new Intent(MainActivity.this,DetectService.class);
                    intent.setPackage(getPackageName());
                    startService(intent);
                }else{
                    //do stuff when Switch if OFF
                    Intent intent = new Intent(MainActivity.this,DetectService.class);
                    intent.setPackage(getPackageName());
                    stopService(intent);
                }
            }
        });
        smartLock=(LinearLayout)findViewById(R.id.smart_lock);
        smartLock.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,CamTestActivity.class);
                startActivity(intent);
            }
        });

    }
    public boolean serviceIsRunning(String serviceName){
        final ActivityManager activityManager=(ActivityManager)this.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>sercices=activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : sercices) {
            if (runningServiceInfo.service.getClassName().equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}