package com.example.hzshang.faceunlock.lib;


import android.content.Context;

import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;
import com.microsoft.projectoxford.face.FaceServiceClient;


/**
 * Created by hzshang on 2017/11/4.
 */

public class AddGroup extends Async<String,String,String>{

    public AddGroup(Context context,interFace delegate) {
        super(context,delegate);
    }
    @Override
    protected String doInBackground(String... params) {
        // Get an instance of face service client.
        FaceServiceClient faceServiceClient = App.getFaceServiceClient();
        try{
            String groupId=params[0];
            publishProgress(context.getString(R.string.create_group));
            // Start creating person group in server.
            faceServiceClient.createPersonGroup(groupId,context.getString(R.string.group_name), null);
            return groupId;
        } catch (Exception e) {
            return null;
        }
    }
}
