package com.example.hzshang.faceunlock.lib;

import android.content.Context;

import com.example.hzshang.faceunlock.R;
import com.example.hzshang.faceunlock.common.Dialog;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

/**
 * Created by hzshang on 2017/11/4.
 */

public class DetectFace extends Async<InputStream,String,Object[]>{

    public DetectFace(Context context,interFace delegate) {
        super(context,delegate);
    }

    @Override
    protected  Object[] doInBackground(InputStream... params){

        FaceServiceClient faceServiceClient = App.getFaceServiceClient();
        Object[] ret=new Object[2];
        try {
            publishProgress(context.getString(R.string.detect));
            // Start detection.
            Face faces[]=faceServiceClient.detect(
                     params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[] {
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.FacialHair,
                            FaceServiceClient.FaceAttributeType.Emotion,
                            FaceServiceClient.FaceAttributeType.HeadPose,
                            FaceServiceClient.FaceAttributeType.Accessories,
                            FaceServiceClient.FaceAttributeType.Blur,
                            FaceServiceClient.FaceAttributeType.Exposure,
                            FaceServiceClient.FaceAttributeType.Hair,
                            FaceServiceClient.FaceAttributeType.Makeup,
                            FaceServiceClient.FaceAttributeType.Noise,
                            FaceServiceClient.FaceAttributeType.Occlusion
                    });
            if(faces.length==1){
                ret[0]=true;
                ret[1]=faces[0];
            }
            else if(faces.length>1){
                ret[0]=false;
                ret[1]=context.getString(R.string.error_to_many_faces);
            }else{
                ret[0]=false;
                ret[1]=context.getString(R.string.error_detect_face);
            }
        } catch (Exception e) {
            ret[0]=false;
            ret[1]=context.getString(R.string.error_network);
        }
        return ret;
    }
}
