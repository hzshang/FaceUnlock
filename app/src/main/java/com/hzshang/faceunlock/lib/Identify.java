package com.hzshang.faceunlock.lib;

import android.content.Context;
import android.util.Log;

import com.hzshang.faceunlock.HTTP.FaceAPI;
import com.hzshang.faceunlock.HTTP.Response;
import com.hzshang.faceunlock.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hzshang on 2017/12/5.
 */

public class Identify extends Async<byte[],String,Double> {

    public Identify(Context context, interFace delegate) {
        super(context, delegate);
    }
    @Override
    protected Double doInBackground(byte[]... params){
        publishProgress(context.getString(R.string.identify));
        Double ret=0.0;
        try{
            String groupId=Storage.getGroupId(context);
            Response response= FaceAPI.search(params[0],groupId);
            String res=new String(response.getContent());
            if(response.getStatus()!=200){
                ret=0.0;
            }else{
                JSONObject jsonObject=new JSONObject(res);
                Log.i("Identify",res);
                JSONArray jsonArray=jsonObject.optJSONArray("results");
                if(jsonArray==null || jsonArray.length()==0){
                    ret=0.0;
                }else{
                    Double max=0.0;
                    for(int i=0;i<jsonArray.length();i++){
                        Double tmp=jsonArray.optJSONObject(i).optDouble("confidence");
                        if(tmp>max){
                            max=tmp;
                        }
                    }
                    ret=max;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ret=0.0;
        }
        return ret;
    }
}
