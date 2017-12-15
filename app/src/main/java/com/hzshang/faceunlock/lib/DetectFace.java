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

public class DetectFace extends Async<byte[], String, Object[]> {

    public DetectFace(Context context, interFace delegate) {
        super(context, delegate);
    }

    @Override
    protected Object[] doInBackground(byte[]... params) {

        Object[] ret = new Object[2];
        try {
            publishProgress(context.getString(R.string.detect));
            // Start detection.

            Response response = FaceAPI.detect(params[0]);
            if (response.getStatus() != 200) {
                ret[0] = false;
                ret[1] = context.getString(R.string.error_network);
            } else {
                String res = new String(response.getContent());
                JSONObject jsonObject = new JSONObject(res);
                Log.i("DetectFace",res);
                int length = jsonObject.optJSONArray("faces").length();
                switch (length) {
                    case 1:
                        ret[0] = true;
                        ret[1] = new Face(jsonObject.optJSONArray("faces").optJSONObject(0));
                        break;
                    case 0:
                        ret[0]=false;
                        ret[1]=context.getString(R.string.error_no_face);
                        break;
                    default:
                        ret[0] = false;
                        ret[1] = context.getString(R.string.error_too_many_faces);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret[0] = false;
            ret[1] = context.getString(R.string.error_network);
            Log.i("DetectFace","net error");
        }
        return ret;
    }
}
