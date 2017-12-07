package com.hzshang.faceunlock.lib;

import org.json.JSONObject;

/**
 * Created by hzshang on 2017/12/5.
 */

public class Face {
    public String faceToken;
    public int top;
    public int left;
    public int width;
    public int height;
    public Face(JSONObject jsonObject){
        faceToken=jsonObject.optString("face_token");
        JSONObject face_rectangle=jsonObject.optJSONObject("face_rectangle");
        top=face_rectangle.optInt("top");
        left=face_rectangle.optInt("left");
        width=face_rectangle.optInt("width");
        height=face_rectangle.optInt("height");
    }
}
