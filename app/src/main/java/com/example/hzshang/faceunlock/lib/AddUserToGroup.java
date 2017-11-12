package com.example.hzshang.faceunlock.lib;


import android.content.Context;
import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;


/**
 * Created by hzshang on 2017/11/4.
 */

public class AddUserToGroup extends Async<String, String, String> {
    public AddUserToGroup(Context context,interFace delegate){
        super(context,delegate);
    }
    @Override
    protected String doInBackground(String... params) {
        // Get an instance of face service client.
        FaceServiceClient faceServiceClient = App.getFaceServiceClient();
        try{
            //WAIT TO CHANGE
            publishProgress(context.getString(R.string.create_user));

            // Start the request to creating person.
            CreatePersonResult createPersonResult = faceServiceClient.createPerson(params[0],params[1], null);

            return createPersonResult.personId.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
