package com.hzshang.faceunlock.lib;


import android.content.Context;
import android.util.Log;

import com.hzshang.faceunlock.HTTP.FaceAPI;
import com.hzshang.faceunlock.HTTP.Response;
import com.hzshang.faceunlock.R;

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
        try{
            publishProgress(context.getString(R.string.create_group));
            // Start creating person group in server.
            Response faceset = FaceAPI.createSet();
            String res = new String(faceset.getContent());
            if(faceset.getStatus()==200){
                JSONObject jsonObject=new JSONObject(res);
                return jsonObject.optString("faceset_token");
            }else{
                Log.i("AddGroup",res);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("AddGroup","network error");
            return null;
        }
    }
}
