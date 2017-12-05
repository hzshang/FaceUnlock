package com.example.hzshang.faceunlock.lib;

import android.app.Application;
import com.example.hzshang.faceunlock.R;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;


public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        String key=getString(R.string.facepp_key);
        String secret=getString(R.string.facepp_secret);
        commonOperate= new CommonOperate(key,secret , false);
        FaceSet= new FaceSetOperate(key, secret, false);
    }
    public static CommonOperate getCommonOperate(){
        return commonOperate;
    }
    public static FaceSetOperate getFaceSet(){
        return FaceSet;
    }

    private static CommonOperate commonOperate=null;
    private static FaceSetOperate FaceSet=null;
}