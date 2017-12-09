package com.hzshang.faceunlock.lib;

import android.content.Context;
import android.util.Log;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.common.App;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;

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
            FaceSetOperate faceSetOperate=App.getFaceSet();
            Response response=faceSetOperate.removeFaceFromFaceSetByFaceSetToken(group,faceId);
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
