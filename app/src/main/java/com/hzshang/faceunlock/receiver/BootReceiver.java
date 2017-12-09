package com.hzshang.faceunlock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hzshang.faceunlock.ManagerUser;
import com.hzshang.faceunlock.common.App;
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent managerService=new Intent(context, ManagerUser.class);
        managerService.putExtra(App.bootIntent,true);
        context.startService(managerService);
    }
}
