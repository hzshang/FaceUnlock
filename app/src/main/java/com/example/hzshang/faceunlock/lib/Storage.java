package com.example.hzshang.faceunlock.lib;

/**
 * Created by hzshang on 2017/11/4.
 */

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Storage {
    static SharedPreferences sharedPreferences = null;
    // group id for every phone
    static public String getGroupId(Context context) {
        sharedPreferences = getSharedPreferences(context);
        String ret;
        String groupIdKey = context.getString(R.string.group_id_key);
        if (!sharedPreferences.contains(groupIdKey)) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            ret=createGroup(context);
            if(ret!=null){
                edit.putString(groupIdKey,ret);
                edit.apply();
                Log.i("Storage","add Group Fail");
            }
        } else {
            ret = sharedPreferences.getString(groupIdKey, null);
        }
        return ret;
    }
    // init group id for first use
    private static String createGroup(Context context) {
        String ret;
        AddGroup addGroup = new AddGroup(context, new AddGroup.interFace<String, String>() {
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
            ret = addGroup.execute().get();
        } catch (Exception e) {
            Dialog.showDialog(context.getString(R.string.error_network), context);
            ret = null;
        }
        return ret;
    }

    //return array name,faceId,face  faceUrl==faceId
    static public List<Map<String, Object>> getUsers(Context context) {
        sharedPreferences = getSharedPreferences(context);
        Set<String> faceIds = sharedPreferences.getStringSet(context.getString(R.string.face_id_key), null);
        List<Map<String, Object>> array = null;
        if (faceIds != null) {
            array = new ArrayList<>();
            for (String faceId : faceIds) {
                Map<String, Object> tmp = new HashMap<>();
                String name = sharedPreferences.getString(faceId + context.getString(R.string.user_name_key), "");
                File facePath = getFacePath(context, faceId);
                if (!facePath.exists()) {
                    deleteUser();
                    continue;
                }
                tmp.put("name", name);
                tmp.put("faceId", faceId);
                tmp.put("faceUrl", facePath);
                array.add(tmp);
            }
        }
        return array;
    }

    private static void deleteUser() {
        //TODO:delete user
        return;
    }

    static public boolean addUserInLocal(Context context, String userName, String faceId, Bitmap face) {
        sharedPreferences = getSharedPreferences(context);
        String faceKey= context.getString(R.string.face_id_key);
        //add faceId
        Set<String> faceIds = sharedPreferences.getStringSet(faceKey, null);
        if (faceIds == null) {
            faceIds = new HashSet<>();
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        faceIds.add(faceId);
        edit.putStringSet(faceKey, faceIds);
        //add userName
        edit.putString(faceId + context.getString(R.string.user_name_key), userName);
        //use faceId as img file name
        boolean ret;
        try {
            File facePath = getFacePath(context, faceId);
            FileOutputStream fos;
            fos = new FileOutputStream(facePath);
            face.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            edit.apply();
            ret = true;
        } catch (Exception e) {
            Log.e("file error", e.toString());
            ret = false;
        }
        return ret;
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

    static private File getFacePath(Context context, String faceId) {
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir(context.getString(R.string.user_face_dir_key), Context.MODE_PRIVATE);
        return new File(dir, faceId);
    }
    static public void setPinProtect(Context context,int index,String answer){
        sharedPreferences=getSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pin_question_index),index);
        editor.putString(context.getString(R.string.pin_protect_answer_text),answer);
        editor.apply();
    }
    static public Object[] getPinProtect(Context context){
        sharedPreferences=getSharedPreferences(context);
        Object[] object=new Object[2];
        object[0]=sharedPreferences.getInt(context.getString(R.string.pin_question_index),-1);
        object[1]=sharedPreferences.getString(context.getString(R.string.pin_protect_answer_text),null);
        return object;
    }
    static public boolean hasPinProtect(Context context){
        sharedPreferences=getSharedPreferences(context);
        return sharedPreferences.contains(context.getString(R.string.pin_question_index));
    }

}
