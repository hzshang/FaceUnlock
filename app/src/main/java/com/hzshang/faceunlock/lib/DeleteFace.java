package com.hzshang.faceunlock.lib;

import android.content.Context;
import android.util.Log;

import com.hzshang.faceunlock.HTTP.FaceAPI;
import com.hzshang.faceunlock.HTTP.Response;
import com.hzshang.faceunlock.R;


import org.json.JSONObject;

/**
 * Created by hzshang on 2017/12/8.
 */

public class DeleteFace extends Async<String,String,Boolean> {
    public DeleteFace(Context context, interFace delegate) {
        super(context, delegate);
    }
    @Override
    public Boolean doInBackground(String... params){
        String faceId=params[0];
        String group=params[1];
        Boolean ret;
        try {
            publishProgress(context.getString(R.string.delete_user_doing));
            Response response= FaceAPI.deleteFace(group, faceId);
            String res=new String(response.getContent());
            if(response.getStatus()!=200){
                ret=false;
                Log.i("DeleteFace",res);
            }else{
                JSONObject jsonObject=new JSONObject(res);
                if(jsonObject!=null&&jsonObject.optInt("face_removed")==1){
                    ret=true;
                }else{
                    ret=false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ret=false;
        }
        return ret;
    }
}
