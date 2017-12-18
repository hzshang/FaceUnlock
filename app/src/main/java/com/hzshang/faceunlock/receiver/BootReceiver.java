package com.hzshang.faceunlock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hzshang.faceunlock.common.App;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.service.ManagerService;

//开机启动
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Storage.getLock(context)){
            Intent managerService=new Intent(context, ManagerService.class);
            managerService.putExtra(App.bootIntent,true);
            context.startService(managerService);
        }
    }
}
