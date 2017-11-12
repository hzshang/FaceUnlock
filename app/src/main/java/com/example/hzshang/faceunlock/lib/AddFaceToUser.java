package com.example.hzshang.faceunlock.lib;

import android.content.Context;
import android.content.res.Resources;
import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;


import java.io.InputStream;
import java.util.UUID;

/**
 * Created by hzshang on 2017/11/4.
 */

public class AddFaceToUser extends Async<Object,String,Boolean> {

    public AddFaceToUser(Context context,interFace delegate) {
        super(context,delegate);
    }


    @Override
    protected void onPreExecute(){
        delegate.processPre();
    }
    @Override
    protected void onProgressUpdate(String... process){
        delegate.processRuning(process[0]);
    }
    @Override
    protected void onPostExecute(Boolean ret) {
        delegate.processFinish(ret);
    }
    @Override
    protected Boolean doInBackground(Object... params) {
        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = App.getFaceServiceClient();
        try{
            publishProgress(context.getString(R.string.upload_image));
            String groupId=(String)params[0];
            UUID personId = (UUID)params[1];
            InputStream inputStream=(InputStream)params[2];
            Face face=(Face)params[3];
            // Start the request to add face.
            faceServiceClient.addPersonFace(groupId, personId, inputStream,null,face.faceRectangle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}