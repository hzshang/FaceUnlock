package com.example.hzshang.faceunlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.hzshang.faceunlock.lib.Storage;

import java.util.List;
import java.util.Map;

public class ManagerUser extends AppCompatActivity {
    private LinearLayout linearLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);
        linearLayout = (LinearLayout) findViewById(R.id.menu_add_user);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManagerUser.this, AddUser.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.mgr_user);
        showUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUsers();
    }

    //wait to better
    private void showUsers() {
        List<Map<String, Object>> users=Storage.getUsers(this);
        if(users!=null){
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, users, R.layout.user, new String[]{"name", "faceUrl"}, new int[]{R.id.name, R.id.face});
            listView.setAdapter(simpleAdapter);
        }
    }
}
