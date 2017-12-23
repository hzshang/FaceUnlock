package com.hzshang.faceunlock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.hzshang.faceunlock.LockActivity;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.service.ManagerService;

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
