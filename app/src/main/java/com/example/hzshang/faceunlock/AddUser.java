package com.example.hzshang.faceunlock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hzshang.faceunlock.common.Dialog;
import com.example.hzshang.faceunlock.lib.AddFaceToUser;
import com.example.hzshang.faceunlock.lib.AddUserToGroup;
import com.example.hzshang.faceunlock.lib.DetectFace;
import com.example.hzshang.faceunlock.lib.Storage;

import com.microsoft.projectoxford.face.contract.Face;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddUser extends AppCompatActivity {
    private EditText userName;
    private ImageView imageView;
    private Button confirmBtn;
    private Button remakeBtn;
    private Bitmap bitmap;
    private Face upFace;
    private String tmp_personId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        imageView = (ImageView) findViewById(R.id.photo_view);
        confirmBtn = (Button) findViewById(R.id.confirm);
        remakeBtn = (Button) findViewById(R.id.remake);
        userName = (EditText) findViewById(R.id.editText);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUser();
            }
        });
        progressDialog = new ProgressDialog(this);
        remakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //take picture
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 0);
        } else {
            showMsg(getString(R.string.error_camera));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
        } else {
            showMsg(getString(R.string.error_camera));
            finish();
        }
    }

    //btn onListen function
    private void addNewUser() {
        String name = userName.getText().toString();
        if (!name.equals(""))
            detectFace();
        else
            showMsg(getString(R.string.error_userName));
    }

    private void addFace2User() {
        String groupId = Storage.getGroupId(this);
        if (groupId == null) {
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        new AddFaceToUser(this,new AddFaceToUser.interFace<Boolean, String>() {
            @Override
            public void processFinish(Boolean out) {
                progressDialog.dismiss();
                if (out) {
                    success();
                    finish();
                }else{
                    showMsg(getString(R.string.error_face));
                }
            }

            @Override
            public void processPre() {
                return;
            }

            @Override
            public void processRuning(String progress) {
                progressDialog.setMessage(progress);
            }
        }).execute(groupId, UUID.fromString(tmp_personId), inputStream, upFace);
    }


    private void addAUser() {
        String groupId = Storage.getGroupId(this);
        if (groupId == null) {
            return;
        }
        new AddUserToGroup(this,new AddUserToGroup.interFace<String, String>() {
            @Override
            public void processFinish(String personId) {
                if (personId == null) {
                    progressDialog.dismiss();
                    showMsg(getString(R.string.error_face));
                } else {
                    tmp_personId = personId;
                    addFace2User();
                }
            }

            @Override
            public void processPre() {
                return;
            }

            @Override
            public void processRuning(String progress) {
                progressDialog.setMessage(progress);
            }
        }).execute(groupId, userName.getText().toString());
    }


    private void detectFace() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        new DetectFace(this,new DetectFace.interFace<Face, String>() {
            @Override
            public void processFinish(Face out) {
                if (out == null) {
                    progressDialog.dismiss();
                    showMsg(getString(R.string.error_face));
                } else {
                    upFace = out;
                    addAUser();
                }
            }

            @Override
            public void processPre() {
                progressDialog.show();
            }

            @Override
            public void processRuning(String progress) {
                progressDialog.setMessage(progress);
            }
        }).execute(inputStream);
    }

    private void showMsg(String out){
        Dialog.showDialog(out,this);
    }
    private void success() {
        Dialog.showDialog(getString(R.string.add_user_success), this);
        String name = userName.getText().toString();
        if (!Storage.addUserInLocal(this,name,tmp_personId,upFace.faceId.toString(),  bitmap)) {
            Dialog.showDialog(getString(R.string.error_addUser),this);
            //delete user added ?
        }
    }
}
