package com.hzshang.faceunlock.HTTP;

import java.util.HashMap;

/**
 * Created by hzshang on 2017/12/15.
 */

public class FaceAPI {
    private final static String API_KEY_NAME="api_key";
    private final static String API_KEY_VALUE="pre_key";
    private final static String URL="http://face.hongze.fun:5002/";
    private final static String DETECT = "detect";
    private final static String SEARCH = "search";
    private final static String ADDFACE = "addface";
    private final static String CREATE = "create";
    private final static String DELETE = "removeface";
    private final static String IMAGE_FILE="image_file";
    private final static String FACE_TOKEN="face_token";
    private final static String FACESET_TOKEN="faceset_token";

    public static Response detect(byte[] face) throws Exception{
        String url=URL+DETECT;
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> fileMap = new HashMap<>();
        map.put(API_KEY_NAME,API_KEY_VALUE);
        fileMap.put(IMAGE_FILE,face);
        return HttpRequest.post(url,map,fileMap);
    }
    public static Response createSet() throws Exception{
        String url=URL+CREATE;
        HashMap<String,String> map=new HashMap<>();
        map.put(API_KEY_NAME,API_KEY_VALUE);
        return HttpRequest.post(url,map,null);
    }
    public static Response deleteFace(String set,String facdId) throws Exception{
        String url=URL+DELETE;
        HashMap<String,String> map=new HashMap<>();
        map.put(API_KEY_NAME,API_KEY_VALUE);
        map.put(FACESET_TOKEN,set);
        map.put(FACE_TOKEN,facdId);
        return HttpRequest.post(url,map,null);
    }
    public static Response search(byte[] face,String set)throws Exception{
        String url=URL+SEARCH;
        HashMap<String,String> map=new HashMap<>();
        HashMap<String,byte[]> fileMap=new HashMap<>();
        map.put(API_KEY_NAME,API_KEY_VALUE);
        map.put(FACESET_TOKEN,set);
        fileMap.put(IMAGE_FILE,face);
        return HttpRequest.post(url,map,fileMap);
    }
    public static Response addFace(String set,String faceId)throws Exception{
        String url=URL+ADDFACE;
        HashMap<String,String> map=new HashMap<>();
        map.put(API_KEY_NAME,API_KEY_VALUE);
        map.put(FACE_TOKEN,faceId);
        map.put(FACESET_TOKEN,set);
        return HttpRequest.post(url,map,null);
    }
}
