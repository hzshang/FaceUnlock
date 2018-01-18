package com.hzshang.faceunlock.common;

import android.app.Application;
import android.os.Handler;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.hzshang.faceunlock.UI.LockActivity;
import com.hzshang.faceunlock.R;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;


public class App extends Application {
    private static CommonOperate commonOperate=null;
    private static FaceSetOperate FaceSet=null;
    public static final String bootIntent="BOOTINTENT";
    public static final String ENABLE_GRAVITY="enable_gravity";
    private static boolean timeout;
    private static Handler handler;
    private static Runnable runnable;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(){
        super.onCreate();
        String key=getString(R.string.facepp_key);
        String secret=getString(R.string.facepp_secret);
        commonOperate= new CommonOperate(key,secret , false);
        FaceSet= new FaceSetOperate(key, secret, false);
        LockManager<LockActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, LockActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.security_lock);
        lockManager.getAppLock().setTimeout(10000);
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                timeout=true;
            }
        };
        timeout=true;
    }

    public static CommonOperate getCommonOperate(){
        return commonOperate;
    }
    public static FaceSetOperate getFaceSet(){
        return FaceSet;
    }
    //解锁后60秒以内免密码
    public static void resetTimeOut(){
        timeout=false;
        handler.postDelayed(runnable,60000);
    }

    public static boolean isTimeout(){
        return timeout;
    }


}