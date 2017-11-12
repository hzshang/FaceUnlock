package com.example.hzshang.faceunlock.lib;

/**
 * Created by hzshang on 2017/11/4.
 */

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Storage {
    static SharedPreferences sharedPreferences = null;

    static public String getGroupId(Context context) {
        sharedPreferences = getSharedPreferences(context);
        String ret;
        String groupIdKey = context.getString(R.string.group_id_key);
        if (!sharedPreferences.contains(groupIdKey)) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            String groupId = UUID.randomUUID().toString();
            edit.putString(groupIdKey, groupId);
            if (createGroup(context,groupId)) {
                edit.apply();
                ret = groupId;
            } else {
                ret = null;
            }
        } else {
            ret = sharedPreferences.getString(groupIdKey, null);
        }
        return ret;
    }

    private static boolean createGroup(Context context,String groupId) {
        String ret;
        AddGroup addGroup = new AddGroup(context,new AddGroup.interFace<String, String>() {
            @Override
            public void processFinish(String out) {
                return;
            }

            @Override
            public void processPre() {
                return;
            }

            @Override
            public void processRuning(String process) {
                return;
            }
        });
        try {
            ret = addGroup.execute(groupId).get();
        } catch (Exception e) {
            Dialog.showDialog(context.getString(R.string.error_network),context);
            ret = null;
        }
        return ret != null;
    }

    //return array userId,name,faceId,faceUrl
    static public JSONArray getUsers(Context context) {
        sharedPreferences = getSharedPreferences(context);
        JSONArray jsonArray=null;
        Set<String> userIds = sharedPreferences.getStringSet(context.getString(R.string.user_id_key), null);
        if (userIds != null) {
            jsonArray=new JSONArray();
            for (String userId : userIds) {
                JSONObject jsonObject= new JSONObject();
                String name = sharedPreferences.getString(userId + context.getString(R.string.user_name_key), "");
                String faceUrl = sharedPreferences.getString(userId + context.getString(R.string.user_face_url_key),"");
                String faceId=sharedPreferences.getString(userId+context.getString(R.string.user_faceId_key),"");
                try{
                    jsonObject.put("faceId", faceId);
                    jsonObject.put("name",name);
                    jsonObject.put("userId",userId);
                    jsonObject.put("faceUrl",faceUrl);
                    jsonArray.put(jsonObject);
                }catch (Exception e){
                    continue;
                }
            }
        }
        return jsonArray;

    }

    public static boolean addUserInLocal(Context context, String userName, String userId, String faceId, Bitmap face) {
        sharedPreferences = getSharedPreferences(context);
        String userKey = context.getString(R.string.user_id_key);
        //add faceId
        Set<String> userIds = sharedPreferences.getStringSet(userKey, null);
        if (userIds == null) {
            userIds = new HashSet<>();
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        userIds.add(userId);
        edit.putStringSet(userKey, userIds);
        //add userName
        edit.putString(userId + context.getString(R.string.user_name_key), userName);
        //add faceId
        edit.putString(userId + context.getString(R.string.user_faceId_key), faceId);
        //use faceId as img file name
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File dir = cw.getDir(context.getString(R.string.user_face_dir_key), Context.MODE_PRIVATE);
            File facePath = new File(dir, faceId);
            FileOutputStream fos;
            fos = new FileOutputStream(facePath);
            face.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            edit.putString(faceId + context.getString(R.string.user_face_url_key), facePath.getAbsolutePath());
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static private SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences ret;
        if (sharedPreferences == null) {
            ret = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        } else {
            ret = sharedPreferences;
        }
        return ret;
    }
}
