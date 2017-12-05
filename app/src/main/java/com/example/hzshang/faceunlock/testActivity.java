package com.example.hzshang.faceunlock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class testActivity extends AppCompatActivity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ImageView imageView=(ImageView)findViewById(R.id.test_imageView);
        Bundle bundle=getIntent().getExtras();
        Bitmap face=BitmapFactory.decodeFile(bundle.getString("path"));
        imageView.setImageBitmap(face);
    }

}