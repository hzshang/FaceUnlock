package com.hzshang.faceunlock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hzshang.faceunlock.UI.LockActivity;
import com.hzshang.faceunlock.lib.Storage;

//开机启动
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Storage.isSetPwd(context)){
            Intent lock=new Intent(context, LockActivity.class);
            context.startActivity(lock);
        }
    }
}
