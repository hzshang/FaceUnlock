package com.hzshang.faceunlock.lib;


import android.content.Context;
import android.util.Log;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.App;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;


import org.json.JSONObject;


/**
 * Created by hzshang on 2017/11/4.
 */

public class AddFaceToGroup extends Async<String, String, Object[]> {
    public AddFaceToGroup(Context context, interFace delegate){
        super(context,delegate);
    }
    @Override
    protected Object[] doInBackground(String... params) {
        // Get an instance of face service client.
        Object[] ret=new Object[2];
        try{
            //WAIT TO CHANGE
            publishProgress(context.getString(R.string.create_user));
            // Start the request to creating person.
            FaceSetOperate faceSetOperate= App.getFaceSet();
            Response response=faceSetOperate.addFaceByFaceToken(params[0],params[1]);
            Log.i("AddFaceToGroup","faceToken:"+params[0]);
            Log.i("AddFaceToGroup","groupId:"+params[1]);

            String res=new String(response.getContent());
            if(response.getStatus()!=200){
                ret[0]=false;
                ret[1]=context.getString(R.string.error_network);
            }else{
                JSONObject jsonObject=new JSONObject(res);
                if(jsonObject.optInt("face_added")==1){
                    ret[0]=true;
                }else{
                    ret[0]=false;
                    ret[1]=context.getString(R.string.error_add_user);
                }
            }
            if(!(boolean)ret[0]){
                Log.i("AddFaceToGroup",res);
            }
        } catch (Exception e) {
            ret[0]=false;
            ret[1]=context.getString(R.string.error_network);
            Log.i("DetectFace","net error");
        }
        return ret;
    }
}
