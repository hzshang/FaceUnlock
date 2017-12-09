package com.hzshang.faceunlock.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.lib.Async;
import com.hzshang.faceunlock.lib.Identify;

import java.io.ByteArrayOutputStream;

public class testActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ImageView imageView = (ImageView) findViewById(R.id.test_imageView);
        Bundle bundle = getIntent().getExtras();
        Bitmap face = BitmapFactory.decodeFile(bundle.getString("path"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        face.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        imageView.setImageBitmap(face);

    }
}