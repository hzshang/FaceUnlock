package com.hzshang.faceunlock.common;

import android.app.Application;

import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.hzshang.faceunlock.LockActivity;
import com.hzshang.faceunlock.R;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;


public class App extends Application {

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
    }

    public static CommonOperate getCommonOperate(){
        return commonOperate;
    }
    public static FaceSetOperate getFaceSet(){
        return FaceSet;
    }

    private static CommonOperate commonOperate=null;
    private static FaceSetOperate FaceSet=null;
    public static final String bootIntent="BOOTINTENT";
}