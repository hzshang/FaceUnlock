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
import com.example.hzshang.faceunlock.lib.AddFaceToUser;
import com.example.hzshang.faceunlock.lib.AddUserToGroup;
import com.example.hzshang.faceunlock.lib.DetectFace;
import com.example.hzshang.faceunlock.lib.Storage;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            readyForTakePic();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readyForTakePic();
                } else {
                    Dialog.showDialog(getString(R.string.error_camera_permission), this);
                    finish();
                }
        }
    }

    private void readyForTakePic() {
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
            finish();
        }
    }

    //button onListen function
    private void addNewUser() {
        String name = userName.getText().toString();
        if (!name.equals(""))
            detectFace();
        else
            Dialog.showDialog(getString(R.string.error_userName), AddUser.this);
    }

    private void addFace2User() {
        String groupId = Storage.getGroupId(this);
        if (groupId == null) {
            Log.e("addUser:addFace2User","groupId is null");
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        new AddFaceToUser(this, new AddFaceToUser.interFace<Boolean, String>() {
            @Override
            public void processFinish(Boolean out) {
                progressDialog.dismiss();
                if(out==true){
                    success();
                }else{
                    Dialog.showDialog(getString(R.string.error_network),AddUser.this);
                }
                finish();
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
            Log.e("addUser:addAUser","group ip is null");
            return;
        }
        new AddUserToGroup(this, new AddUserToGroup.interFace<String, String>() {
            @Override
            public void processFinish(String personId) {
                if (personId == null) {
                    progressDialog.dismiss();
                    Dialog.showDialog(getString(R.string.error_addUser), AddUser.this);
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

    //detect face location
    private void detectFace() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        new DetectFace(this, new DetectFace.interFace<Object[], String>() {
            @Override
            public void processFinish(Object[] out) {
                if ((boolean) out[0] == false) {
                    progressDialog.dismiss();
                    Dialog.showDialog((String) out[1], AddUser.this);
                } else {
                    upFace = (Face) out[1];
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

    private void success() {

        String name = userName.getText().toString();
        FaceRectangle rectangle = upFace.faceRectangle;
        Bitmap cropped = Bitmap.createBitmap(bitmap, rectangle.left, rectangle.top, rectangle.width, rectangle.height);
        if (Storage.addUserInLocal(this, name, tmp_personId, upFace.faceId.toString(), cropped)) {
            Dialog.showDialog(getString(R.string.add_user_success), this);
        } else {
            Dialog.showDialog(getString(R.string.error_addUser), this);
        }
    }

}
