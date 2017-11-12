package com.example.hzshang.faceunlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.hzshang.faceunlock.lib.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        List<String> strs = new ArrayList<String>();
        JSONArray users = Storage.getUsers(this);
        if (users != null) {
            for (int i = 0; i < users.length(); i++)
                try {
                    JSONObject tmp = (JSONObject) users.get(i);
                    strs.add(tmp.getString("name"));
                } catch (Exception e) {
                    continue;
                }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, strs);
            listView.setAdapter(adapter);
        }
    }

}
