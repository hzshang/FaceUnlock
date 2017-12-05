package com.example.hzshang.faceunlock;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hzshang.faceunlock.common.Dialog;
import com.example.hzshang.faceunlock.lib.AddFaceToGroup;
import com.example.hzshang.faceunlock.lib.DetectFace;
import com.example.hzshang.faceunlock.lib.Face;
import com.example.hzshang.faceunlock.lib.Storage;

import java.io.ByteArrayOutputStream;

public class AddUser extends AppCompatActivity {
    private EditText userName;
    private ImageView imageView;
    private Button confirmBtn;
    private Button remakeBtn;
    private Bitmap bitmap;
    private Face upFace;
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
        //already asked permission
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.resolveActivity(getPackageManager());
        startActivityForResult(takePictureIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e("AddUser","add camera fail");
            finish();
        }
    }

    //detect face location
    private void detectFace() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] byteArray = output.toByteArray();

        new DetectFace(this, new DetectFace.interFace<Object[], String>() {
            @Override
            public void processFinish(Object[] out) {
                if (!(boolean) out[0]) {
                    progressDialog.dismiss();
                    Dialog.showDialog((String) out[1], AddUser.this);
                } else {
                    upFace = (Face) out[1];
                    addFace2Group();
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
        }).execute(byteArray);
    }

    //button onListen function
    private void addNewUser() {
        String name = userName.getText().toString();
        if (!name.equals(""))
            detectFace();
        else
            Dialog.showDialog(getString(R.string.error_userName), AddUser.this);
    }

    private void addFace2Group() {
        String groupId = Storage.getGroupId(this);
        if (groupId == null) {
            Log.e("addUser:addAUser", "group ip is null");
            return;
        }
        new AddFaceToGroup(this, new AddFaceToGroup.interFace<Object[], String>() {
            @Override
            public void processFinish(Object[] ret) {
                progressDialog.dismiss();
                if (!(boolean)ret[0]) {
                    Dialog.showDialog((String) ret[1], AddUser.this);
                } else {
                    success();
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
        }).execute(upFace.faceToken,groupId);
    }



    private void success() {

        String name = userName.getText().toString();
        Bitmap cropped = Bitmap.createBitmap(bitmap, upFace.left, upFace.top, upFace.width, upFace.height);
        if (Storage.addUserInLocal(this, name, upFace.faceToken, cropped)) {
            Dialog.showDialog(getString(R.string.add_user_success), this);
        } else {
            Dialog.showDialog(getString(R.string.error_add_user), this);
        }
        finish();
    }
}