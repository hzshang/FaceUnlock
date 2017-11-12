package com.example.hzshang.faceunlock.lib;


import android.app.Application;
import android.content.res.Resources;

import com.example.hzshang.faceunlock.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;


public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.azure_endpoint),getString(R.string.azure_key));
    }
    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient=null;
}