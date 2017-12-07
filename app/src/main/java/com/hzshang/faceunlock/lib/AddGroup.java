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

public class AddGroup extends Async<String,String,String>{

    public AddGroup(Context context,interFace delegate) {
        super(context,delegate);
    }
    @Override
    protected String doInBackground(String... params) {
        // Get an instance of face service client.
        FaceSetOperate FaceSet= App.getFaceSet();
        try{
            publishProgress(context.getString(R.string.create_group));
            // Start creating person group in server.
            Response faceset = FaceSet.createFaceSet(null,null,null,null,null, 1);
            String res = new String(faceset.getContent());
            if(faceset.getStatus()==200){
                JSONObject jsonObject=new JSONObject(res);
                return jsonObject.optString("faceset_token");
            }else{
                Log.i("AddGroup",res);
                return null;
            }
        } catch (Exception e) {
            Log.i("AddGroup","network error");
            return null;
        }
    }
}
