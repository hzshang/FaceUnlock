package com.hzshang.faceunlock.lib;

/**
 * Created by hzshang on 2017/11/4.
 */

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.dialog.DialogMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Storage {
    private static SharedPreferences sharedPreferences = null;
    private static final String GROUPID_KEY="groupId";
    private static final String FACEID_KEY="user_id_key";
    private static final String PREFERENCE_KEY="com.hzshang.faceUnlock.Key";
    private static final String FACE_DIR="faceDir";
    private static final String NAME_KEY="_name";
    private static final String PIN_QUESTION="pin_question";
    private static final String PIN_PROTECT_ANSWER_TEXT="pin_protect_answer_text";
    private static final String PIN_SET="PIN_SET";
    private static final String GRAVITY_ENABLE="GRAVITY_ENABLE";
    // group id for every phone
    static public String getGroupId(Context context) {
        sharedPreferences = getSharedPreferences(context);
        String ret;
        if (!sharedPreferences.contains(GROUPID_KEY)) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            ret = createGroup(context);
            if (ret != null) {
                edit.putString(GROUPID_KEY, ret);
                edit.apply();
            }
        } else {
            ret = sharedPreferences.getString(GROUPID_KEY, null);
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
            public void processRunning(String process) {
                return;
            }
        });
        try {
            ret = addGroup.execute().get();
        } catch (Exception e) {
            DialogMessage.showDialog(context.getString(R.string.error_network), context);
            ret = null;
        }
        return ret;
    }

    //return array name,faceId,face  faceUrl==faceId
    static public List<Map<String, Object>> getUsers(Context context) {
        sharedPreferences = getSharedPreferences(context);
        Set<String> faceIds = sharedPreferences.getStringSet(FACEID_KEY, null);
        List<Map<String, Object>> array = null;
        if (faceIds != null) {
            array = new ArrayList<>();
            for (String faceId : faceIds) {
                Map<String, Object> tmp = new HashMap<>();
                String name = sharedPreferences.getString(faceId + NAME_KEY, "");
                File facePath = getFacePath(context, faceId);
                if (!facePath.exists()) {
                    Log.i("Storage", "face error");
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


    static public boolean addUserInLocal(Context context, String userName, String faceId, Bitmap face) {
        sharedPreferences = getSharedPreferences(context);

        //add faceId
        Set<String> faceIds = sharedPreferences.getStringSet(FACEID_KEY, null);
        if (faceIds == null) {
            faceIds = new HashSet<>();
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        faceIds.add(faceId);
        edit.putStringSet(FACEID_KEY, faceIds);
        //add userName
        edit.putString(faceId + NAME_KEY, userName);
        //use faceId as img file name
        boolean ret;
        try {
            File facePath = getFacePath(context, faceId);
            FileOutputStream fos;
            fos = new FileOutputStream(facePath);
            face.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.close();
            edit.apply();
            ret = true;
        } catch (Exception e) {
            Log.e("Storage","file error" + e.toString());
            ret = false;
        }
        return ret;
    }

    static private SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences ret;
        if (sharedPreferences == null) {
            ret = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        } else {
            ret = sharedPreferences;
        }
        return ret;
    }

    static private File getFacePath(Context context, String faceId) {
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir(FACE_DIR, Context.MODE_PRIVATE);
        return new File(dir, faceId);
    }
    //忘记密码后使用密保功能，由于会调用输入法，用户可能退出activity，弃用
    static public void setPinProtect(Context context, String question, String answer) {
        sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PIN_QUESTION,question);
        editor.putString(PIN_PROTECT_ANSWER_TEXT, answer);
        editor.apply();
    }

    static public boolean userIsEmpty(Context context) {
        sharedPreferences = getSharedPreferences(context);
        Set<String> faceIds = sharedPreferences.getStringSet(FACEID_KEY, null);
        return faceIds == null||faceIds.isEmpty();
    }

    static public String[] getPinProtect(Context context) {
        sharedPreferences = getSharedPreferences(context);
        String[] object = new String[2];
        object[0] = sharedPreferences.getString(PIN_QUESTION, null);
        object[1] = sharedPreferences.getString(PIN_PROTECT_ANSWER_TEXT, null);
        return object;
    }
//      检测是否设置密保，弃用
    static public boolean hasPinProtect(Context context) {
        sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.contains(PIN_QUESTION);
    }

    static public void deleteUserInLocal(Context context, String faceId) {
        sharedPreferences = getSharedPreferences(context);
        Set<String> faceIds = sharedPreferences.getStringSet(FACEID_KEY, null);
        if (faceIds != null){
            SharedPreferences.Editor edit = sharedPreferences.edit();
            //delete face Id
            faceIds.remove(faceId);
            edit.putStringSet(FACEID_KEY,faceIds);
            //delete name
            edit.remove(faceId+NAME_KEY);
            //delete face image
            getFacePath(context,faceId).delete();
            //commit
            edit.apply();
        }else{
            Log.e("Storage","faceset is null in deleteUserInLocal");
        }
    }
    //检查是否设置密码
    static public boolean isSetPwd(Context context){
        sharedPreferences=getSharedPreferences(context);
        return sharedPreferences.getBoolean(PIN_SET,false);
    }
    static public void setPwd(Context context){
        sharedPreferences=getSharedPreferences(context);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putBoolean(PIN_SET,true);
        edit.apply();
    }
//
//    static public void setLock(Context context,boolean isEnable){
//        sharedPreferences=getSharedPreferences(context);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putBoolean(LOCK_ENABLE,isEnable);
//        editor.apply();
//    }

    static public boolean getGravitySwitch(Context context){
        sharedPreferences=getSharedPreferences(context);
        return sharedPreferences.getBoolean(GRAVITY_ENABLE,false);
    }
    static public void setGravitySwitch(Context context,boolean isEnable){
        sharedPreferences=getSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(GRAVITY_ENABLE,isEnable);
        editor.apply();
    }

}
