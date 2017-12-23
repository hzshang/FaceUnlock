package com.hzshang.faceunlock.HTTP;

/**
 * Created by hzshang on 2017/12/15.
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;


public class HttpRequest {
    private static final int CONNECT_TIME_OUT = 5000;
    private static final int READ_OUT_TIME = 5000;
    private static String boundaryString = getBoundary();

    static Response post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        URL url1 = new URL(url);
        HttpURLConnection conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());

        Iterator iter = map.entrySet().iterator();
        Entry ins;
        while (iter.hasNext()) {
            ins = (Entry) iter.next();
            String key = (String) ins.getKey();
            String value = (String) ins.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }

        if (fileMap != null && fileMap.size() > 0) {
            Iterator fileIter = fileMap.entrySet().iterator();

            while (fileIter.hasNext()) {
                Entry<String, byte[]> fileEntry = (Entry) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + (String) fileEntry.getKey() + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }

        obos.writeBytes("--" + boundaryString + "--\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        int code = conne.getResponseCode();

        InputStream ins1;
        try {
            if (code == 200) {
                ins1 = conne.getInputStream();
            } else {
                ins1 = conne.getErrorStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];

        int len;
        while ((len = ins1.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }

        byte[] bytes = baos.toByteArray();
        Response response = new Response(bytes, code);
        ins1.close();
        return response;
    }

    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }

        return sb.toString();
    }

    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
